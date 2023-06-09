package org.sdc.spring.domain.crudify.controller;

import org.sdc.spring.domain.crudify.spec.SpringCrudifyEntityException;

public interface ISpringCrudifyBusiness<Entity> {

	void beforeCreate(String tenantId, Entity entity) throws SpringCrudifyEntityException;

	void beforeUpdate(String tenantId, Entity entity) throws SpringCrudifyEntityException;

	void beforeDelete(String tenantId, Entity entity) throws SpringCrudifyEntityException;

}
