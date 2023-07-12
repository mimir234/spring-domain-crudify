package org.sdc.spring.domain.crudify.business;

import org.sdc.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.sdc.spring.domain.crudify.spec.SpringCrudifyEntityException;

public interface ISpringCrudifyBusiness<Entity extends ISpringCrudifyEntity> {

	void beforeCreate(String tenantId, String userId, Entity entity) throws SpringCrudifyEntityException;

	void beforeUpdate(String tenantId, String userId, Entity entity) throws SpringCrudifyEntityException;

	void beforeDelete(String tenantId, String userId, Entity entity) throws SpringCrudifyEntityException;

}
