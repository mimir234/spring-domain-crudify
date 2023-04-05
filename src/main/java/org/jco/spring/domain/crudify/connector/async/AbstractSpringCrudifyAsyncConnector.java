package org.jco.spring.domain.crudify.connector.async;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jco.spring.domain.crudify.connector.ISpringCrudifyConnector;
import org.jco.spring.domain.crudify.connector.SpringCrudifyConnectorException;
import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntityFactory;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractSpringCrudifyAsyncConnector<T extends ISpringCrudifyEntity> implements ISpringCrudifyConnector<T> {

	@Inject
	protected ExecutorService executor;
	
	@Value("${spring.domain.crudify.connector.timeout}")
	protected int timeout;
	
	@Value("${spring.domain.crudify.connector.timeout.timeUnit}")
	protected TimeUnit unit;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	private Object mapLocker = new Object(); 

	private Map<String, SpringCrudifyAsyncConnectorPair<T>> receivedMessages = new HashMap<String, SpringCrudifyAsyncConnectorPair<T>>();

	protected Class<T> clazz;

	private String domain;

	private ISpringCrudifyEntityFactory<T> factory;

	@SuppressWarnings("unchecked")
	@PostConstruct
	private void getDomain() {
		this.setEntityClazz();
		Constructor<T> constructor;
		try {

			constructor = this.clazz.getConstructor();
			T entity = (T) constructor.newInstance();
			if (entity.getDomain().isEmpty()) {
				this.domain = "unknown";
			} else {
				this.domain = entity.getDomain();
			}

			this.factory = (ISpringCrudifyEntityFactory<T>) entity.getFactory();

		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			this.domain = "unknown";
			this.factory = null;
		}
	}
	
	@Override
	public Future<List<T>> requestList(String tenantId, List<T> object, SpringCrudifyConnectorOperation operation) throws SpringCrudifyConnectorException {
		log.info("[Tenant {}] [Domain {}] Processing request entity list operation {} ", tenantId, this.domain, operation);
		
		
		
		
		
		return null;
	}	
	
	@Override
	public Future<T> requestEntity(String tenantId, T object, SpringCrudifyConnectorOperation operation) throws SpringCrudifyConnectorException {
		log.info("[Tenant {}] [Domain {}] Processing request entity operation {} ", tenantId, this.domain, operation);
		
		return this.executor.submit(new Callable<T>() {
			
			public T call() throws SpringCrudifyConnectorException {
				
				if( object == null ) {
					mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
				} else {
					mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, true);
				}
				
				String uuid = UUID.randomUUID().toString();
				String transactionUuid = uuid;
				
				Object locker = new Object();

				SpringCrudifyAsyncConnectorEnvelop<T> message = new SpringCrudifyAsyncConnectorEnvelop<T>(
						SpringCrudifyAsyncMessageType.REQUEST, uuid, transactionUuid, tenantId, object.getDomain(), null, operation,
						object, null, null);
				
				try {
					log.info("[Tenant {}] [Domain {}] Sending request {}", tenantId, object.getDomain(), mapper.writeValueAsString(message));
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				synchronized (mapLocker) {
					receivedMessages.put(uuid, new SpringCrudifyAsyncConnectorPair<T>(locker, null));
				}
				
				publishRequest(message);
				
				if( log.isDebugEnabled() ) log.debug("Thread sleeping for {} {}", timeout, unit.toString());
				
				long millis = TimeUnit.MILLISECONDS.convert(timeout, unit);
				
				if( log.isDebugEnabled() ) log.debug("Thread sleeping for {} ms", millis);
				
				try {
					synchronized (locker) {
						locker.wait(millis);
					}
					
				} catch(InterruptedException e) {
					//Message is received
					if( log.isDebugEnabled() ) log.debug("Thread awake due to interruption exception");
				}
				
				if( log.isDebugEnabled() ) log.debug("Thread awake");
				
				SpringCrudifyAsyncConnectorPair<T> pair = null;
				synchronized (mapLocker) {
					pair = receivedMessages.remove(uuid);
				}
				
				if( pair.getEntity() == null ) {
					try {
						log.warn("[Tenant {}] [Domain {}] no message received for message {}", tenantId, object.getDomain(), mapper.writeValueAsString(message));
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					throw new SpringCrudifyConnectorException("No message received from connector");
				}
				
				if( log.isDebugEnabled() ) log.debug("map size "+receivedMessages.size());
				return pair.getEntity().getEntity();
			}
		});

	}

	protected void onResponse(SpringCrudifyAsyncConnectorEnvelop<T> message) throws SpringCrudifyConnectorException, JsonProcessingException {
		
		log.info("[Tenant {}] [Domain {}] Response received {}", message.getTenantId(), message.getEntity().getDomain(), this.mapper.writeValueAsString(message));
		
		String transactionUuid = message.getTransactionUuid();
		
		SpringCrudifyAsyncConnectorPair<T> pair = null;
		synchronized (this.mapLocker) {
			pair = this.receivedMessages.get(transactionUuid);
		}
		
		if( pair != null ) {
			pair.setEntity(message);
			synchronized (pair.getLocker()) {
				pair.getLocker().notifyAll();
			}
		} else {
			log.warn("[Tenant {}] [Domain {}] Request with transaction id {} not found, dropping message", message.getTenantId(), message.getEntity().getDomain(), message.getTransactionUuid());
		}
		
	}

	// -----------------------------------------------------------//
	// Abstract methods below to be implemented by sub classes //
	// -----------------------------------------------------------//

	abstract public void publishRequest(SpringCrudifyAsyncConnectorEnvelop<T> message) throws SpringCrudifyConnectorException;

}
