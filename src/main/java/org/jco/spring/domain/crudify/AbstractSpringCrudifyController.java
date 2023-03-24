/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.jco.spring.domain.crudify;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractSpringCrudifyController<T extends ISpringCrudifyEntity> implements ISpringCrudifyController<T> {

	@Inject 
	protected ISpringCrudifyRepository<T> crudRepository;
	
	@Override
	public T getEntity(String tenantId, String uuid) throws SpringCrudifyEntityException {
		log.info("Getting entity with id "+uuid+" and tenantId "+tenantId);
		T entity = this.crudRepository.getOneByUuid(tenantId, uuid);
		if( entity == null ) {
			throw new SpringCrudifyEntityException(SpringCrudifyEntityException.ENTITY_NOT_FOUND, "Entity does not exist"); 
		}
		return entity;
	}

	@Override
	public T createEntity(String tenantId, T entity, String domain) throws SpringCrudifyEntityException {
		
		log.info("Creating entity of type "+entity.getClass());
		
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
	public List<String> getEntityUuidList(String tenantId) {
		log.info("Getting entities of type ");
		ArrayList<String> entityUuids = new ArrayList<String>();
		List<T> entities = this.crudRepository.getEntities(tenantId);
		
		entities.forEach(e -> {
			entityUuids.add(e.getUuid());
		});
		
		return entityUuids;
	}
	
	@Override
	public List<String> getEntityIdList(String tenantId) {
		log.info("Getting entities of type ");
		ArrayList<String> entityUuids = new ArrayList<String>();
		List<T> entities = this.crudRepository.getEntities(tenantId);
		
		entities.forEach(e -> {
			entityUuids.add(e.getId());
		});
		
		return entityUuids;
	}
	
	@Override
	public List<T> getEntityFullList(String tenantId) {
		log.info("Getting entities of type ");
		List<T> entities = this.crudRepository.getEntities(tenantId);
		return entities;
	}

	@Override
	public T updateEntity(String tenantId, T entity, String domain) throws SpringCrudifyEntityException {
		log.info("Updating entity with id "+entity.getId());
		
		this.beforeUpdate(tenantId, entity);
		
		
		if( !this.crudRepository.doesExists(tenantId, entity) ){
			throw new SpringCrudifyEntityException(SpringCrudifyEntityException.ENTITY_NOT_FOUND, "Entity does not exist"); 
		}

		T updated = this.crudRepository.update(tenantId, entity);
		
		return updated;
	}

	@Override
	public void deleteEntity(String tenantId, String id, String domain) throws SpringCrudifyEntityException {
		log.info("Deleting entity with id "+id);
		
		T entity = this.crudRepository.getOneById(tenantId, id);
		
		if( entity == null ){
			throw new SpringCrudifyEntityException(SpringCrudifyEntityException.ENTITY_NOT_FOUND, "Entity does not exist"); 
		}
		
		this.beforeDelete(tenantId, entity);
		
		this.crudRepository.delete(tenantId, entity);

	}
	
	@Override
	public void deleteEntities(final String tenantId, String domain) {
		log.info("Deleting all entities for domain "+domain);
		List<T> entities = this.crudRepository.getEntities(tenantId);
		
		entities.forEach(s->{
			try {
				this.beforeDelete(tenantId, s);
				this.crudRepository.delete(tenantId, s);
			} catch (SpringCrudifyEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
	
	//-----------------------------------------------------------//
	// Abstract method below to be implemented by sub classes    //
	//-----------------------------------------------------------//	

	protected abstract void beforeCreate(String tenantId, T entity) throws SpringCrudifyEntityException;
	protected abstract void beforeUpdate(String tenantId, T entity) throws SpringCrudifyEntityException;
	protected abstract void beforeDelete(String tenantId, T entity) throws SpringCrudifyEntityException;
}
