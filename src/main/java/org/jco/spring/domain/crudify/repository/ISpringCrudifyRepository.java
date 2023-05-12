/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.jco.spring.domain.crudify.repository;

import java.util.List;

import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.jco.spring.domain.crudify.spec.filter.SpringCrudifyLiteral;
import org.jco.spring.domain.crudify.spec.sort.SpringCrudifySort;

/**
 * 
 * @author JérémyCOLOMBET
 *
 * @param <Entity>
 */
public interface ISpringCrudifyRepository<Entity extends ISpringCrudifyEntity> {

	boolean doesExists(String tenantId, Entity entity);

	List<Entity> getEntities(String tenantId, int pageSize, int pageIndex, SpringCrudifyLiteral filter,
			SpringCrudifySort sort);

	void save(String tenantId, Entity entity);

	Entity update(String tenantId, Entity entity);

	Entity getOneById(String tenantId, String id);

	void delete(String tenantId, Entity entity);

	boolean doesExists(String tenantId, String uuid);

	Entity getOneByUuid(String tenantId, String uuid);

	long getCount(String tenantId, SpringCrudifyLiteral filter);

	Class<Entity> getEntityClass();

}
