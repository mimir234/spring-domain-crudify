package org.sdc.spring.domain.crudify.controller;

import java.util.List;
import java.util.Optional;

import org.sdc.spring.domain.crudify.business.ISpringCrudifyBusiness;
import org.sdc.spring.domain.crudify.connector.ISpringCrudifyConnector;
import org.sdc.spring.domain.crudify.repository.ISpringCrudifyRepository;
import org.sdc.spring.domain.crudify.spec.ISpringCrudifyEntity;

@SuppressWarnings("unchecked")
public class SpringCrudifyEngineController extends AbstractSpringCrudifyController<ISpringCrudifyEntity> {

	private Class<?> entityClass;

	public SpringCrudifyEngineController(Class<?> entityClass, Optional<?> repoObj, Optional<ISpringCrudifyConnector<ISpringCrudifyEntity, List<ISpringCrudifyEntity>>> crudConnector, Optional<ISpringCrudifyBusiness<ISpringCrudifyEntity>> business) {
		this.entityClass = entityClass;
		this.crudRepository = (Optional<ISpringCrudifyRepository<ISpringCrudifyEntity>>) repoObj;
		this.crudConnector = crudConnector;
		this.business = business;
		
		this.getDomain();
	}
	
	@Override
	public Class<ISpringCrudifyEntity> getEntityClazz() {
		return (Class<ISpringCrudifyEntity>) this.entityClass;
	}


	@Override
	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

	@Override
	public void setRepository(Optional<?> repository) {
		this.crudRepository = (Optional<ISpringCrudifyRepository<ISpringCrudifyEntity>>) repository;
	}

	@Override
	public void setConnector(Optional<ISpringCrudifyConnector<ISpringCrudifyEntity, List<ISpringCrudifyEntity>>> connectorObj) {
		this.crudConnector = connectorObj;
	}

	@Override
	public void setbusiness(Optional<ISpringCrudifyBusiness<ISpringCrudifyEntity>> business) {
		this.business = business;
		
	}

}
