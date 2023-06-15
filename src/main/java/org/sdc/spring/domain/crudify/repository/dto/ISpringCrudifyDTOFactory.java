package org.sdc.spring.domain.crudify.repository.dto;

import org.sdc.spring.domain.crudify.spec.ISpringCrudifyEntity;

public interface ISpringCrudifyDTOFactory<Entity extends ISpringCrudifyEntity, Dto extends ISpringCrudifyDTOObject<Entity>> {

	Dto newInstance(String tenantId, Entity entity);
	
}
