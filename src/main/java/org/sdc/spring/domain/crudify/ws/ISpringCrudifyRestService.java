package org.sdc.spring.domain.crudify.ws;

import java.util.List;

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

}
