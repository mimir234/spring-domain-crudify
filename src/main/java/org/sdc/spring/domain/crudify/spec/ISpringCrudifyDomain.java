package org.sdc.spring.domain.crudify.spec;

import org.sdc.spring.domain.crudify.repository.dto.ISpringCrudifyDTOObject;

public interface ISpringCrudifyDomain<Entity extends ISpringCrudifyEntity, Dto extends ISpringCrudifyDTOObject<Entity>>{
	
	Class<Entity> getEntityClass();
	
	Class<Dto> getDtoClass();	
	
	String getDomain();

}
