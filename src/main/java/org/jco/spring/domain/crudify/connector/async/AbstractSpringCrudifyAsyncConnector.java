package org.jco.spring.domain.crudify.connector.async;

import java.util.List;

import org.jco.spring.domain.crudify.connector.ISpringCrudifyConnector;
import org.jco.spring.domain.crudify.connector.SpringCrudifyConnectorException;
import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractSpringCrudifyAsyncConnector<T extends ISpringCrudifyEntity> implements ISpringCrudifyConnector<T> {

	@Override
	public void publishEntity(String tenantId, String domain, T object, SpringCrudifyConnectorOperation operation) throws SpringCrudifyConnectorException {
	
		
	}

	@Override
	public T readEntity(String tenantId, String domain, String uuid) throws SpringCrudifyConnectorException {
		return null;
	}
	
	@Override
	public List<T> readEntityList(String tenantId, String domain) throws SpringCrudifyConnectorException {
		return null;
	}
	
	//-----------------------------------------------------------//
	// Abstract methods below to be implemented by sub classes    //
	//-----------------------------------------------------------//	
	
	abstract public void publishMessage(SpringCrudifyAsyncConnectorEnvelop<T> message) throws SpringCrudifyConnectorException;
	
	abstract public void onhMessage(SpringCrudifyAsyncConnectorEnvelop<T> message) throws SpringCrudifyConnectorException;
}
