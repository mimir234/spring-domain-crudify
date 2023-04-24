/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.jco.spring.domain.crudify.ws;

import java.util.List;

import javax.inject.Inject;

import org.jco.spring.domain.crudify.controller.ISpringCrudifyController;
import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.jco.spring.domain.crudify.spec.SpringCrudifyEntityException;
import org.jco.spring.domain.crudify.spec.SpringCrudifyReadOutputMode;
import org.jco.spring.domain.crudify.spec.filter.SpringCrudifyLiteral;
import org.jco.spring.domain.crudify.spec.sort.SpringCrudifySort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
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
	protected static final String FILTER_ERROR = "The filter has error";

	protected boolean AUTHORIZE_CREATION = false;
	protected boolean AUTHORIZE_GET_ALL = false;
	protected boolean AUTHORIZE_GET_ONE = false;
	protected boolean AUTHORIZE_UPDATE = false;
	protected boolean AUTHORIZE_DELETE_ONE = false;
	protected boolean AUTHORIZE_DELETE_ALL = false;
	protected boolean AUTHORIZE_COUNT = false;

	protected abstract void defineAuthorizations();

	@PostConstruct
	public void init() {
		this.defineAuthorizations();
	}

	@Inject
	private ISpringCrudifyController<T> crudController;

	@Getter
	protected String domain;

	/**
	 * Creates an entity.
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	private ResponseEntity<?> createEntity(@RequestBody T entity, @RequestHeader String tenantId) {
		ResponseEntity<?> response = null;

		if (this.AUTHORIZE_CREATION) {
			try {
				entity = this.crudController.createEntity(tenantId, entity);
				response = new ResponseEntity<>(entity, HttpStatus.CREATED);
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
	 * Get a list of entities.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "", method = RequestMethod.GET)
	private ResponseEntity<?> getEntities(@RequestHeader String tenantId,
			@RequestParam(value = "mode", defaultValue = "full") SpringCrudifyReadOutputMode mode,
			@RequestParam(value = "pageSize", defaultValue = "0") int pageSize,
			@RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
			@RequestParam(value = "filter", defaultValue = "") String filterString,
			@RequestParam(value = "sort", defaultValue = "") String sortString) {

		if (this.AUTHORIZE_GET_ALL) {

			Object entities = null;
			
			ObjectMapper mapper = new ObjectMapper();
			SpringCrudifyLiteral filter = null;
			SpringCrudifySort sort = null;
			try {
				if( filterString != null && !filterString.isEmpty() ) {
					filter = mapper.readValue(filterString, SpringCrudifyLiteral.class);
				}
				if( sortString != null && !sortString.isEmpty() ) {
					sort = mapper.readValue(sortString, SpringCrudifySort.class);
				}
			} catch (JsonProcessingException e) {
				return new ResponseEntity<>(new ISpringCrudifyErrorObject("Error parsing request param : "+e.getMessage()), HttpStatus.BAD_REQUEST);
			}

			try {
				entities = this.crudController.getEntityList(tenantId, pageSize, pageIndex, filter, sort, mode);
			} catch (SpringCrudifyEntityException e) {
				return new ResponseEntity<>(new ISpringCrudifyErrorObject(e.getMessage()),
						this.getHttpErrorCodeFromEntityExceptionCode(e));
			}

			if (pageSize > 0) {
				long totalCount = 0;
				try {
					totalCount = this.crudController.getEntityTotalCount(tenantId);
				} catch (SpringCrudifyEntityException e) {
					return new ResponseEntity<>(new ISpringCrudifyErrorObject(e.getMessage()),
							this.getHttpErrorCodeFromEntityExceptionCode(e));
				}

				SpringCrudifyWsPage page = new SpringCrudifyWsPage(totalCount, ((List<Object>) entities));

				return new ResponseEntity<>(page, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(entities, HttpStatus.OK);
			}

		} else {
			return new ResponseEntity<>(new ISpringCrudifyErrorObject(NOT_IMPLEMENTED), HttpStatus.NOT_IMPLEMENTED);
		}

	}

	/**
	 * Get one entity.
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	private ResponseEntity<?> getEntity(@RequestHeader String tenantId, @PathVariable String uuid) {
		ResponseEntity<?> response = null;

		if (this.AUTHORIZE_GET_ONE) {
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
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.PATCH)
	private ResponseEntity<?> updateEntity(@PathVariable String uuid, @RequestBody T entity,
			@RequestHeader String tenantId) {

		ResponseEntity<?> response = null;

		if (this.AUTHORIZE_UPDATE) {
			try {
				entity.setUuid(uuid);
				T updatedEntity = this.crudController.updateEntity(tenantId, entity);
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
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE)
	private ResponseEntity<?> deleteEntity(@PathVariable String uuid, @RequestHeader String tenantId) {

		if (this.AUTHORIZE_DELETE_ONE) {
			ResponseEntity<?> response = null;

			try {
				this.crudController.deleteEntity(tenantId, uuid);
				response = new ResponseEntity<>(new ISpringCrudifyErrorObject(SUCCESSFULLY_DELETED), HttpStatus.OK);
			} catch (SpringCrudifyEntityException e) {
				response = new ResponseEntity<>(new ISpringCrudifyErrorObject(e.getMessage()),
						HttpStatus.NOT_ACCEPTABLE);
			}

			return response;

		} else {
			return new ResponseEntity<>(new ISpringCrudifyErrorObject(NOT_IMPLEMENTED), HttpStatus.NOT_IMPLEMENTED);
		}
	}

	/**
	 * Delete all the entities.
	 * 
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.DELETE)
	private ResponseEntity<?> deleteAll(@RequestHeader String tenantId) {

		if (this.AUTHORIZE_DELETE_ALL) {
			ResponseEntity<?> response = null;

			try {
				this.crudController.deleteEntities(tenantId);
				response = new ResponseEntity<>(new ISpringCrudifyErrorObject(SUCCESSFULLY_DELETED), HttpStatus.OK);
			} catch (SpringCrudifyEntityException e) {
				response = new ResponseEntity<>(new ISpringCrudifyErrorObject(e.getMessage()),
						this.getHttpErrorCodeFromEntityExceptionCode(e));
			}

			return response;

		} else {
			return new ResponseEntity<>(new ISpringCrudifyErrorObject(NOT_IMPLEMENTED), HttpStatus.NOT_IMPLEMENTED);
		}
	}

	/**
	 * Get count of entities
	 * 
	 * @return
	 */
	@RequestMapping(value = "/count", method = RequestMethod.DELETE)
	private ResponseEntity<?> getCount(@RequestHeader String tenantId) {

		if (this.AUTHORIZE_COUNT) {
			ResponseEntity<?> response = null;

			try {
				long count = this.crudController.getEntityTotalCount(tenantId);
				response = new ResponseEntity<>(count, HttpStatus.OK);
			} catch (SpringCrudifyEntityException e) {
				response = new ResponseEntity<>(new ISpringCrudifyErrorObject(e.getMessage()),
						this.getHttpErrorCodeFromEntityExceptionCode(e));
			}

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
		switch (e.getCode()) {
		default:
		case SpringCrudifyEntityException.BAD_REQUEST:
			return HttpStatus.BAD_REQUEST;
		case SpringCrudifyEntityException.ENTITY_NOT_FOUND:
			return HttpStatus.NOT_FOUND;
		}
	}

}
