package org.jco.spring.domain.crudify.controller;

import java.util.List;
import java.util.Optional;

import org.jco.spring.domain.crudify.connector.ISpringCrudifyConnector;
import org.jco.spring.domain.crudify.engine.ISpringCrudifyDynamicController;
import org.jco.spring.domain.crudify.repository.ISpringCrudifyRepository;
import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.jco.spring.domain.crudify.spec.SpringCrudifyEntityException;

@SuppressWarnings("unchecked")
public class SpringCrudifyEngineController extends AbstractSpringCrudifyController<ISpringCrudifyEntity> {

	private Class<?> entityClass;
	private ISpringCrudifyDynamicController dynamicController;

	public SpringCrudifyEngineController(Class<?> entityClass, ISpringCrudifyRepository<ISpringCrudifyEntity> crudRepository, Optional<ISpringCrudifyConnector<ISpringCrudifyEntity, List<ISpringCrudifyEntity>>> crudConnector, ISpringCrudifyDynamicController dynamicController) {
		this.entityClass = entityClass;
		this.crudRepository = crudRepository;
		this.crudConnector = crudConnector;
		this.dynamicController = dynamicController;
		
		this.getDomain();
	}
	
	@Override
	public Class<ISpringCrudifyEntity> getEntityClazz() {
		return (Class<ISpringCrudifyEntity>) this.entityClass;
	}

	@Override
	protected void beforeCreate(String tenantId, ISpringCrudifyEntity entity) throws SpringCrudifyEntityException {
		if( this.dynamicController != null ) {
			this.dynamicController.beforeCreate(tenantId, entity);
		}
	}

	@Override
	protected void beforeUpdate(String tenantId, ISpringCrudifyEntity entity) throws SpringCrudifyEntityException {
		if( this.dynamicController != null ) {
			this.dynamicController.beforeUpdate(tenantId, entity);
		}
	}

	@Override
	protected void beforeDelete(String tenantId, ISpringCrudifyEntity entity) throws SpringCrudifyEntityException {
		if( this.dynamicController != null ) {
			this.dynamicController.beforeDelete(tenantId, entity);
		}
	}

}
