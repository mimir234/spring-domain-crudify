/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.jco.spring.domain.crudify;

public interface ISpringCrudifyConnector {

	public enum SpringCrudifyConnectorOperation {
		CREATE, UPDATE, DELETE
		
	}

	public <T extends ISpringCrudifyEntity> void publishEntity(T object, SpringCrudifyConnectorOperation create,
			String tenantId, String domain) throws SpringCrudifyConnectorException;

}
