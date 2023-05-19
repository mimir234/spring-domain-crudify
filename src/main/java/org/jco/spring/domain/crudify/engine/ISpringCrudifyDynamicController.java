package org.jco.spring.domain.crudify.engine;

import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.jco.spring.domain.crudify.spec.SpringCrudifyEntityException;

public interface ISpringCrudifyDynamicController<Entity extends ISpringCrudifyEntity> {

	void beforeCreate(String tenantId, Entity entity) throws SpringCrudifyEntityException;

	void beforeUpdate(String tenantId, Entity entity) throws SpringCrudifyEntityException;

	void beforeDelete(String tenantId, Entity entity) throws SpringCrudifyEntityException;

}
