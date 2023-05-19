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
	private boolean authorize_creation;
	private boolean authorize_read_all;
	private boolean authorize_read_one;
	private boolean authorize_update_one;
	private boolean authorize_delete_one;
	private boolean authorize_count;
	private boolean authorize_delete_all;

	public SpringCrudifyEngineService(Class<?> entityClass, ISpringCrudifyController<ISpringCrudifyEntity> crudController, boolean authorize_creation, boolean authorize_read_all, boolean authorize_read_one, boolean authorize_update_one, boolean authorize_delete_one, boolean authorize_delete_all, boolean authorize_count) {
		this.entityClass = entityClass;
		this.crudController = crudController;
		this.authorize_creation = authorize_creation;
		this.authorize_read_all = authorize_read_all;
		this.authorize_read_one = authorize_read_one;
		this.authorize_update_one = authorize_update_one;
		this.authorize_delete_one = authorize_delete_one;
		this.authorize_delete_all = authorize_delete_all;
		this.authorize_count = authorize_count;
		
		this.getDomain();
		this.init();
	}
	
	@Override
	protected void defineAuthorizations() {
		this.AUTHORIZE_CREATION = this.authorize_creation;
		this.AUTHORIZE_DELETE_ALL = this.authorize_delete_all;
		this.AUTHORIZE_DELETE_ONE = this.authorize_delete_one;
		this.AUTHORIZE_GET_ALL = this.authorize_read_all;
		this.AUTHORIZE_GET_ONE = this.authorize_read_one;
		this.AUTHORIZE_UPDATE = this.authorize_update_one;
		this.AUTHORIZE_COUNT = this.authorize_count;
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
