/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.jco.spring.domain.crudify;

import java.util.List;

public interface ISpringCrudifyController<T> {

	public T createEntity(String tenantId, T object, String domain) throws SpringCrudifyEntityException;

	public T updateEntity(String tenantId, T entity, String domain) throws SpringCrudifyEntityException;
	
	public void deleteEntity(String tenantId, String id, String domain) throws SpringCrudifyEntityException;

	public void deleteEntities(String tenantId, String domain);

	public T getEntity(String tenantId, String uuid) throws SpringCrudifyEntityException;

	public List<String> getEntityUuidList(String tenantId);

	public List<String> getEntityIdList(String tenantId);

	public List<T> getEntityFullList(String tenantId);
	
}