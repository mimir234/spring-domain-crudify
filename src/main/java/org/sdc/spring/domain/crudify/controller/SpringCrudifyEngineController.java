package org.sdc.spring.domain.crudify.controller;

import java.util.List;
import java.util.Optional;

import org.sdc.spring.domain.crudify.connector.ISpringCrudifyConnector;
import org.sdc.spring.domain.crudify.repository.ISpringCrudifyRepository;
import org.sdc.spring.domain.crudify.spec.ISpringCrudifyEntity;

@SuppressWarnings("unchecked")
public class SpringCrudifyEngineController extends AbstractSpringCrudifyController<ISpringCrudifyEntity> {

	private Class<?> entityClass;

	public SpringCrudifyEngineController(Class<?> entityClass, ISpringCrudifyRepository<ISpringCrudifyEntity> crudRepository, Optional<ISpringCrudifyConnector<ISpringCrudifyEntity, List<ISpringCrudifyEntity>>> crudConnector, Optional<ISpringCrudifyBusiness<ISpringCrudifyEntity>> business) {
		this.entityClass = entityClass;
		this.crudRepository = crudRepository;
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
	public void setRepository(ISpringCrudifyRepository<?> repository) {
		this.crudRepository = (ISpringCrudifyRepository<ISpringCrudifyEntity>) repository;
	}

	@Override
	public void setConnector(Optional<?> connectorObj) {
		this.crudConnector = (Optional<ISpringCrudifyConnector<ISpringCrudifyEntity, List<ISpringCrudifyEntity>>>) connectorObj;
	}

	@Override
	public void setbusiness(Optional<?> business) {
		this.business = (Optional<ISpringCrudifyBusiness<ISpringCrudifyEntity>>) business;
		
	}

}
