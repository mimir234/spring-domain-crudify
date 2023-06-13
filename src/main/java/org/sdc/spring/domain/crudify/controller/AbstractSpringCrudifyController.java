/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.sdc.spring.domain.crudify.controller;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Inject;

import org.sdc.spring.domain.crudify.business.ISpringCrudifyBusiness;
import org.sdc.spring.domain.crudify.connector.ISpringCrudifyConnector;
import org.sdc.spring.domain.crudify.connector.ISpringCrudifyConnector.SpringCrudifyConnectorOperation;
import org.sdc.spring.domain.crudify.connector.SpringCrudifyConnectorException;
import org.sdc.spring.domain.crudify.events.ISpringCrudifyEventPublisher;
import org.sdc.spring.domain.crudify.events.SpringCrudifyEntityEvent;
import org.sdc.spring.domain.crudify.repository.ISpringCrudifyRepository;
import org.sdc.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.sdc.spring.domain.crudify.spec.ISpringCrudifyEntityFactory;
import org.sdc.spring.domain.crudify.spec.SpringCrudifyEntityException;
import org.sdc.spring.domain.crudify.spec.SpringCrudifyEntityHelper;
import org.sdc.spring.domain.crudify.spec.SpringCrudifyReadOutputMode;
import org.sdc.spring.domain.crudify.spec.filter.SpringCrudifyLiteral;
import org.sdc.spring.domain.crudify.spec.filter.SpringCrudifyLiteralException;
import org.sdc.spring.domain.crudify.spec.sort.SpringCrudifySort;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author JérémyCOLOMBET
 *
 * @param <Entity>
 * 
 *            This class implements the data treatments that have to be done one
 *            the entities during their process. If the Uuid of an entity is not
 *            set before storage, then the controller calculates one and affects
 *            it to the entity.
 * 
 * 
 */
@Slf4j
public abstract class AbstractSpringCrudifyController<Entity extends ISpringCrudifyEntity> implements ISpringCrudifyController<Entity> {

	/**
	 * The repository used to store the entity
	 */
	@Inject
	protected Optional<ISpringCrudifyRepository<Entity>> crudRepository;

	@Inject
	protected Optional<ISpringCrudifyConnector<Entity, List<Entity>>> crudConnector;
	
	@Inject
	protected Optional<ISpringCrudifyEventPublisher> eventPublisher;
	
	@Inject
	protected Optional<ISpringCrudifyBusiness<Entity>> business;

	protected String domain;

	private ISpringCrudifyEntityFactory<Entity> factory;

