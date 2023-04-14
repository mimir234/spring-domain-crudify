/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.jco.spring.domain.crudify.controller;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Future;

import javax.inject.Inject;

import org.jco.spring.domain.crudify.connector.ISpringCrudifyConnector;
import org.jco.spring.domain.crudify.connector.ISpringCrudifyConnector.SpringCrudifyConnectorOperation;
import org.jco.spring.domain.crudify.repository.ISpringCrudifyRepository;
import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntityFactory;
import org.jco.spring.domain.crudify.spec.SpringCrudifyEntityException;
import org.jco.spring.domain.crudify.spec.filter.SpringCrudifyLiteral;
import org.jco.spring.domain.crudify.spec.filter.SpringCrudifyLiteralException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author JérémyCOLOMBET
 *
 * @param <T>
 * 
 *            This class implements the data treatments that have to be done one
 *            the entities during their process. If the Uuid of an entity is not
 *            set before storage, then the controller calculates one and affects
 *            it to the entity.
 * 
 * 
 */
@Slf4j
public abstract class AbstractSpringCrudifyController<T extends ISpringCrudifyEntity> implements ISpringCrudifyController<T> {

	/**
	 * The repository used to store the entity
	 */
	@Inject
	protected ISpringCrudifyRepository<T> crudRepository;

	@Inject
	private Optional<ISpringCrudifyConnector<T, List<T>>> crudConnector;

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

	/**
	 * 
	 */
	@Override
	public T getEntity(String tenantId, String uuid) throws SpringCrudifyEntityException {
		log.info("[Tenant {}] [Domain {}] Getting entity with Uuid " + uuid, tenantId, this.domain);
		T entity = null;

		if (this.crudConnector.isPresent()) {

			try {

				entity = this.factory.newInstance(uuid);

				Future<T> entityResponse = this.crudConnector.get().requestEntity(tenantId, entity, SpringCrudifyConnectorOperation.READ);

				while (!entityResponse.isDone()) {
					Thread.sleep(250);
				}

				entity = entityResponse.get();
			} catch (Exception e) {
				throw new SpringCrudifyEntityException(SpringCrudifyEntityException.CONNECTOR_ERROR, e);
			}

		} else {
			entity = this.crudRepository.getOneByUuid(tenantId, uuid);
		}

		if (entity == null) {
			throw new SpringCrudifyEntityException(SpringCrudifyEntityException.ENTITY_NOT_FOUND,
					"Entity does not exist");
		}
		return entity;
	}

	@Override
	public T createEntity(String tenantId, T entity) throws SpringCrudifyEntityException {

		log.info("[Tenant {}] [Domain {}] Creating entity with uuid {}", tenantId, this.domain, entity.getUuid());

		if (entity.getUuid() == null || entity.getUuid().isEmpty()) {
			entity.setUuid(UUID.randomUUID().toString());
		}

		this.beforeCreate(tenantId, entity);

		if (this.crudConnector.isPresent()) {
			try {

				Future<T> entityResponse = this.crudConnector.get().requestEntity(tenantId, entity, SpringCrudifyConnectorOperation.CREATE);

				while (!entityResponse.isDone()) {
					Thread.sleep(250);
				}

				entity = entityResponse.get();
			} catch (Exception e) {
				throw new SpringCrudifyEntityException(SpringCrudifyEntityException.CONNECTOR_ERROR, e);
			}
		} else {

			if (this.crudRepository.doesExists(tenantId, entity)) {
				throw new SpringCrudifyEntityException(SpringCrudifyEntityException.ENTITY_ALREADY_EXISTS,
						"Entity already exists");
			}

			this.crudRepository.save(tenantId, entity);
		}

		return entity;
	}

	@Override
	public List<String> getEntityUuidList(String tenantId, int pageSize, int pageIndex, SpringCrudifyLiteral filter)
			throws SpringCrudifyEntityException {
		log.info("[Tenant {}] [Domain {}] Getting entities", tenantId, this.domain);
		try {
			SpringCrudifyLiteral.validate(filter);
		} catch (SpringCrudifyLiteralException e) {
			throw new SpringCrudifyEntityException(SpringCrudifyEntityException.BAD_REQUEST, e);
		}
		ArrayList<String> entityUuids = new ArrayList<String>();

		List<T> entities = null;

		if (this.crudConnector.isPresent()) {
			try {

				Future<List<T>> entityResponse = this.crudConnector.get().requestList(tenantId, null, SpringCrudifyConnectorOperation.READ);
				
				while( !entityResponse.isDone() ) {
					Thread.sleep(250);
				}
				
				entities = entityResponse.get();	
			} catch (Exception e) {
				throw new SpringCrudifyEntityException(SpringCrudifyEntityException.CONNECTOR_ERROR, e);
			}
		} else {
			entities = this.crudRepository.getEntities(tenantId, pageSize, pageIndex, filter);
		}

		entities.forEach(e -> {
			entityUuids.add(e.getUuid());
		});

		return entityUuids;
	}

