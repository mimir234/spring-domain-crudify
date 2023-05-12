package org.jco.spring.domain.crudify.repository.dto;

import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntity;

public interface ISpringCrudifyDTOFactory<Entity extends ISpringCrudifyEntity, Dto extends ISpringCrudifyDTOObject<Entity>> {

	Dto newInstance(String uuid, Entity entity);
	
}
