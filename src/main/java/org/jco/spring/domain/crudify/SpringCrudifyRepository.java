/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.jco.spring.domain.crudify;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("rawtypes")
@Slf4j
@EnableMongoRepositories
public abstract class SpringCrudifyRepository<T, S extends AbstractSpringCrudifyDTOObject> implements ISpringCrudifyRepository<T>, ISpringCrudifyDatableRepository<T> {
	
	@Inject
	protected ISpringCrudifyMongoRepository<S> mongoCrudRepository;

	@Override
	public boolean doesExists(String tenantId, String uuid) {
 
		log.info("Checking if entity with uuid "+uuid+" exists.");
		
		if( this.mongoCrudRepository.findOneByUuidAndTenantId(uuid, tenantId) != null ){
			log.info("Entity with uuid "+uuid+" exists.");
			return true;
		} 
		log.info("Entity with uuid "+uuid+" does not exists.");
		return false;
	}
	
	@Override
	public boolean doesExists(String tenantId, T entity) {
 
		S object = this.convertToDTOObject(tenantId, entity);
		
		log.info("Checking if entity with uuid "+object.getUuid()+" exists.");
		
		if( this.mongoCrudRepository.findOneByUuidAndTenantId(object.getId(), object.getTenantId()) != null ){
			log.info("Entity with uuid "+object.getUuid()+" exists.");
			return true;
		} 
		log.info("Entity with uuid "+object.getUuid()+" does not exists.");
		return false;
	}


	@Override
	public List<T> getEntities(String tenantId) {
		List<T> entities = new ArrayList<T>();
		List<S> objects = this.mongoCrudRepository.findByTenantId(tenantId);
		
		objects.forEach(s ->{
			entities.add(this.convertToEntity(s));
		});
	
		return entities;
	}

	@Override
	public void save(String tenantId, T entity) {
		S object = this.convertToDTOObject(tenantId, entity);
		log.info("Saving entity with uuid "+object.getUuid()+" exists.");

		this.mongoCrudRepository.save( object );
		
	}

	@Override
	public T update(String tenantId, T entity) {
		
		S object = this.convertToDTOObject(tenantId, entity);
		
		S objectToBeUpdated = this.mongoCrudRepository.findOneByUuidAndTenantId(object.getUuid(), object.getTenantId());
		log.info("Updating entity with uuid "+object.getUuid()+" exists.");
		
		if( objectToBeUpdated != null ){
			
			this.update(objectToBeUpdated, object);
			
			this.mongoCrudRepository.save(objectToBeUpdated);
			
			return this.convertToEntity(objectToBeUpdated);
		
		} else {
			return null;
		}

	}
	
	@Override
	public T getOneByUuid(String tenantId, String uuid) {
		log.info("Looking for object with uuid "+uuid);
		S object = this.mongoCrudRepository.findOneByUuidAndTenantId(uuid, tenantId);
		
		if( object != null ){
			log.info("Object with uuid "+uuid+" found !");
			return this.convertToEntity(object);
		}
		
		log.info("Object with uuid "+uuid+" not found.");
		return null;
	}
	
	@Override
	public T getOneById(String tenantId, String id) {
		log.info("Looking for object with id "+id);
		S object = this.mongoCrudRepository.findOneByIdAndTenantId(id, tenantId);
		
		if( object != null ){
			log.info("Object with id "+id+" found !");
			return this.convertToEntity(object);
		}
		
		log.info("Object with id "+id+" not found.");
		return null;
	}

	@Override
	public void delete(String tenantId, T entity) {
		S object = this.convertToDTOObject(tenantId, entity);
		
		this.mongoCrudRepository.delete(object);

	}

	//-----------------------------------------------------------//
	// Abstract method below to be implemented by sub classes    //
	//-----------------------------------------------------------//	

	protected abstract S convertToDTOObject(String tenantId, T entity);

	protected abstract T convertToEntity(S s);
	
	protected abstract void update(S objectToBeUpdated, S object);

}
