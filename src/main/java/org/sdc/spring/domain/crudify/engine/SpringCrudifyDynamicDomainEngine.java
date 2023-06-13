package org.sdc.spring.domain.crudify.engine;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.reflections.Reflections;
import org.sdc.spring.domain.crudify.business.ISpringCrudifyBusiness;
import org.sdc.spring.domain.crudify.connector.ISpringCrudifyConnector;
import org.sdc.spring.domain.crudify.controller.ISpringCrudifyController;
import org.sdc.spring.domain.crudify.controller.SpringCrudifyEngineController;
import org.sdc.spring.domain.crudify.events.ISpringCrudifyEventPublisher;
import org.sdc.spring.domain.crudify.repository.ISpringCrudifyRepository;
import org.sdc.spring.domain.crudify.repository.SpringCrudifyEngineRepository;
import org.sdc.spring.domain.crudify.repository.dao.ISpringCrudifyDAORepository;
import org.sdc.spring.domain.crudify.repository.dao.SpringCrudifyDao;
import org.sdc.spring.domain.crudify.repository.dao.mongodb.SpringCrudifyEngineMongoRepository;
import org.sdc.spring.domain.crudify.repository.dto.ISpringCrudifyDTOObject;
import org.sdc.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.sdc.spring.domain.crudify.spec.SpringCrudifyEntity;
import org.sdc.spring.domain.crudify.spec.SpringCrudifyReadOutputMode;
import org.sdc.spring.domain.crudify.ws.AbstractSpringCrudifyService;
import org.sdc.spring.domain.crudify.ws.ISpringCrudifyRestService;
import org.sdc.spring.domain.crudify.ws.SpringCrudifyEngineRestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPatternParser;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class SpringCrudifyDynamicDomainEngine implements ISpringCrudifyDynamicDomainEngine {

	@Inject
	protected Optional<MongoTemplate> mongo;
	
	@Inject 
	protected ApplicationContext context;

	@Value("${spring.domain.crudify.magicTenantId}")
	protected String magicTenantId;
	
	@Value("${spring.domain.crudify.engine.packages}")
	protected String[] scanPackages;

	@Inject
	private RequestMappingHandlerMapping requestMappingHandlerMapping;
	
	@Inject
    public OpenAPI openApi;

	private List<ISpringCrudifyRestService<?>> services;
	
	private Map<String, ISpringCrudifyDAORepository<?>> daos = new HashMap<String, ISpringCrudifyDAORepository<?>>();
	private Map<String, ISpringCrudifyRepository<?>> repositries = new HashMap<String, ISpringCrudifyRepository<?>>();
	private Map<String, ISpringCrudifyController<?>> controllers = new HashMap<String, ISpringCrudifyController<?>>();
	private Map<String, ISpringCrudifyRestService<?>> restServices = new HashMap<String, ISpringCrudifyRestService<?>>();

	private SpringCrudifyOpenAPIHelper openApiHelper;
	
	@Override
	public ISpringCrudifyDAORepository<?> getDao(String name){
		return this.daos.get(name);
	}
	
	@Override
	public ISpringCrudifyRepository<?> getRepository(String name){
		return this.repositries.get(name);
	}
	
	@Override
	public ISpringCrudifyController<?> getController(String name){
		return this.controllers.get(name);
	}
	
	@Override
	public ISpringCrudifyRestService<?> getService(String name){
		return this.restServices.get(name);
	}
	
	@SuppressWarnings("unchecked")
	@Bean
	protected List<ISpringCrudifyRestService<?>> engineServices() throws SpringCrudifyEngineException {
		
		this.openApiHelper = new SpringCrudifyOpenAPIHelper();

		log.info("============================================");
		log.info("======                                ======");
		log.info("====== Starting Dynamic Domain Engine ======");
		log.info("======                                ======");
		log.info("============================================");
		log.info("Version: {}", this.getClass().getPackage().getImplementationVersion());
		
		this.services = new ArrayList<ISpringCrudifyRestService<?>>();
		
		for (String pack : this.scanPackages) {
			log.info("Scanning package "+ pack);
			
			Reflections reflections = new Reflections(pack);
			
			Set<Class<?>> entities__ = reflections.getTypesAnnotatedWith(SpringCrudifyEntity.class);

			for (Class<?> clazz : entities__) {
			
				Class<?> entityClass = clazz;
				Class<?> dtoClass = null;
				
				if (!ISpringCrudifyEntity.class.isAssignableFrom(entityClass)) {
					throw new SpringCrudifyEngineException( "The class [" + entityClass.getName() + "] must implements the ISpringCrudifyEntity interface.");
				}

				SpringCrudifyEntity entityAnnotation = clazz.getAnnotation(SpringCrudifyEntity.class);
				
				boolean authorize_creation = entityAnnotation.authorize_creation();
				boolean authorize_read_all = entityAnnotation.authorize_read_all();
				boolean authorize_read_one = entityAnnotation.authorize_read_one();
				boolean authorize_update_one = entityAnnotation.authorize_update_one();
				boolean authorize_delete_one = entityAnnotation.authorize_delete_one();
				boolean authorize_delete_all = entityAnnotation.authorize_delete_all();
				boolean authorize_count = entityAnnotation.authorize_count();
				
				try {
					dtoClass = Class.forName(entityAnnotation.dto());
				} catch (ClassNotFoundException e) {
					throw new SpringCrudifyEngineException(e);
				}
				
				if (!ISpringCrudifyDTOObject.class.isAssignableFrom(dtoClass)) {
					throw new SpringCrudifyEngineException("The class [" + dtoClass.getName() + "] must implements the ISpringCrudifyDTOObject interface.");
				}
				
				SpringCrudifyDao db = entityAnnotation.db();
				
				// Web Service
				String ws__ = entityAnnotation.ws();
				ISpringCrudifyRestService<ISpringCrudifyEntity> ws = null;
				
				if( ws__ != null && !ws__.isEmpty() ) {					
					ws = (ISpringCrudifyRestService<ISpringCrudifyEntity>) this.getObjectFromConfiguration(ws__, ISpringCrudifyRestService.class);
				}
				
				// Controller
				String controller__ = entityAnnotation.controller();
				ISpringCrudifyController<ISpringCrudifyEntity> controller = null;
				
				if( controller__ != null && !controller__.isEmpty() ) {					
					controller = (ISpringCrudifyController<ISpringCrudifyEntity>) this.getObjectFromConfiguration(controller__, ISpringCrudifyController.class);
				}
				
				// Event Publisher
				String event__ = entityAnnotation.eventPublisher();
				ISpringCrudifyEventPublisher event = null;
				
				if( event__ != null && !event__.isEmpty() ) {					
					event = (ISpringCrudifyEventPublisher) this.getObjectFromConfiguration(event__, ISpringCrudifyEventPublisher.class);
				}

				// Business
				String business__ = entityAnnotation.business();
				ISpringCrudifyBusiness<ISpringCrudifyEntity> business = null;
				
				if( business__ != null && !business__.isEmpty() ) {					
					business = (ISpringCrudifyBusiness<ISpringCrudifyEntity>) this.getObjectFromConfiguration(business__, ISpringCrudifyBusiness.class);
				}
				
				// Connector 
				String connector__ = entityAnnotation.connector();
				ISpringCrudifyConnector<ISpringCrudifyEntity, List<ISpringCrudifyEntity>> connector = null;
				
				if( connector__ != null && !connector__.isEmpty() ) {					
					connector = (ISpringCrudifyConnector<ISpringCrudifyEntity, List<ISpringCrudifyEntity>>) this.getObjectFromConfiguration(connector__, ISpringCrudifyConnector.class);
				}
				
				// Repository
				String repo__ = entityAnnotation.repository();
				ISpringCrudifyRepository<ISpringCrudifyEntity> repo = null;
				
				if( repo__ != null && !repo__.isEmpty() ) {					
					repo = (ISpringCrudifyRepository<ISpringCrudifyEntity>) this.getObjectFromConfiguration(repo__, ISpringCrudifyRepository.class);
				}
				
				//DAO
				String dao__ = entityAnnotation.repository();
				ISpringCrudifyDAORepository<ISpringCrudifyDTOObject<ISpringCrudifyEntity>> dao = null;
				
				if( dao__ != null && !dao__.isEmpty() ) {					
					dao = (ISpringCrudifyDAORepository<ISpringCrudifyDTOObject<ISpringCrudifyEntity>>) this.getObjectFromConfiguration(dao__, ISpringCrudifyDAORepository.class);
				}

				try {
					this.createDynamicDomain(this.services, entityClass, dtoClass, db, ws, controller, business, event, connector, repo, dao, authorize_creation, authorize_read_all, authorize_read_one, authorize_update_one, authorize_delete_one, authorize_delete_all, authorize_count);
				} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | IOException e) {
					throw new SpringCrudifyEngineException(e);
				}
			}
		}
		
		return services;
	}


	private Object getObjectFromConfiguration(String objectName, Class<?> superClass) throws SpringCrudifyEngineException {
		Object obj = null; 
		
		String[] splits = objectName.split(":");
		Class<?> objClass;
		try {
			objClass = Class.forName(splits[1]);
		} catch (ClassNotFoundException e1) {
			throw new SpringCrudifyEngineException(e1);
		}
		
		if (!superClass.isAssignableFrom(objClass)) {
			throw new SpringCrudifyEngineException("The class [" + objClass.getName() + "] must implements the ["+superClass.getCanonicalName()+"] interface.");
		}

		switch(splits[0]) {
		case "bean":
			obj = this.context.getBean(objClass);
			break;
		case "class":
			try {
				Constructor<?> ctor = objClass.getConstructor();
				obj = ctor.newInstance();
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new SpringCrudifyEngineException(e);
			}
			break;
		default:
			throw new SpringCrudifyEngineException("Invalid controller "+objectName+", should be bean: or class:");
		}
		
		
		return obj;
	}

	/**
	 * 
	 * @param services
	 * @param entityClass
	 * @param dtoClass
	 * @param db
	 * @param controller2 
	 * @param ws 
	 * @param event 
	 * @param dynamicController
	 * @param connector
	 * @param repo
	 * @param dao
	 * @param authorize_creation
	 * @param authorize_read_all
	 * @param authorize_read_one
	 * @param authorize_update_one
	 * @param authorize_delete_one
	 * @param authorize_delete_all
	 * @param authorize_count
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void createDynamicDomain(List<ISpringCrudifyRestService<?>> services, Class<?> entityClass, Class<?> dtoClass, SpringCrudifyDao db, ISpringCrudifyRestService<ISpringCrudifyEntity> ws, ISpringCrudifyController<ISpringCrudifyEntity> controller, ISpringCrudifyBusiness<ISpringCrudifyEntity> business, ISpringCrudifyEventPublisher event, ISpringCrudifyConnector<ISpringCrudifyEntity, List<ISpringCrudifyEntity>> connector, ISpringCrudifyRepository<ISpringCrudifyEntity> repo, ISpringCrudifyDAORepository<ISpringCrudifyDTOObject<ISpringCrudifyEntity>> dao, boolean authorize_creation, boolean authorize_read_all, boolean authorize_read_one, boolean authorize_update_one, boolean authorize_delete_one, boolean authorize_delete_all, boolean authorize_count) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		
		/*
		 * TODO: THIS METHOD NEEDS TO BE REFACTORED
		 */
		
		log.info("Creating Dynamic Domain [Entity [{}], DTO [{}], DB [{}], authorize_creation [{}], authorize_read_all [{}], authorize_read_one [{}], authorize_update_one [{}], authorize_delete_one [{}], authorize_delete_all [{}], authorize_count [{}]]",
				entityClass.getCanonicalName(), 
				dtoClass.getCanonicalName(), 
				db,
				authorize_creation,
				authorize_read_all,
				authorize_read_one,
				authorize_update_one,
				authorize_delete_one,
				authorize_delete_all,
				authorize_count);
		
		Optional<ISpringCrudifyConnector<ISpringCrudifyEntity, List<ISpringCrudifyEntity>>> connectorObj = Optional.ofNullable(((ISpringCrudifyConnector<ISpringCrudifyEntity, List<ISpringCrudifyEntity>>) connector));
		Optional<ISpringCrudifyBusiness<ISpringCrudifyEntity>> businessObj = Optional.ofNullable(((ISpringCrudifyBusiness<ISpringCrudifyEntity>) business));
		Optional<ISpringCrudifyEventPublisher> eventObj = Optional.ofNullable(event);
		
		
		if( dao == null ) {	
			switch(db) {
			default:
			case mongo:
				dao = new SpringCrudifyEngineMongoRepository(dtoClass, this.mongo.get(), this.magicTenantId);
				break;
			}
		} else {
			dao.setDtoClass(dtoClass);
			dao.setMagicTenantId(this.magicTenantId);
		}
		
		if( repo == null ) {
			repo = new SpringCrudifyEngineRepository(entityClass, dtoClass, dao);
		} else {
			repo.setDao(dao);
			repo.setEntityClass(entityClass);
			repo.setEntityClass(dtoClass);
		}
		
		Optional<ISpringCrudifyRepository<ISpringCrudifyEntity>> repoObj = Optional.ofNullable(repo);
		
		if( controller == null ) {
			controller = new SpringCrudifyEngineController(entityClass, repoObj, connectorObj, businessObj, eventObj);
		} else {
			controller.setEntityClass(entityClass);
			controller.setRepository((Optional<?>) repoObj);
			controller.setConnector(connectorObj);
			controller.setbusiness(businessObj);
			controller.setEventPublisher(eventObj);
		}
		
		if( ws == null ) {
			ws = new SpringCrudifyEngineRestService(entityClass, (ISpringCrudifyController<ISpringCrudifyEntity>) controller, authorize_creation, authorize_read_all, authorize_read_one, authorize_update_one, authorize_delete_one, authorize_delete_all, authorize_count);
		} else {
			ws.setEntityClass(entityClass);
			ws.setController(controller);
			ws.authorize(authorize_creation, authorize_read_all, authorize_read_one, authorize_update_one, authorize_delete_one, authorize_delete_all, authorize_count);
		}

		String domain = ws.getDomain();
		
		services.add(ws);
		this.daos.put(domain.toLowerCase()+"_dao", dao);
		this.repositries.put(domain.toLowerCase()+"_repository", repo);
		this.controllers.put(domain.toLowerCase()+"_controller", controller);
		this.restServices.put(domain.toLowerCase()+"_service", ws);
		
		String baseUrl = "/"+ws.getDomain().toLowerCase();
		
		RequestMappingInfo.BuilderConfiguration options = new RequestMappingInfo.BuilderConfiguration();
		options.setPatternParser(new PathPatternParser());
		
		RequestMappingInfo requestMappingInfoGetAll = RequestMappingInfo.paths(baseUrl).methods(RequestMethod.GET).options(options).build();
		RequestMappingInfo requestMappingInfoDeleteAll = RequestMappingInfo.paths(baseUrl).methods(RequestMethod.DELETE).options(options).build();
		RequestMappingInfo requestMappingInfoCreate = RequestMappingInfo.paths(baseUrl).methods(RequestMethod.POST).options(options).build();
		RequestMappingInfo requestMappingInfoCount = RequestMappingInfo.paths(baseUrl+"/count").methods(RequestMethod.GET).options(options).build();
		RequestMappingInfo requestMappingInfoGetOne = RequestMappingInfo.paths(baseUrl+"/{uuid}").methods(RequestMethod.GET).options(options).build();
		RequestMappingInfo requestMappingInfoUpdate = RequestMappingInfo.paths(baseUrl+"/{uuid}").methods(RequestMethod.PATCH).options(options).build();
		RequestMappingInfo requestMappingInfoDeleteOne = RequestMappingInfo.paths(baseUrl+"/{uuid}").methods(RequestMethod.DELETE).options(options).build();
		
		Tag tag = new Tag().name("Domain "+ws.getDomain().toLowerCase());
		this.openApi.addTagsItem(tag);
	
		SpringCrudifyEntity entityAnnotation = ((Class<ISpringCrudifyEntity>) entityClass).getAnnotation(SpringCrudifyEntity.class);

		OpenAPI templateOpenApi = this.openApiHelper.getOpenApi(domain.toLowerCase(), entityClass.getSimpleName(), entityAnnotation.openApiSchemas());
		PathItem pathItemBase = new PathItem();
		PathItem pathItemCount = new PathItem();
		PathItem pathItemUuid = new PathItem();
		
		this.openApi.getComponents().addSchemas(entityClass.getSimpleName(), templateOpenApi.getComponents().getSchemas().get(entityClass.getSimpleName()));
		this.openApi.getComponents().addSchemas("ErrorObject", templateOpenApi.getComponents().getSchemas().get("ErrorObject"));
		this.openApi.getComponents().addSchemas("SortQuery", templateOpenApi.getComponents().getSchemas().get("SortQuery"));
		this.openApi.getComponents().addSchemas("FilterQuery", templateOpenApi.getComponents().getSchemas().get("FilterQuery"));

		if( authorize_read_all ) {
			this.requestMappingHandlerMapping.registerMapping(requestMappingInfoGetAll, ws, ws.getClass().getMethod("getEntities", String.class, SpringCrudifyReadOutputMode.class, Integer.class, Integer.class, String.class, String.class));
			this.openApi.path(baseUrl, pathItemBase.get(templateOpenApi.getPaths().get(baseUrl).getGet()));
		}
		if( authorize_delete_all ) {
			this.requestMappingHandlerMapping.registerMapping(requestMappingInfoDeleteAll, ws, ws.getClass().getMethod("deleteAll", String.class));
			this.openApi.path(baseUrl,pathItemBase.delete(templateOpenApi.getPaths().get(baseUrl).getDelete()));
		}
		if( authorize_creation ) {
			this.requestMappingHandlerMapping.registerMapping(requestMappingInfoCreate, ws, ws.getClass().getMethod("createEntity", String.class, String.class));
			this.openApi.path(baseUrl, pathItemBase.post(templateOpenApi.getPaths().get(baseUrl).getPost()));
		}
		if( authorize_count ) {
			this.requestMappingHandlerMapping.registerMapping(requestMappingInfoCount, ws, ws.getClass().getMethod("getCount", String.class));
			this.openApi.path(baseUrl+"/count", pathItemCount.get(templateOpenApi.getPaths().get(baseUrl+"/count").getGet()));
		}
		if( authorize_read_one ) {
			this.requestMappingHandlerMapping.registerMapping(requestMappingInfoGetOne, ws, ws.getClass().getMethod("getEntity", String.class, String.class));
			this.openApi.path(baseUrl+"/{uuid}", pathItemUuid.get(templateOpenApi.getPaths().get(baseUrl+"/{uuid}").getGet()));
		}
		if( authorize_update_one ) {
			this.requestMappingHandlerMapping.registerMapping(requestMappingInfoUpdate, ws, ws.getClass().getMethod("updateEntity", String.class, String.class, String.class));
			this.openApi.path(baseUrl+"/{uuid}", pathItemUuid.patch(templateOpenApi.getPaths().get(baseUrl+"/{uuid}").getPatch()));
		}
		if( authorize_delete_one ) {
			this.requestMappingHandlerMapping.registerMapping(requestMappingInfoDeleteOne, ws, ws.getClass().getMethod("deleteEntity", String.class, String.class));
			this.openApi.path(baseUrl+"/{uuid}", pathItemUuid.delete(templateOpenApi.getPaths().get(baseUrl+"/{uuid}").getDelete()));
		}

		Info infos = this.openApi.getInfo();
		String description = infos.getDescription() + "       The configured Magic Tenant ID is : 0";
		infos.description(description);

		return;
	}

}
