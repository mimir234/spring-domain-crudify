package org.sdc.spring.domain.crudify.ws;

import java.util.List;

import org.sdc.spring.domain.crudify.controller.ISpringCrudifyController;
import org.sdc.spring.domain.crudify.security.authorization.ISpringCrudifyAuthorization;
import org.sdc.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.sdc.spring.domain.crudify.spec.SpringCrudifyReadOutputMode;
import org.springframework.http.ResponseEntity;

public interface ISpringCrudifyRestService<Entity extends ISpringCrudifyEntity> {

	String getDomain();

	List<ISpringCrudifyAuthorization> createAuthorizations();

	ResponseEntity<?> createEntity(Entity entity, String tenantId);

	ResponseEntity<?> getEntities(String tenantId, SpringCrudifyReadOutputMode mode, Integer pageSize, Integer pageIndex, String filterString, String sortString);

	ResponseEntity<?> getEntity(String tenantId, String uuid);

	ResponseEntity<?> updateEntity(String uuid, Entity entity, String tenantId);

	ResponseEntity<?> deleteEntity(String uuid, String tenantId);

	ResponseEntity<?> deleteAll(String tenantId);

	ResponseEntity<?> getCount(String tenantId);

	void authorize(boolean authorize_creation, boolean authorize_read_all, boolean authorize_read_one,
			boolean authorize_update_one, boolean authorize_delete_one, boolean authorize_delete_all,
			boolean authorize_count);

	void setEntityClass(Class<?> entityClass);

	void setController(ISpringCrudifyController<?> controller);

}
