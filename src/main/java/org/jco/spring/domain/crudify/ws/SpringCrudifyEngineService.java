package org.jco.spring.domain.crudify.ws;

import java.util.List;

import org.jco.spring.domain.crudify.controller.ISpringCrudifyController;
import org.jco.spring.domain.crudify.security.authorization.ISpringCrudifyAuthorization;
import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Setter;

@SuppressWarnings("unchecked")
public class SpringCrudifyEngineService extends AbstractSpringCrudifyService<ISpringCrudifyEntity> {

	public void setCrudController(ISpringCrudifyController<ISpringCrudifyEntity> crudController) {
		this.crudController = crudController;
	}
	
	@Setter
	protected Class<?> entityClass;

	public SpringCrudifyEngineService(Class<?> entityClass, ISpringCrudifyController<ISpringCrudifyEntity> crudController) {
		this.entityClass = entityClass;
		this.crudController = crudController;
		
		this.getDomain();
		this.init();
	}
	
	@Override
	protected void defineAuthorizations() {
		this.AUTHORIZE_CREATION = true;
		this.AUTHORIZE_DELETE_ALL = true;
		this.AUTHORIZE_DELETE_ONE = true;
		this.AUTHORIZE_GET_ALL = true;
		this.AUTHORIZE_GET_ONE = true;
		this.AUTHORIZE_UPDATE = true;
		this.AUTHORIZE_COUNT = true;
	}

	@Override
	protected List<ISpringCrudifyAuthorization> createCustomAuthorizations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Class<ISpringCrudifyEntity> getEntityClazz() {
		return (Class<ISpringCrudifyEntity>) this.entityClass;
	}
	
	public ResponseEntity<?> createEntity(@RequestBody String entity, @RequestHeader String tenantId) {
		
		ObjectMapper mapper = new ObjectMapper();
		Object entityObj = null;
		try {
			entityObj = mapper.readValue(entity, this.entityClass);
		} catch (JsonProcessingException e) {
			return new ResponseEntity<>(new ISpringCrudifyErrorObject(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
		
		return this.createEntity((ISpringCrudifyEntity) entityObj, tenantId);
	}
	
	public ResponseEntity<?> updateEntity(@PathVariable String uuid, @RequestBody String entity, @RequestHeader String tenantId) {
		
		ObjectMapper mapper = new ObjectMapper();
		Object entityObj = null;
		try {
			entityObj = mapper.readValue(entity, this.entityClass);
		} catch (JsonProcessingException e) {
			return new ResponseEntity<>(new ISpringCrudifyErrorObject(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
		
		return this.updateEntity(uuid, (ISpringCrudifyEntity) entityObj, tenantId);
	}

}
