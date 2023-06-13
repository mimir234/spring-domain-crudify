/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.sdc.spring.domain.crudify.ws;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.sdc.spring.domain.crudify.controller.ISpringCrudifyController;
import org.sdc.spring.domain.crudify.security.authorization.BasicSpringCrudifyAuthorization;
import org.sdc.spring.domain.crudify.security.authorization.ISpringCrudifyAuthorization;
import org.sdc.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.sdc.spring.domain.crudify.spec.ISpringCrudifyEntityFactory;
import org.sdc.spring.domain.crudify.spec.SpringCrudifyEntityException;
import org.sdc.spring.domain.crudify.spec.SpringCrudifyReadOutputMode;
import org.sdc.spring.domain.crudify.spec.filter.SpringCrudifyLiteral;
import org.sdc.spring.domain.crudify.spec.sort.SpringCrudifySort;
import org.springframework.http.HttpMethod;
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
 * @param <Entity>
 */
@Slf4j
public abstract class AbstractSpringCrudifyService<Entity extends ISpringCrudifyEntity> implements ISpringCrudifyRestService<Entity> {

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
	
	protected abstract List<ISpringCrudifyAuthorization> createCustomAuthorizations();
	
	@Getter
	protected String domain;

	private ISpringCrudifyEntityFactory<Entity> factory;

	private ArrayList<ISpringCrudifyAuthorization> authorizations;

	@SuppressWarnings("unchecked")
	@PostConstruct
	protected void init() {
		this.defineAuthorizations();
		Class<Entity> clazz = this.getEntityClazz();
		Constructor<Entity> constructor;
		try {

			constructor = (Constructor<Entity>) clazz.getConstructor();
			Entity entity = (Entity) constructor.newInstance();
			if (entity.getDomain().isEmpty()) {
				this.domain = "unknown";
			} else {
				this.domain = entity.getDomain();
			}

			this.factory = (ISpringCrudifyEntityFactory<Entity>) entity.getFactory();

		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			this.domain = "unknown";
			this.factory = null;
		}
	}

	protected abstract Class<Entity> getEntityClazz();

	@Override
	public List<ISpringCrudifyAuthorization> createAuthorizations() {
		if( this.authorizations == null ) {
			this.authorizations = new ArrayList<ISpringCrudifyAuthorization>();
			
			this.authorizations.add(new BasicSpringCrudifyAuthorization("/"+this.domain.toLowerCase(), this.domain.toLowerCase()+"-read", HttpMethod.GET));
			this.authorizations.add(new BasicSpringCrudifyAuthorization("/"+this.domain.toLowerCase(), this.domain.toLowerCase()+"-create", HttpMethod.POST));
			this.authorizations.add(new BasicSpringCrudifyAuthorization("/"+this.domain.toLowerCase(), this.domain.toLowerCase()+"-delete-all", HttpMethod.DELETE));	
			this.authorizations.add(new BasicSpringCrudifyAuthorization("/"+this.domain.toLowerCase()+"/count", this.domain.toLowerCase()+"-get-count", HttpMethod.GET));
			this.authorizations.add(new BasicSpringCrudifyAuthorization("/"+this.domain.toLowerCase()+"/*", this.domain.toLowerCase()+"-read", HttpMethod.GET));
			this.authorizations.add(new BasicSpringCrudifyAuthorization("/"+this.domain.toLowerCase()+"/*", this.domain.toLowerCase()+"-update", HttpMethod.PATCH));
			this.authorizations.add(new BasicSpringCrudifyAuthorization("/"+this.domain.toLowerCase()+"/*", this.domain.toLowerCase()+"-delete-one", HttpMethod.DELETE));
			
			if( this.createCustomAuthorizations() != null ) {
				this.authorizations.addAll(this.createCustomAuthorizations());
			}
		}
		return authorizations;
	}
	
	@Inject
	protected ISpringCrudifyController<Entity> crudController;

