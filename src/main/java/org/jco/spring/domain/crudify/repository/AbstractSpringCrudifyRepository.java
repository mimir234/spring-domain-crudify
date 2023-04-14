/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.jco.spring.domain.crudify.repository;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jco.spring.domain.crudify.repository.dao.ISpringCrudifyDAORepository;
import org.jco.spring.domain.crudify.repository.dto.AbstractSpringCrudifyDTOObject;
import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntityFactory;
import org.jco.spring.domain.crudify.spec.filter.SpringCrudifyLiteral;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("rawtypes")
@Slf4j
@EnableMongoRepositories
public abstract class AbstractSpringCrudifyRepository<T extends ISpringCrudifyEntity, S extends AbstractSpringCrudifyDTOObject> implements ISpringCrudifyRepository<T> {
	
	@Inject
	protected ISpringCrudifyDAORepository<S> daoRepository;
	
    protected Class<T> clazz;

	private String domain;

	private ISpringCrudifyEntityFactory<T> factory;
	
	@Inject
	MongoTemplate template;
	
    @SuppressWarnings("unchecked")
	@PostConstruct
    private void getDomain() {
    	this.setEntityClazz();
    	Constructor<T> constructor;
		try {
			
			constructor = this.clazz.getConstructor();
			T entity = (T) constructor.newInstance();
			if( entity.getDomain().isEmpty() ) {
				this.domain = "unknown";
			} else {
				this.domain = entity.getDomain();
			}
			
			this.factory = (ISpringCrudifyEntityFactory<T>) entity.getFactory();
			
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			this.domain = "unknown";
			this.factory = null;
		}
    }
    
    @Override
    public long getTotalCount(String tenantId) {
    	log.info("[Tenant {}] [Domain {}] Get Total Count.", tenantId, this.domain);
    	long totalCount = 0;
    	
    	totalCount = this.daoRepository.countByTenantId(tenantId);
    	
    	return totalCount;
    }

	@Override
	public boolean doesExists(String tenantId, String uuid) {
 
		log.info("[Tenant {}] [Domain {}] Checking if entity with uuid {} exists.", tenantId, this.domain);
		
		if( this.daoRepository.findOneByUuidAndTenantId(uuid, tenantId) != null ){
			log.info("Entity with uuid "+uuid+" exists.");
			return true;
		} 
		log.info("[Tenant {}] [Domain {}] Entity with uuid "+uuid+" does not exists.", tenantId, this.domain);
		return false;
	}
	
	@Override
	public boolean doesExists(String tenantId, T entity) {
 
		S object = this.convertToDTOObject(tenantId, entity);
		
		log.info("[Tenant {}] [Domain {}] Checking if entity with uuid "+object.getTechUuid()+" exists.", tenantId, this.domain);
		
		if( this.daoRepository.findOneByUuidAndTenantId(object.getId(), object.getTenantId()) != null ){
			log.info("[Tenant "+tenantId+"] Entity with uuid "+object.getTechUuid()+" exists.");
			return true;
		} 
		log.info("[Tenant {}] [Domain {}] Entity with uuid "+object.getTechUuid()+" does not exists.", tenantId, this.domain);
		return false;
	}


	@Override
	public List<T> getEntities(String tenantId, int pageSize, int pageIndex, SpringCrudifyLiteral  filter) {
		log.info("[Tenant {}] [Domain {}] Getting entities", tenantId, this.domain);

		List<T> entities = new ArrayList<T>();
		List<S> objects = null;

		Pageable page = null; 
				
		if( pageSize > 0 ) {
			page = PageRequest.of(pageIndex, pageSize);
		} 
			
		objects = this.daoRepository.findByTenantId(tenantId, page, filter);
	
		objects.forEach(s ->{
			entities.add(this.convertToEntity(s));
		});
	
		return entities;
	}

	@Override
	public void save(String tenantId, T entity) {
		S object = this.convertToDTOObject(tenantId, entity);
		log.info("[Tenant {}] [Domain {}] Saving entity with uuid "+object.getTechUuid()+" exists.", tenantId, this.domain);

		this.daoRepository.save( object );
		
	}

	@Override
	public T update(String tenantId, T entity) {
		
		S object = this.convertToDTOObject(tenantId, entity);
		
		S objectToBeUpdated = this.daoRepository.findOneByUuidAndTenantId(object.getTechUuid(), object.getTenantId());
		log.info("[Tenant {}] [Domain {}] Updating entity with uuid "+object.getTechUuid()+" exists.", tenantId, this.domain);
		
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
		log.info("[Tenant {}] [Domain {}] Looking for object with uuid "+uuid, tenantId, this.domain);
		S object = this.daoRepository.findOneByUuidAndTenantId(uuid, tenantId);
		
		if( object != null ){
			log.info("[Tenant {}] [Domain {}] Object with uuid "+uuid+" found !", tenantId, this.domain);
			return this.convertToEntity(object);
		}
		
		log.info("[Tenant {}] [Domain {}] Object with uuid "+uuid+" not found.", tenantId, this.domain);
		return null;
	}
	
	@Override
	public T getOneById(String tenantId, String id) {
		log.info("[Tenant {}] [Domain {}] Looking for object with id "+id, tenantId, this.domain);
		S object = this.daoRepository.findOneByIdAndTenantId(id, tenantId);
		
		if( object != null ){
			log.info("[Tenant {}] [Domain {}] Object with id "+id+" found !", tenantId, this.domain);
			return this.convertToEntity(object);
		}
		
		log.info("[Tenant {}] [Domain {}] Object with id "+id+" not found.", tenantId, this.domain);
		return null;
	}

	@Override
	public void delete(String tenantId, T entity) {
		S object = this.convertToDTOObject(tenantId, entity);
		log.info("[Tenant {}] [Domain {}] Deleting entity with Uuid "+object.getTechUuid(), tenantId, this.domain);
		
		this.daoRepository.delete(object);
	}
	

	//-----------------------------------------------------------//
	// Abstract method below to be implemented by sub classes    //
	//-----------------------------------------------------------//	

	protected abstract S convertToDTOObject(String tenantId, T entity);

	protected abstract T convertToEntity(S s);
	
	protected abstract void update(S objectToBeUpdated, S object);
	
}