	@SuppressWarnings("unchecked")
	@PostConstruct
	protected void getDomain() {
		Class<Entity> clazz = this.getEntityClazz();
		try {
			this.factory = (ISpringCrudifyEntityFactory<Entity>) SpringCrudifyEntityHelper.getFactory((Class<ISpringCrudifyEntity>) clazz);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.domain = SpringCrudifyEntityHelper.getDomain((Class<ISpringCrudifyEntity>) clazz);
	}

	/**
	 * 
	 */
	@Override
	public Entity getEntity(String tenantId, String uuid) throws SpringCrudifyEntityException {
		log.info("[Tenant {}] [Domain {}] Getting entity with Uuid " + uuid, tenantId, this.domain);
		Entity entity = null;

		if (this.crudConnector.isPresent()) {

			try {

				entity = this.factory.newInstance(uuid);

				Future<Entity> entityResponse = this.crudConnector.get().requestEntity(tenantId, entity, SpringCrudifyConnectorOperation.READ);

				while (!entityResponse.isDone()) {
					Thread.sleep(250);
				}

				entity = entityResponse.get();
			} catch (Exception e) {
				throw new SpringCrudifyEntityException(SpringCrudifyEntityException.CONNECTOR_ERROR, e);
			}

		} else if (this.crudRepository.isPresent()) {
			entity = this.crudRepository.get().getOneByUuid(tenantId, uuid);
		}

		if (entity == null) {
			throw new SpringCrudifyEntityException(SpringCrudifyEntityException.ENTITY_NOT_FOUND,
					"Entity does not exist");
		}
		return entity;
	}

	@Override
	public Entity createEntity(String tenantId, Entity entity) throws SpringCrudifyEntityException {

		log.info("[Tenant {}] [Domain {}] Creating entity with uuid {}", tenantId, this.domain, entity.getUuid());

		if (entity.getUuid() == null || entity.getUuid().isEmpty()) {
			entity.setUuid(UUID.randomUUID().toString());
		}

		if(this.business.isPresent()) {
			this.business.get().beforeCreate(tenantId, entity);
		}

		if (this.crudConnector.isPresent()) {
			try {

				Future<Entity> entityResponse = this.crudConnector.get().requestEntity(tenantId, entity, SpringCrudifyConnectorOperation.CREATE);

				while (!entityResponse.isDone()) {
					Thread.sleep(250);
				}

				entity = entityResponse.get();
			} catch (Exception e) {
				throw new SpringCrudifyEntityException(SpringCrudifyEntityException.CONNECTOR_ERROR, e);
			}
		} else if (this.crudRepository.isPresent()) {

			if (this.crudRepository.get().doesExists(tenantId, entity)) {
				throw new SpringCrudifyEntityException(SpringCrudifyEntityException.ENTITY_ALREADY_EXISTS,
						"Entity already exists");
			}

			this.crudRepository.get().save(tenantId, entity);
		}
		
		if( this.eventPublisher.isPresent()) {
			this.eventPublisher.get().publishEntityEvent(SpringCrudifyEntityEvent.CREATE, entity);
		}
		
		return entity;
	}

	@Override
	public List<?> getEntityList(String tenantId, int pageSize, int pageIndex, SpringCrudifyLiteral filter, SpringCrudifySort sort, SpringCrudifyReadOutputMode mode)
			throws SpringCrudifyEntityException {
		log.info("[Tenant {}] [Domain {}] Getting entities, mode {}, page size {}, page index {}, filter {}, sort {}", tenantId, this.domain, mode, pageSize, pageIndex, filter, sort);
		try {
			SpringCrudifyLiteral.validate(filter);
		} catch (SpringCrudifyLiteralException e) {
			throw new SpringCrudifyEntityException(SpringCrudifyEntityException.BAD_REQUEST, e);
		}
		ArrayList<String> entityUuids = new ArrayList<String>();

		List<Entity> entities = null;

		if (this.crudConnector.isPresent()) {
			try {

				Future<List<Entity>> entityResponse = this.crudConnector.get().requestList(tenantId, null, SpringCrudifyConnectorOperation.READ);
				
				while( !entityResponse.isDone() ) {
					Thread.sleep(250);
				}
				
				entities = entityResponse.get();	
			} catch (Exception e) {
				throw new SpringCrudifyEntityException(SpringCrudifyEntityException.CONNECTOR_ERROR, e);
			}
		} else if (this.crudRepository.isPresent()) {
			entities = this.crudRepository.get().getEntities(tenantId, pageSize, pageIndex, filter, sort);
		}

		switch (mode) {
		case full -> {return entities;}
		case id -> {entities.forEach(e -> { entityUuids.add(e.getId()); });}
		case uuid -> {entities.forEach(e -> { entityUuids.add(e.getUuid()); });}
		}
		
		return entityUuids;
	}

	@Override
	public Entity updateEntity(String tenantId, Entity entity) throws SpringCrudifyEntityException {
		log.info("[Tenant {}] [Domain {}] Updating entity with Uuid " + entity.getUuid(), tenantId, this.domain);
		Entity updated = null;
		
		if(this.business.isPresent()) {
			this.business.get().beforeUpdate(tenantId, entity);
		}

		if (this.crudConnector.isPresent()) {
			try {

				Future<Entity> entityResponse = this.crudConnector.get().requestEntity(tenantId, entity, SpringCrudifyConnectorOperation.UPDATE);

				while (!entityResponse.isDone()) {
					Thread.sleep(250);
				}

				entity = entityResponse.get();
			} catch (Exception e) {
				throw new SpringCrudifyEntityException(SpringCrudifyEntityException.CONNECTOR_ERROR, e);
			}
		} else if (this.crudRepository.isPresent()) {
			if (!this.crudRepository.get().doesExists(tenantId, entity)) {
				throw new SpringCrudifyEntityException(SpringCrudifyEntityException.ENTITY_NOT_FOUND,
						"Entity does not exist");
			}

			updated = this.crudRepository.get().update(tenantId, entity);
		}
		
		if( this.eventPublisher.isPresent()) {
			this.eventPublisher.get().publishEntityEvent(SpringCrudifyEntityEvent.UPDATE, entity);
		}

		return updated;
	}

	@Override
	public void deleteEntity(String tenantId, String uuid) throws SpringCrudifyEntityException {
		log.info("[Tenant {}] [Domain {}] Deleting entity with Uuid " + uuid, tenantId, this.domain);

		Entity entity = this.getEntity(tenantId, uuid);

		if (entity == null) {
			throw new SpringCrudifyEntityException(SpringCrudifyEntityException.ENTITY_NOT_FOUND,
					"Entity does not exist");
		}
		
		if(this.business.isPresent()) {
			this.business.get().beforeDelete(tenantId, entity);
		}
		
		if (this.crudConnector.isPresent()) {
			try {

				Future<Entity> entityResponse = this.crudConnector.get().requestEntity(tenantId, entity, SpringCrudifyConnectorOperation.DELETE);

				while (!entityResponse.isDone()) {
					Thread.sleep(250);
				}

				entity = entityResponse.get();
			} catch (Exception e) {
				throw new SpringCrudifyEntityException(SpringCrudifyEntityException.CONNECTOR_ERROR, e);
			}
		} else if (this.crudRepository.isPresent()) {
			this.crudRepository.get().delete(tenantId, entity);
		}
		
		if( this.eventPublisher.isPresent()) {
			this.eventPublisher.get().publishEntityEvent(SpringCrudifyEntityEvent.DELETE, entity);
		}

	}

	@Override
	public void deleteEntities(final String tenantId) throws SpringCrudifyEntityException {
		log.info("[Tenant {}] [Domain {}] Deleting all entities", tenantId, this.domain);
		List<Entity> entities = this.crudRepository.get().getEntities(tenantId, 0, 1, null, null);

		for (Entity s : entities) {
			try {
				this.deleteEntity(tenantId, s.getUuid());
			} catch (SpringCrudifyEntityException e) {
				throw new SpringCrudifyEntityException(SpringCrudifyEntityException.UNKNOWN_ERROR,
						"Error during entities deletion");
			}
		}
	}

	@Override
	public long getEntityTotalCount(String tenantId, SpringCrudifyLiteral filter) throws SpringCrudifyEntityException {
		if (this.crudConnector.isPresent()) {
			try {
				Future<List<Entity>> list = this.crudConnector.get().requestList(tenantId, null, null);
				while (!list.isDone()) {
					Thread.sleep(250);
				}
				return list.get().size();
			} catch (InterruptedException | ExecutionException | SpringCrudifyConnectorException e) {
				throw new SpringCrudifyEntityException(SpringCrudifyEntityException.CONNECTOR_ERROR, e);
			}
		} else if (this.crudRepository.isPresent()) {
			return this.crudRepository.get().getCount(tenantId, filter);
		}
		return 0;

	}
}
