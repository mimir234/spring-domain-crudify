package org.jco.spring.domain.crudify.engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.jco.spring.domain.crudify.connector.ISpringCrudifyConnector;
import org.jco.spring.domain.crudify.controller.SpringCrudifyEngineController;
import org.jco.spring.domain.crudify.repository.SpringCrudifyEngineRepository;
import org.jco.spring.domain.crudify.repository.dao.SpringCrudifyDao;
import org.jco.spring.domain.crudify.repository.dao.mongodb.SpringCrudifyEngineMongoRepository;
import org.jco.spring.domain.crudify.repository.dto.ISpringCrudifyDTOObject;
import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.jco.spring.domain.crudify.spec.SpringCrudifyReadOutputMode;
import org.jco.spring.domain.crudify.ws.SpringCrudifyEngineService;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPatternParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class SpringCrudifyDynamicDomainEngine {

	@Inject
	protected MongoTemplate mongo;
	
	@Inject 
	protected ApplicationContext context;

	@Value("${spring.domain.crudify.magicTenantId}")
	protected String magicTenantId;
	
	@Value("${spring.domain.crudify.engine.packages}")
	protected String[] scanPackages;

	@Autowired
	private RequestMappingHandlerMapping requestMappingHandlerMapping;
	
	@Bean
	public List<SpringCrudifyEngineService> engineServices() throws SpringCrudifyEngineException {

		log.info("== Starting Dynamic Domain Engine ==");
		List<SpringCrudifyEngineService> services = new ArrayList<SpringCrudifyEngineService>();
		
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
				
				String controller__ = entityAnnotation.controller();
				ISpringCrudifyDynamicController controller = null;
				
				if( controller__ != null && !controller__.isEmpty() ) {
					String[] splits = controller__.split(":");
					Class<?> controllerClass;
					try {
						controllerClass = Class.forName(splits[1]);
					} catch (ClassNotFoundException e1) {
						throw new SpringCrudifyEngineException(e1);
					}
					
					if (!ISpringCrudifyDynamicController.class.isAssignableFrom(controllerClass)) {
						throw new SpringCrudifyEngineException("The class [" + dtoClass.getName() + "] must implements the ISpringCrudifyDynamicController interface.");
					}

					switch(splits[0]) {
					case "bean":
						controller = (ISpringCrudifyDynamicController) this.context.getBean(controllerClass);
						break;
					case "class":
						try {
							Constructor<?> ctor = controllerClass.getConstructor();
							controller = (ISpringCrudifyDynamicController) ctor.newInstance();
						} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							throw new SpringCrudifyEngineException(e);
						}
						break;
					default:
						throw new SpringCrudifyEngineException("Invalid controller "+controller__+", should be bean: or class:");
					}
				}

				try {
					this.createDynamicDomain(services, entityClass, dtoClass, db, controller, authorize_creation, authorize_read_all, authorize_read_one, authorize_update_one, authorize_delete_one, authorize_delete_all, authorize_count);
				} catch (NoSuchMethodException e) {
					throw new SpringCrudifyEngineException(e);
				}
			}
		}
		
		return services;
	}


	private void createDynamicDomain(List<SpringCrudifyEngineService> services, Class<?> entityClass, Class<?> dtoClass, SpringCrudifyDao db, ISpringCrudifyDynamicController dynamicController, boolean authorize_creation, boolean authorize_read_all, boolean authorize_read_one, boolean authorize_update_one, boolean authorize_delete_one, boolean authorize_delete_all, boolean authorize_count) throws NoSuchMethodException {

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
		
		Optional<ISpringCrudifyConnector<ISpringCrudifyEntity, List<ISpringCrudifyEntity>>> connector = Optional.empty();
		SpringCrudifyEngineMongoRepository dao = null;
		
		switch(db) {
		default:
		case mongo:
			dao = new SpringCrudifyEngineMongoRepository(dtoClass, this.mongo, this.magicTenantId);
			break;
		}

		SpringCrudifyEngineRepository repo = new SpringCrudifyEngineRepository(entityClass, dtoClass, dao);
		SpringCrudifyEngineController controller = new SpringCrudifyEngineController(entityClass, repo, connector, dynamicController);
		SpringCrudifyEngineService service = new SpringCrudifyEngineService(entityClass, controller, authorize_creation, authorize_read_all, authorize_read_one, authorize_update_one, authorize_delete_one, authorize_delete_all, authorize_count);

		services.add(service);
		
		String baseUrl = "/"+service.getDomain().toLowerCase();
		
		RequestMappingInfo.BuilderConfiguration options = new RequestMappingInfo.BuilderConfiguration();
		options.setPatternParser(new PathPatternParser());
		
		RequestMappingInfo requestMappingInfoGetAll = RequestMappingInfo.paths(baseUrl).methods(RequestMethod.GET).options(options).build();
		RequestMappingInfo requestMappingInfoDeleteAll = RequestMappingInfo.paths(baseUrl).methods(RequestMethod.DELETE).options(options).build();
		RequestMappingInfo requestMappingInfoCreate = RequestMappingInfo.paths(baseUrl).methods(RequestMethod.POST).options(options).build();
		RequestMappingInfo requestMappingInfoCount = RequestMappingInfo.paths(baseUrl+"/count").methods(RequestMethod.DELETE).options(options).build();
		RequestMappingInfo requestMappingInfoGetOne = RequestMappingInfo.paths(baseUrl+"/{uuid}").methods(RequestMethod.GET).options(options).build();
		RequestMappingInfo requestMappingInfoUpdate = RequestMappingInfo.paths(baseUrl+"/{uuid}").methods(RequestMethod.PATCH).options(options).build();
		RequestMappingInfo requestMappingInfoDeleteOne = RequestMappingInfo.paths(baseUrl+"/{uuid}").methods(RequestMethod.DELETE).options(options).build();
		
		this.requestMappingHandlerMapping.registerMapping(requestMappingInfoGetAll, service, SpringCrudifyEngineService.class.getMethod("getEntities", String.class, SpringCrudifyReadOutputMode.class, Integer.class, Integer.class, String.class, String.class));
		this.requestMappingHandlerMapping.registerMapping(requestMappingInfoDeleteAll, service, SpringCrudifyEngineService.class.getMethod("deleteAll", String.class));
		this.requestMappingHandlerMapping.registerMapping(requestMappingInfoCreate, service, SpringCrudifyEngineService.class.getMethod("createEntity", String.class, String.class));
		this.requestMappingHandlerMapping.registerMapping(requestMappingInfoCount, service, SpringCrudifyEngineService.class.getMethod("getCount", String.class));
		this.requestMappingHandlerMapping.registerMapping(requestMappingInfoGetOne, service, SpringCrudifyEngineService.class.getMethod("getEntity", String.class, String.class));
		this.requestMappingHandlerMapping.registerMapping(requestMappingInfoUpdate, service, SpringCrudifyEngineService.class.getMethod("updateEntity", String.class, String.class, String.class));
		this.requestMappingHandlerMapping.registerMapping(requestMappingInfoDeleteOne, service, SpringCrudifyEngineService.class.getMethod("deleteEntity", String.class, String.class));
		
		return;
	}

}
