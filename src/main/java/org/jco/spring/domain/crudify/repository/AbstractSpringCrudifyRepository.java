/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.jco.spring.domain.crudify.repository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jco.spring.domain.crudify.repository.dao.ISpringCrudifyDAORepository;
import org.jco.spring.domain.crudify.repository.dto.AbstractSpringCrudifyDTOObject;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("rawtypes")
@Slf4j
@EnableMongoRepositories
public abstract class AbstractSpringCrudifyRepository<T, S extends AbstractSpringCrudifyDTOObject> implements ISpringCrudifyRepository<T> {
	
	@Inject
	protected ISpringCrudifyDAORepository<S> daoRepository;

	@Override
	public boolean doesExists(String tenantId, String uuid) {
 
		log.info("[Tenant "+tenantId+"] Checking if entity with uuid "+uuid+" exists.");
		
		if( this.daoRepository.findOneByUuidAndTenantId(uuid, tenantId) != null ){
			log.info("Entity with uuid "+uuid+" exists.");
			return true;
		} 
		log.info("[Tenant "+tenantId+"] Entity with uuid "+uuid+" does not exists.");
		return false;
	}
	
	@Override
	public boolean doesExists(String tenantId, T entity) {
 
		S object = this.convertToDTOObject(tenantId, entity);
		
		log.info("[Tenant "+tenantId+"] Checking if entity with uuid "+object.getUuid()+" exists.");
		
		if( this.daoRepository.findOneByUuidAndTenantId(object.getId(), object.getTenantId()) != null ){
			log.info("[Tenant "+tenantId+"] Entity with uuid "+object.getUuid()+" exists.");
			return true;
		} 
		log.info("[Tenant "+tenantId+"] Entity with uuid "+object.getUuid()+" does not exists.");
		return false;
	}


	@Override
	public List<T> getEntities(String tenantId) {
		log.info("[Tenant "+tenantId+"] getting entities");

		List<T> entities = new ArrayList<T>();
		List<S> objects = this.daoRepository.findByTenantId(tenantId);
		
		objects.forEach(s ->{
			entities.add(this.convertToEntity(s));
		});
	
		return entities;
	}

	@Override
	public void save(String tenantId, T entity) {
		S object = this.convertToDTOObject(tenantId, entity);
		log.info("[Tenant "+tenantId+"] Saving entity with uuid "+object.getUuid()+" exists.");

		this.daoRepository.save( object );
		
	}

	@Override
	public T update(String tenantId, T entity) {
		
		S object = this.convertToDTOObject(tenantId, entity);
		
		S objectToBeUpdated = this.daoRepository.findOneByUuidAndTenantId(object.getUuid(), object.getTenantId());
		log.info("[Tenant "+tenantId+"] Updating entity with uuid "+object.getUuid()+" exists.");
		
		if( objectToBeUpdated != null ){
			
			this.update(objectToBeUpdated, object);
			
			this.daoRepository.save(objectToBeUpdated);
			
			return this.convertToEntity(objectToBeUpdated);
		
		} else {
			return null;
		}

	}
	
	@Override
	public T getOneByUuid(String tenantId, String uuid) {
		log.info("[Tenant "+tenantId+"] Looking for object with uuid "+uuid);
		S object = this.daoRepository.findOneByUuidAndTenantId(uuid, tenantId);
		
		if( object != null ){
			log.info("[Tenant "+tenantId+"] Object with uuid "+uuid+" found !");
			return this.convertToEntity(object);
		}
		
		log.info("[Tenant "+tenantId+"] Object with uuid "+uuid+" not found.");
		return null;
	}
	
	@Override
	public T getOneById(String tenantId, String id) {
		log.info("[Tenant "+tenantId+"] Looking for object with id "+id);
		S object = this.daoRepository.findOneByIdAndTenantId(id, tenantId);
		
		if( object != null ){
			log.info("[Tenant "+tenantId+"] Object with id "+id+" found !");
			return this.convertToEntity(object);
		}
		
		log.info("[Tenant "+tenantId+"] Object with id "+id+" not found.");
		return null;
	}

	@Override
	public void delete(String tenantId, T entity) {
		S object = this.convertToDTOObject(tenantId, entity);
		log.info("[Tenant "+tenantId+"] Deleting entity with Uuid "+object.getUuid());
		
		this.daoRepository.delete(object);
	}
	

	//-----------------------------------------------------------//
	// Abstract method below to be implemented by sub classes    //
	//-----------------------------------------------------------//	

	protected abstract S convertToDTOObject(String tenantId, T entity);

	protected abstract T convertToEntity(S s);
	
	protected abstract void update(S objectToBeUpdated, S object);

}
