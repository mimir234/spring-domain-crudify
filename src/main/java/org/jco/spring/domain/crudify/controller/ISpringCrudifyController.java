/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.jco.spring.domain.crudify.controller;

import java.util.List;

import org.jco.spring.domain.crudify.spec.SpringCrudifyEntityException;

public interface ISpringCrudifyController<T> {

	public T createEntity(String tenantId, T object, String domain) throws SpringCrudifyEntityException;

	public T updateEntity(String tenantId, T entity, String domain) throws SpringCrudifyEntityException;
	
	public void deleteEntity(String tenantId, String id, String domain) throws SpringCrudifyEntityException;

	public void deleteEntities(String tenantId, String domain) throws SpringCrudifyEntityException;

	public T getEntity(String tenantId, String uuid, String domain) throws SpringCrudifyEntityException;

	public List<String> getEntityUuidList(String tenantId, String domain) throws SpringCrudifyEntityException;

	public List<String> getEntityIdList(String tenantId, String domain) throws SpringCrudifyEntityException;

	public List<T> getEntityFullList(String tenantId, String domain) throws SpringCrudifyEntityException;
	
}
