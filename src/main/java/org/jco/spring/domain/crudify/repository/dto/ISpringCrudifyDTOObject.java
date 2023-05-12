/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.jco.spring.domain.crudify.repository.dto;

import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntity;

public interface ISpringCrudifyDTOObject<T extends ISpringCrudifyEntity> {
	
	//-----------------------------------------------------------//
	// Abstract method below to be implemented by sub classes    //
	//-----------------------------------------------------------//	
	
	public void create(T entity);
	
	public T convert();

	/**
	 * Update the object fields with the objects fields given in argument.
	 * @param object
	 */
	public void update(ISpringCrudifyDTOObject<T> object);
	
	public ISpringCrudifyDTOFactory<T, ? extends ISpringCrudifyDTOObject<?>> getFactory();

	public String getUuid();

	public String getTenantId();

	public String getId();

}
