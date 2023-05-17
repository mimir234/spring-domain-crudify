package org.jco.spring.domain.crudify.repository.dto;

import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntity;

public interface ISpringCrudifyDTOFactory<Entity, Dto extends ISpringCrudifyDTOObject<?>> {

	Dto newInstance(String tenantId, Entity entity);
	
}
