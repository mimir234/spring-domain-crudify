/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.jco.spring.domain.crudify.connector;

import java.util.concurrent.Future;

import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntity;

public interface ISpringCrudifyConnector <T extends ISpringCrudifyEntity> {

	public enum SpringCrudifyConnectorOperation {
		READ, CREATE, UPDATE, DELETE, READ_LIST
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
	public Future<T> request(String tenantId, String domain, T object, SpringCrudifyConnectorOperation operation) throws SpringCrudifyConnectorException;

}
