/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.sdc.spring.domain.crudify.repository.dto;

import org.sdc.spring.domain.crudify.spec.ISpringCrudifyEntity;

public interface ISpringCrudifyDTOObject<Entity extends ISpringCrudifyEntity> {
	
	//-----------------------------------------------------------//
	// Abstract method below to be implemented by sub classes    //
	//-----------------------------------------------------------//	
	
	public void create(Entity entity);
	
	public Entity convert();

	/**
	 * Update the object fields with the objects fields given in argument.
	 * @param object
	 */
	public void update(ISpringCrudifyDTOObject<Entity> object);
	
	public ISpringCrudifyDTOFactory<Entity, ? extends ISpringCrudifyDTOObject<Entity>> getFactory();

	public String getUuid();

	public String getTenantId();

	public String getId();

}
