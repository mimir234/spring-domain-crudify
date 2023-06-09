/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.sdc.spring.domain.crudify.repository;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.sdc.spring.domain.crudify.repository.dao.ISpringCrudifyDAORepository;
import org.sdc.spring.domain.crudify.repository.dto.ISpringCrudifyDTOFactory;
import org.sdc.spring.domain.crudify.repository.dto.ISpringCrudifyDTOObject;
import org.sdc.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.sdc.spring.domain.crudify.spec.ISpringCrudifyEntityFactory;
import org.sdc.spring.domain.crudify.spec.filter.SpringCrudifyLiteral;
import org.sdc.spring.domain.crudify.spec.sort.SpringCrudifySort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableMongoRepositories
public abstract class AbstractSpringCrudifyRepository<Entity extends ISpringCrudifyEntity, Dto extends ISpringCrudifyDTOObject<Entity>> implements ISpringCrudifyRepository<Entity> {
	
	@Inject
	protected ISpringCrudifyDAORepository<Dto> daoRepository;

	protected String domain;

	private ISpringCrudifyEntityFactory<Entity> entityFactory;
	private ISpringCrudifyDTOFactory<Entity, Dto> dtoFactory;
	
	@SuppressWarnings("unchecked")
	@PostConstruct
    protected void getDomain() {
    	
    	Class<Entity> entityClass = this.getEntityClass();
    	Class<Dto> dtoClass = this.getDTOClass();
    	
    	Constructor<Entity> entityConstructor;
    	Constructor<Dto> dtoConstructor;
		try {
			
			entityConstructor = entityClass.getConstructor();
			Entity entity = (Entity) entityConstructor.newInstance();
			if( entity.getDomain().isEmpty() ) {
				this.domain = "unknown";
			} else {
				this.domain = entity.getDomain();
			}
			
			this.entityFactory = (ISpringCrudifyEntityFactory<Entity>) entity.getFactory();
			
			dtoConstructor = dtoClass.getConstructor();
			Dto dto = (Dto) dtoConstructor.newInstance();
			this.dtoFactory = (ISpringCrudifyDTOFactory<Entity, Dto>) dto.getFactory();
			
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			this.domain = "unknown";
			this.entityFactory = null;
		}
    }

	@Override
    public long getCount(String tenantId, SpringCrudifyLiteral filter) {
    	log.info("[Tenant {}] [Domain {}] Get Total Count, Filter {}", tenantId, this.domain, filter);
    	long totalCount = 0;
    	
    	totalCount = this.daoRepository.countByTenantId(tenantId, filter);
    	
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
	public boolean doesExists(String tenantId, Entity entity) {
 
		Dto object = this.dtoFactory.newInstance(tenantId, entity);
		
		log.info("[Tenant {}] [Domain {}] Checking if entity with uuid "+object.getUuid()+" exists.", tenantId, this.domain);
		
		if( this.daoRepository.findOneByUuidAndTenantId(object.getUuid(), object.getTenantId()) != null ){
			log.info("[Tenant "+tenantId+"] Entity with uuid "+object.getUuid()+" exists.");
			return true;
		} 
		log.info("[Tenant {}] [Domain {}] Entity with uuid "+object.getUuid()+" does not exists.", tenantId, this.domain);
		return false;
	}


	@Override
	public List<Entity> getEntities(String tenantId, int pageSize, int pageIndex, SpringCrudifyLiteral filter, SpringCrudifySort sort) {
		log.info("[Tenant {}] [Domain {}] Getting entities", tenantId, this.domain);

		List<Entity> entities = new ArrayList<Entity>();
		List<Dto> objects = null;

		Pageable page = null; 
				
		if( pageSize > 0 ) {
			page = PageRequest.of(pageIndex, pageSize);
		} 
			
		objects = this.daoRepository.findByTenantId(tenantId, page, filter, sort);
	
		objects.forEach(s ->{
			entities.add((Entity) s.convert());
		});
	
		return entities;
	}

	@Override
	public void save(String tenantId, Entity entity) {
		Dto object = this.dtoFactory.newInstance(tenantId, entity);
		log.info("[Tenant {}] [Domain {}] Saving entity with uuid "+object.getUuid()+" exists.", tenantId, this.domain);

		this.daoRepository.save( object );
		
	}

	@Override
	public Entity update(String tenantId, Entity entity) {
		
		Dto object = this.dtoFactory.newInstance(tenantId, entity);

		Dto objectToBeUpdated = this.daoRepository.findOneByUuidAndTenantId(object.getUuid(), object.getTenantId());
		log.info("[Tenant {}] [Domain {}] Updating entity with uuid "+object.getUuid()+" exists.", tenantId, this.domain);
		
		if( objectToBeUpdated != null ){
			
			objectToBeUpdated.update(object);
		
			this.daoRepository.save(objectToBeUpdated);
			
			return (Entity) object.convert();
		
		} else {
			return null;
		}

	}
	
	@Override
	public Entity getOneByUuid(String tenantId, String uuid) {
		log.info("[Tenant {}] [Domain {}] Looking for object with uuid "+uuid, tenantId, this.domain);
		Dto object = this.daoRepository.findOneByUuidAndTenantId(uuid, tenantId);
		
		if( object != null ){
			log.info("[Tenant {}] [Domain {}] Object with uuid "+uuid+" found !", tenantId, this.domain);
			return (Entity) object.convert();
		}
		
		log.info("[Tenant {}] [Domain {}] Object with uuid "+uuid+" not found.", tenantId, this.domain);
		return null;
	}
	
	@Override
	public Entity getOneById(String tenantId, String id) {
		log.info("[Tenant {}] [Domain {}] Looking for object with id "+id, tenantId, this.domain);
		Dto object = this.daoRepository.findOneByIdAndTenantId(id, tenantId);
		
		if( object != null ){
			log.info("[Tenant {}] [Domain {}] Object with id "+id+" found !", tenantId, this.domain);
			return (Entity) object.convert();
		}
		
		log.info("[Tenant {}] [Domain {}] Object with id "+id+" not found.", tenantId, this.domain);
		return null;
	}

	@Override
	public void delete(String tenantId, Entity entity) {
		Dto object = this.dtoFactory.newInstance(tenantId, entity);

		log.info("[Tenant {}] [Domain {}] Deleting entity with Uuid "+object.getUuid(), tenantId, this.domain);
		
		this.daoRepository.delete(object);
	}
	
	protected abstract Class<Dto> getDTOClass();

}
