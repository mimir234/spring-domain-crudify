/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.jco.spring.domain.crudify.controller;

import java.util.List;

import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.jco.spring.domain.crudify.spec.SpringCrudifyEntityException;

public interface ISpringCrudifyController<T extends ISpringCrudifyEntity> {

	public T createEntity(String tenantId, T object) throws SpringCrudifyEntityException;

	public T updateEntity(String tenantId, T entity) throws SpringCrudifyEntityException;
	
	public void deleteEntity(String tenantId, String id) throws SpringCrudifyEntityException;

	public void deleteEntities(String tenantId) throws SpringCrudifyEntityException;

	public T getEntity(String tenantId, String uuid) throws SpringCrudifyEntityException;

	public List<String> getEntityUuidList(String tenantId, int pageSize, int pageIndex) throws SpringCrudifyEntityException;

	public List<String> getEntityIdList(String tenantId, int pageSize, int pageIndex) throws SpringCrudifyEntityException;

	public List<T> getEntityFullList(String tenantId, int pageSize, int pageIndex) throws SpringCrudifyEntityException;

	public Integer getEntityTotalCount(String tenantId) throws SpringCrudifyEntityException;
	
	public void setEntityClazz();
	
}