	/**
	 * Creates an entity.
	 * 
	 * @param
	 * @return
	 */
	@Override
	public ResponseEntity<?> createEntity(Entity entity, String tenantId) {
		ResponseEntity<?> response = null;

		if (this.AUTHORIZE_CREATION) {
			try {
				entity = this.crudController.createEntity(tenantId, entity);
				response = new ResponseEntity<>(entity, HttpStatus.CREATED);
			} catch (SpringCrudifyEntityException e) {
				response = new ResponseEntity<>(new ISpringCrudifyErrorObject(e.getMessage()),
						this.getHttpErrorCodeFromEntityExceptionCode(e));
			} catch (Exception e) {
				return new ResponseEntity<>(new ISpringCrudifyErrorObject(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
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
	@Override
	@SuppressWarnings("unchecked")
	public ResponseEntity<?> getEntities( String tenantId, SpringCrudifyReadOutputMode mode, Integer pageSize, Integer pageIndex, String filterString, String sortString) {

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
			} catch (Exception e) {
				return new ResponseEntity<>(new ISpringCrudifyErrorObject(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
			}

			if (pageSize > 0) {
				long totalCount = 0;
				try {
					totalCount = this.crudController.getEntityTotalCount(tenantId, filter);
				} catch (SpringCrudifyEntityException e) {
					return new ResponseEntity<>(new ISpringCrudifyErrorObject(e.getMessage()),
							this.getHttpErrorCodeFromEntityExceptionCode(e));
				} catch (Exception e) {
					return new ResponseEntity<>(new ISpringCrudifyErrorObject(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
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
	@Override
	public ResponseEntity<?> getEntity(String tenantId, String uuid) {
		ResponseEntity<?> response = null;

		if (this.AUTHORIZE_GET_ONE) {
			Entity entity;
			try {
				entity = this.crudController.getEntity(tenantId, uuid);
				response = new ResponseEntity<>(entity, HttpStatus.OK);
			} catch (SpringCrudifyEntityException e) {
				response = new ResponseEntity<>(new ISpringCrudifyErrorObject(e.getMessage()),
						this.getHttpErrorCodeFromEntityExceptionCode(e));
			} catch (Exception e) {
				return new ResponseEntity<>(new ISpringCrudifyErrorObject(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
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
	@Override
	public ResponseEntity<?> updateEntity(String uuid, Entity entity, String tenantId) {

		ResponseEntity<?> response = null;

		if (this.AUTHORIZE_UPDATE) {
			try {
				entity.setUuid(uuid);
				Entity updatedEntity = this.crudController.updateEntity(tenantId, entity);
				response = new ResponseEntity<>(updatedEntity, HttpStatus.OK);
			} catch (SpringCrudifyEntityException e) {
				response = new ResponseEntity<>(new ISpringCrudifyErrorObject(e.getMessage()),
						this.getHttpErrorCodeFromEntityExceptionCode(e));
			} catch (Exception e) {
				return new ResponseEntity<>(new ISpringCrudifyErrorObject(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
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
	@Override
	public ResponseEntity<?> deleteEntity(@PathVariable(name = "uuid") String uuid, @RequestHeader(name = "tenantId") String tenantId) {

		if (this.AUTHORIZE_DELETE_ONE) {
			ResponseEntity<?> response = null;

			try {
				this.crudController.deleteEntity(tenantId, uuid);
				response = new ResponseEntity<>(new ISpringCrudifyErrorObject(SUCCESSFULLY_DELETED), HttpStatus.OK);
			} catch (SpringCrudifyEntityException e) {
				response = new ResponseEntity<>(new ISpringCrudifyErrorObject(e.getMessage()),
						HttpStatus.NOT_ACCEPTABLE);
			} catch (Exception e) {
				return new ResponseEntity<>(new ISpringCrudifyErrorObject(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
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
	@Override
	public ResponseEntity<?> deleteAll(String tenantId) {

		if (this.AUTHORIZE_DELETE_ALL) {
			ResponseEntity<?> response = null;

			try {
				this.crudController.deleteEntities(tenantId);
				response = new ResponseEntity<>(new ISpringCrudifyErrorObject(SUCCESSFULLY_DELETED), HttpStatus.OK);
			} catch (SpringCrudifyEntityException e) {
				response = new ResponseEntity<>(new ISpringCrudifyErrorObject(e.getMessage()),
						this.getHttpErrorCodeFromEntityExceptionCode(e));
			} catch (Exception e) {
				return new ResponseEntity<>(new ISpringCrudifyErrorObject(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
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
	@Override
	public ResponseEntity<?> getCount(@RequestHeader(name = "tenantId") String tenantId) {

		if (this.AUTHORIZE_COUNT) {
			ResponseEntity<?> response = null;

			try {
				long count = this.crudController.getEntityTotalCount(tenantId, null);
				response = new ResponseEntity<>(count, HttpStatus.OK);
			} catch (SpringCrudifyEntityException e) {
				response = new ResponseEntity<>(new ISpringCrudifyErrorObject(e.getMessage()),
						this.getHttpErrorCodeFromEntityExceptionCode(e));
			} catch (Exception e) {
				return new ResponseEntity<>(new ISpringCrudifyErrorObject(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
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
