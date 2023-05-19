package org.jco.spring.domain.crudify.engine;

import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.jco.spring.domain.crudify.spec.SpringCrudifyEntityException;

public interface ISpringCrudifyDynamicController {

	void beforeCreate(String tenantId, ISpringCrudifyEntity entity) throws SpringCrudifyEntityException;

	void beforeUpdate(String tenantId, ISpringCrudifyEntity entity) throws SpringCrudifyEntityException;

	void beforeDelete(String tenantId, ISpringCrudifyEntity entity) throws SpringCrudifyEntityException;

}
