package org.sdc.spring.domain.crudify.repository.dto;

public interface ISpringCrudifyDTOFactory<Entity, Dto extends ISpringCrudifyDTOObject<?>> {

	Dto newInstance(String tenantId, Entity entity);
	
}
