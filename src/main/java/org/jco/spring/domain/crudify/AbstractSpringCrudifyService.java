/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.jco.spring.domain.crudify;

import javax.inject.Inject;
import javax.ws.rs.PathParam;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author J.Colombet
 *
 * @param <T>
 */
@Slf4j
public abstract class AbstractSpringCrudifyService<T extends ISpringCrudifyEntity> {

	protected static final String SUCCESSFULLY_DELETED = "Ressource has been successfully deleted";

	protected static final String NOT_IMPLEMENTED = "This function is not implemented";
	
	protected boolean AUTHORIZE_CREATION = false;
	protected boolean AUTHORIZE_GET_ALL = false;
	protected boolean AUTHORIZE_GET_ONE = false;
	protected boolean AUTHORIZE_UPDATE = false;
	protected boolean AUTHORIZE_DELETE_ONE = false;
	protected boolean AUTHORIZE_DELETE_ALL = false;
	
	protected abstract void defineAuthorizations();
	
	@PostConstruct
	public void init() {
		this.defineAuthorizations();
		this.setDomain();
	}
	
	protected abstract void setDomain();

	@Inject
	private ISpringCrudifyController<T> crudController;

	protected String domain;

	/**
	 * Creates an entity.
	 * @param Customer
	 * @return
	 */
    @RequestMapping(value = "", method = RequestMethod.POST)
    private ResponseEntity<?> createEntity(@RequestBody T entity, @RequestHeader String tenantId) {
    	ResponseEntity<?> response = null;
    	
    	if( this.AUTHORIZE_CREATION ) {
	    	try {
				entity = this.crudController.createEntity(tenantId, entity, this.domain);
				response = new ResponseEntity<>(entity, HttpStatus.CREATED);
			} catch (SpringCrudifyEntityException e) {
				response = new ResponseEntity<>(new ISpringCrudifyErrorObject(e.getMessage()), this.getHttpErrorCodeFromEntityExceptionCode(e));
			}
    	} else {
    		response = new ResponseEntity<>(new ISpringCrudifyErrorObject(NOT_IMPLEMENTED), HttpStatus.NOT_IMPLEMENTED);
    	}

        return response;
    }

	/**
     * Get a list of entities.
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    private ResponseEntity<?> getEntities(@RequestHeader String tenantId, @PathParam(value = "mode") SpringCrudifyReadOutputMode mode){
    	
		if ( this.AUTHORIZE_GET_ALL ) {
			
			Object entities = null; 
			
			switch( mode ) {
			default:
			case uuid:
				entities = this.crudController.getEntityUuidList(tenantId);
				break;
			case id:
				entities = this.crudController.getEntityIdList(tenantId);
				break;
			case full:
				entities = this.crudController.getEntityFullList(tenantId);
				break;
			}

			return new ResponseEntity<>(entities, HttpStatus.OK);

		} else {
			return new ResponseEntity<>(new ISpringCrudifyErrorObject(NOT_IMPLEMENTED), HttpStatus.NOT_IMPLEMENTED);
		}
    	
    }
    
    /**
     * Get one entity.
     * @return
     */
    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
    private ResponseEntity<?> getEntity(@RequestHeader String tenantId, @PathVariable String uuid){
    	ResponseEntity<?> response = null;
    	
		if ( this.AUTHORIZE_GET_ONE ) {
			T entity;
			try {
				entity = this.crudController.getEntity(tenantId, uuid);
				response = new ResponseEntity<>(entity, HttpStatus.OK);
			} catch (SpringCrudifyEntityException e) {
				response = new ResponseEntity<>(new ISpringCrudifyErrorObject(e.getMessage()),
						this.getHttpErrorCodeFromEntityExceptionCode(e));
			}
		} else {
			return new ResponseEntity<>(new ISpringCrudifyErrorObject(NOT_IMPLEMENTED), HttpStatus.NOT_IMPLEMENTED);
		}
		return response;
    	
    }
  
    /**
     * Update an entity.
     * @return
     */
    @RequestMapping(value = "/{uuid}", method = RequestMethod.PATCH)
    private ResponseEntity<?> updateEntity(@PathVariable String uuid, @RequestBody T entity, @RequestHeader String tenantId){
    	
		ResponseEntity<?> response = null;

		if ( this.AUTHORIZE_UPDATE ) {
			try {
				entity.setId(uuid);
				T updatedEntity = this.crudController.updateEntity(tenantId, entity, this.domain);
				response = new ResponseEntity<>(updatedEntity, HttpStatus.OK);
			} catch (SpringCrudifyEntityException e) {
				response = new ResponseEntity<>(new ISpringCrudifyErrorObject(e.getMessage()),
						this.getHttpErrorCodeFromEntityExceptionCode(e));
			}

		} else {
			response = new ResponseEntity<>(new ISpringCrudifyErrorObject(NOT_IMPLEMENTED), HttpStatus.NOT_IMPLEMENTED);
		}

        return response;
    }
    
    
    /**
     * Delete an entity.
     * @return
     */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE)
	private ResponseEntity<?> deleteEntity(@PathVariable String uuid, @RequestHeader String tenantId) {

		if (this.AUTHORIZE_DELETE_ONE) {
			ResponseEntity<?> response = null;

			try {
				this.crudController.deleteEntity(tenantId, uuid, this.domain);
				response = new ResponseEntity<>(new ISpringCrudifyErrorObject(SUCCESSFULLY_DELETED), HttpStatus.OK);
			} catch (SpringCrudifyEntityException e) {
				response = new ResponseEntity<>(new ISpringCrudifyErrorObject(e.getMessage()), HttpStatus.NOT_ACCEPTABLE);
			}

			return response;

		} else {
			return new ResponseEntity<>(new ISpringCrudifyErrorObject(NOT_IMPLEMENTED), HttpStatus.NOT_IMPLEMENTED);
		}
	}
    
    /**
     * Delete all the entities.
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    private ResponseEntity<?> deleteAll(@RequestHeader String tenantId){
    
		if ( this.AUTHORIZE_DELETE_ALL ) {
	    	ResponseEntity<?> response = null;
	
			this.crudController.deleteEntities(tenantId, this.domain);
			response = new ResponseEntity<>(new ISpringCrudifyErrorObject(SUCCESSFULLY_DELETED), HttpStatus.OK);
	
	        return response;

		} else {
			return new ResponseEntity<>(new ISpringCrudifyErrorObject(NOT_IMPLEMENTED), HttpStatus.NOT_IMPLEMENTED);
		}
    }
    
    /**
     * 
     * @param e
     * @return
     */
    protected HttpStatus getHttpErrorCodeFromEntityExceptionCode(SpringCrudifyEntityException e) {
		switch( e.getCode() ){
		default:
		case SpringCrudifyEntityException.BAD_REQUEST:
			return HttpStatus.BAD_REQUEST;
		case SpringCrudifyEntityException.ENTITY_NOT_FOUND:
			return HttpStatus.NOT_FOUND;
		}
	}
	
}
