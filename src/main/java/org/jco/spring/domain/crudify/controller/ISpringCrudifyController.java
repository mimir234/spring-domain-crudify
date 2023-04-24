/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.jco.spring.domain.crudify.controller;

import java.util.List;

import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.jco.spring.domain.crudify.spec.SpringCrudifyEntityException;
import org.jco.spring.domain.crudify.spec.SpringCrudifyReadOutputMode;
import org.jco.spring.domain.crudify.spec.filter.SpringCrudifyLiteral;
import org.jco.spring.domain.crudify.spec.sort.SpringCrudifySort;

public interface ISpringCrudifyController<T extends ISpringCrudifyEntity> {

	public T createEntity(String tenantId, T object) throws SpringCrudifyEntityException;

	public T updateEntity(String tenantId, T entity) throws SpringCrudifyEntityException;
	
	public void deleteEntity(String tenantId, String id) throws SpringCrudifyEntityException;

	public void deleteEntities(String tenantId) throws SpringCrudifyEntityException;

	public T getEntity(String tenantId, String uuid) throws SpringCrudifyEntityException;

	public long getEntityTotalCount(String tenantId) throws SpringCrudifyEntityException;
	
	public void setEntityClazz();

	public List<?> getEntityList(String tenantId, int pageSize, int pageIndex, SpringCrudifyLiteral filter, SpringCrudifySort sort,
			SpringCrudifyReadOutputMode mode) throws SpringCrudifyEntityException;
	
}