	@Override
	public List<String> getEntityIdList(String tenantId, int pageSize, int pageIndex, SpringCrudifyLiteral filter)
			throws SpringCrudifyEntityException {
		log.info("[Tenant {}] [Domain {}] Getting entities", tenantId, this.domain);
		ArrayList<String> entityUuids = new ArrayList<String>();
		try {
			SpringCrudifyLiteral.validate(filter);
		} catch (SpringCrudifyLiteralException e) {
			throw new SpringCrudifyEntityException(SpringCrudifyEntityException.BAD_REQUEST, e);
		}
		
		List<T> entities = null;

		if (this.crudConnector.isPresent()) {
			try {

				Future<List<T>> entityResponse = this.crudConnector.get().requestList(tenantId, null, SpringCrudifyConnectorOperation.READ);

				while (!entityResponse.isDone()) {
					Thread.sleep(250);
				}

				entities = entityResponse.get();
			} catch (Exception e) {
				throw new SpringCrudifyEntityException(SpringCrudifyEntityException.CONNECTOR_ERROR, e);
			}
		} else {
			entities = this.crudRepository.getEntities(tenantId, pageSize, pageIndex, filter);
		}

		entities.forEach(e -> {
			entityUuids.add(e.getId());
		});

		return entityUuids;
	}

	@Override
	public List<T> getEntityFullList(String tenantId, int pageSize, int pageIndex, SpringCrudifyLiteral filter) throws SpringCrudifyEntityException {
		log.info("[Tenant {}] [Domain {}] Getting entities", tenantId, this.domain);
		try {
			SpringCrudifyLiteral.validate(filter);
		} catch (SpringCrudifyLiteralException e) {
			throw new SpringCrudifyEntityException(SpringCrudifyEntityException.BAD_REQUEST, e);
		}
		List<T> entities = null;

		if (this.crudConnector.isPresent()) {
			try {

				Future<List<T>> entityResponse = this.crudConnector.get().requestList(tenantId, null, SpringCrudifyConnectorOperation.READ);

				while (!entityResponse.isDone()) {
					Thread.sleep(250);
				}

				entities = entityResponse.get();
			} catch (Exception e) {
				throw new SpringCrudifyEntityException(SpringCrudifyEntityException.CONNECTOR_ERROR, e);
			}
		} else {
			entities = this.crudRepository.getEntities(tenantId, pageSize, pageIndex, filter);
		}
		return entities;
	}

	@Override
	public T updateEntity(String tenantId, T entity) throws SpringCrudifyEntityException {
		log.info("[Tenant {}] [Domain {}] Updating entity with Uuid " + entity.getUuid(), tenantId, this.domain);
		T updated = null;

		this.beforeUpdate(tenantId, entity);

		if (this.crudConnector.isPresent()) {
			try {

				Future<T> entityResponse = this.crudConnector.get().requestEntity(tenantId, entity, SpringCrudifyConnectorOperation.UPDATE);

				while (!entityResponse.isDone()) {
					Thread.sleep(250);
				}

				entity = entityResponse.get();
			} catch (Exception e) {
				throw new SpringCrudifyEntityException(SpringCrudifyEntityException.CONNECTOR_ERROR, e);
			}
		} else {
			if (!this.crudRepository.doesExists(tenantId, entity)) {
				throw new SpringCrudifyEntityException(SpringCrudifyEntityException.ENTITY_NOT_FOUND,
						"Entity does not exist");
			}

			updated = this.crudRepository.update(tenantId, entity);
		}

		return updated;
	}

	@Override
	public void deleteEntity(String tenantId, String uuid) throws SpringCrudifyEntityException {
		log.info("[Tenant {}] [Domain {}] Deleting entity with Uuid " + uuid, tenantId, this.domain);

		if (this.crudConnector.isPresent()) {
			try {

				T entity = this.factory.newInstance(uuid);
				Future<T> entityResponse = this.crudConnector.get().requestEntity(tenantId, entity, SpringCrudifyConnectorOperation.DELETE);

				while (!entityResponse.isDone()) {
					Thread.sleep(250);
				}

				entity = entityResponse.get();
			} catch (Exception e) {
				throw new SpringCrudifyEntityException(SpringCrudifyEntityException.CONNECTOR_ERROR, e);
			}
		} else {
			T entity = this.crudRepository.getOneByUuid(tenantId, uuid);

			if (entity == null) {
				throw new SpringCrudifyEntityException(SpringCrudifyEntityException.ENTITY_NOT_FOUND,
						"Entity does not exist");
			}

			this.beforeDelete(tenantId, entity);

			this.crudRepository.delete(tenantId, entity);
		}

	}

	@Override
	public void deleteEntities(final String tenantId) throws SpringCrudifyEntityException {
		log.info("[Tenant {}] [Domain {}] Deleting all entities", tenantId, this.domain);
		List<T> entities = this.crudRepository.getEntities(tenantId, 0, 1, null);

		for (T s : entities) {
			try {
				this.deleteEntity(tenantId, s.getUuid());
			} catch (SpringCrudifyEntityException e) {
				throw new SpringCrudifyEntityException(SpringCrudifyEntityException.UNKNOWN_ERROR,
						"Error during entities deletion");
			}
		}
	}

	@Override
	public long getEntityTotalCount(String tenantId) throws SpringCrudifyEntityException {
		return this.crudRepository.getTotalCount(tenantId);
	}

	// -----------------------------------------------------------//
	// Abstract methods below to be implemented by sub classes //
	// -----------------------------------------------------------//

	protected abstract void beforeCreate(String tenantId, T entity) throws SpringCrudifyEntityException;

	protected abstract void beforeUpdate(String tenantId, T entity) throws SpringCrudifyEntityException;

	protected abstract void beforeDelete(String tenantId, T entity) throws SpringCrudifyEntityException;
}
