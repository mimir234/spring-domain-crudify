package org.sdc.spring.domain.crudify.spec;

import org.sdc.spring.domain.crudify.repository.dto.ISpringCrudifyDTOFactory;
import org.sdc.spring.domain.crudify.repository.dto.ISpringCrudifyDTOObject;

public interface ISpringCrudifyDomainable<Entity extends ISpringCrudifyEntity, Dto extends ISpringCrudifyDTOObject<Entity>> {

	void setDomain(ISpringCrudifyDomain<Entity, Dto> domain);
	
	String getDomain();

	ISpringCrudifyEntityFactory<Entity> getEntityFactory();
	
	ISpringCrudifyDTOFactory<Entity, Dto> getDtoFactory();

	Class<Entity> getEntityClass();

	Class<Dto> getDtoClass();
	
}
