package org.jco.spring.domain.crudify.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.jco.spring.domain.crudify.connector.ISpringCrudifyConnector;
import org.jco.spring.domain.crudify.controller.SpringCrudifyEngineController;
import org.jco.spring.domain.crudify.repository.SpringCrudifyEngineRepository;
import org.jco.spring.domain.crudify.repository.dao.mongodb.SpringCrudifyEngineMongoRepository;
import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.jco.spring.domain.crudify.spec.SpringCrudifyReadOutputMode;
import org.jco.spring.domain.crudify.ws.SpringCrudifyEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPatternParser;

@Configuration
public class SpringCrudifyEngine {

	@Autowired
	private ConfigurableApplicationContext context;

	@Inject
	protected MongoTemplate mongo;

	@Value("${spring.domain.crudify.magicTenantId}")
	protected String magicTenantId;

	@Autowired
	private RequestMappingHandlerMapping requestMappingHandlerMapping;
	
	@Bean
	public List<SpringCrudifyEngineService> engineServices() throws NoSuchMethodException, SecurityException {

		List<SpringCrudifyEngineService> services = new ArrayList<SpringCrudifyEngineService>();
		Optional<ISpringCrudifyConnector<ISpringCrudifyEntity, List<ISpringCrudifyEntity>>> toto = Optional.empty();

		SpringCrudifyEngineMongoRepository dao = new SpringCrudifyEngineMongoRepository(TestDTO.class, this.mongo, this.magicTenantId);
		SpringCrudifyEngineRepository repo = new SpringCrudifyEngineRepository(TestEntity.class, TestDTO.class, dao);
		SpringCrudifyEngineController controller = new SpringCrudifyEngineController(TestEntity.class, repo, toto);
		SpringCrudifyEngineService service = new SpringCrudifyEngineService(TestEntity.class, controller);

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
		
		return services;

	}


	public void addMapping(String urlPath) throws NoSuchMethodException {

		

//	    requestMappingHandlerMapping.
//	            registerMapping(requestMappingInfo, queryController, QueryController.class.getDeclaredMethod("handleRequests")
//	            );
	}

}
