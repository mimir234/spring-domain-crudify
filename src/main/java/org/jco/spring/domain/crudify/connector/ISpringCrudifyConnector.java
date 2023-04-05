/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.jco.spring.domain.crudify.connector;

import java.util.List;
import java.util.concurrent.Future;

import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntity;

public interface ISpringCrudifyConnector <T extends ISpringCrudifyEntity, S extends List<T>> {

	public enum SpringCrudifyConnectorOperation {
		READ, CREATE, UPDATE, DELETE
	}

	/**
	 * 
	 * @param tenantId
	 * @param domain
	 * @param object
	 * @param operation
	 * @return
	 * @throws SpringCrudifyConnectorException
	 */
	public Future<T> requestEntity(String tenantId, T entity, SpringCrudifyConnectorOperation operation) throws SpringCrudifyConnectorException;
	
	/**
	 * 
	 * @param tenantId
	 * @param domain
	 * @param object
	 * @param operation
	 * @return
	 * @throws SpringCrudifyConnectorException
	 */
	public Future<S> requestList(String tenantId, S list, SpringCrudifyConnectorOperation operation) throws SpringCrudifyConnectorException;
	
	/**
	 * 
	 */
	public void setEntityClazz();


}
