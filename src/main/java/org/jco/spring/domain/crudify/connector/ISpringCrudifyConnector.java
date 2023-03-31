/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.jco.spring.domain.crudify.connector;

import java.util.List;

import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntity;

public interface ISpringCrudifyConnector <T extends ISpringCrudifyEntity> {

	public enum SpringCrudifyConnectorOperation {
		CREATE, UPDATE, DELETE
		
	}

	public void publishEntity(String tenantId, String domain, T object, SpringCrudifyConnectorOperation operation) throws SpringCrudifyConnectorException;
	
	//Read methods

	public T readEntity(String tenantId, String domain, String uuid) throws SpringCrudifyConnectorException;
	
	public List<String> getEntityUuidList(String tenantId, String domain) throws SpringCrudifyConnectorException;

	public List<String> getEntityIdList(String tenantId, String domain) throws SpringCrudifyConnectorException;

	public List<T> getEntityFullList(String tenantId, String domain) throws SpringCrudifyConnectorException;

}
