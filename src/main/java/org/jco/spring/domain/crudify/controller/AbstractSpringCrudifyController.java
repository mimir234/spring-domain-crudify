/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.jco.spring.domain.crudify.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.jco.spring.domain.crudify.repository.ISpringCrudifyRepository;
import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.jco.spring.domain.crudify.spec.SpringCrudifyEntityException;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author JérémyCOLOMBET
 *
 * @param <T>
 * 
 * This class implements the data treatments that have to be done one the entities during their process. 
 * If the Uuid of an entity is not set before storage, then the controller calculates one and affects it to the entity. 
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
	
	/**
	 * 
	 */
	@Override
	public T getEntity(String tenantId, String uuid, String domain) throws SpringCrudifyEntityException {
		log.info("[Tenant "+tenantId+"] Getting entity with Uuid "+uuid);
		T entity = this.crudRepository.getOneByUuid(tenantId, uuid);
		if( entity == null ) {
			throw new SpringCrudifyEntityException(SpringCrudifyEntityException.ENTITY_NOT_FOUND, "Entity does not exist"); 
		}
		return entity;
	}

	@Override
	public T createEntity(String tenantId, T entity, String domain) throws SpringCrudifyEntityException {
		
		log.info("[Tenant "+tenantId+"] Creating entity of type "+entity.getClass());
		
		if( entity.getUuid() == null || entity.getUuid().isEmpty() ) {
			entity.setUuid(UUID.randomUUID().toString());
		}
		
		this.beforeCreate(tenantId, entity);
		
		if( this.crudRepository.doesExists(tenantId, entity) ){
			throw new SpringCrudifyEntityException("Entity already exists"); 
		}
		
		
		this.crudRepository.save(tenantId, entity);
		
		return entity;
	}

	@Override
	public List<String> getEntityUuidList(String tenantId, String domain) throws SpringCrudifyEntityException {
		log.info("[Tenant "+tenantId+"] Getting entities of type ");
		ArrayList<String> entityUuids = new ArrayList<String>();
		List<T> entities = this.crudRepository.getEntities(tenantId);
		
		entities.forEach(e -> {
			entityUuids.add(e.getUuid());
		});
		
		return entityUuids;
	}
	
	@Override
	public List<String> getEntityIdList(String tenantId, String domain) throws SpringCrudifyEntityException {
		log.info("[Tenant "+tenantId+"] Getting entities of type ");
		ArrayList<String> entityUuids = new ArrayList<String>();
		List<T> entities = this.crudRepository.getEntities(tenantId);
		
		entities.forEach(e -> {
			entityUuids.add(e.getId());
		});
		
		return entityUuids;
	}
	
	@Override
	public List<T> getEntityFullList(String tenantId, String domain) throws SpringCrudifyEntityException {
		log.info("[Tenant "+tenantId+"] Getting entities of type ");
		List<T> entities = this.crudRepository.getEntities(tenantId);
		return entities;
	}

	@Override
	public T updateEntity(String tenantId, T entity, String domain) throws SpringCrudifyEntityException {
		log.info("[Tenant "+tenantId+"] Updating entity with Uuid "+entity.getUuid());
		
		this.beforeUpdate(tenantId, entity);
		
		
		if( !this.crudRepository.doesExists(tenantId, entity) ){
			throw new SpringCrudifyEntityException(SpringCrudifyEntityException.ENTITY_NOT_FOUND, "Entity does not exist"); 
		}

		T updated = this.crudRepository.update(tenantId, entity);
		
		return updated;
	}

	@Override
	public void deleteEntity(String tenantId, String uuid, String domain) throws SpringCrudifyEntityException {
		log.info("[Tenant "+tenantId+"] Deleting entity with Uuid "+uuid);
		
		T entity = this.crudRepository.getOneByUuid(tenantId, uuid);
		
		if( entity == null ){
			throw new SpringCrudifyEntityException(SpringCrudifyEntityException.ENTITY_NOT_FOUND, "Entity does not exist"); 
		}
		
		this.beforeDelete(tenantId, entity);
		
		this.crudRepository.delete(tenantId, entity);

	}
	
	@Override
	public void deleteEntities(final String tenantId, String domain) throws SpringCrudifyEntityException {
		log.info("[Tenant "+tenantId+"] Deleting all entities for domain "+domain);
		List<T> entities = this.crudRepository.getEntities(tenantId);
		
		for( T s: entities ) {
			try {
				this.beforeDelete(tenantId, s);
				this.crudRepository.delete(tenantId, s);
			} catch (SpringCrudifyEntityException e) {
				throw new SpringCrudifyEntityException(SpringCrudifyEntityException.UNKNOWN_ERROR, "Error during entities deletion"); 
			}
		}
	}
	
	//-----------------------------------------------------------//
	// Abstract method below to be implemented by sub classes    //
	//-----------------------------------------------------------//	

	protected abstract void beforeCreate(String tenantId, T entity) throws SpringCrudifyEntityException;
	protected abstract void beforeUpdate(String tenantId, T entity) throws SpringCrudifyEntityException;
	protected abstract void beforeDelete(String tenantId, T entity) throws SpringCrudifyEntityException;
}
