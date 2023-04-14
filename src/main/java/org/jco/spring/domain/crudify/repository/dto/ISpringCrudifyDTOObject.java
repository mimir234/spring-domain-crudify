/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.jco.spring.domain.crudify.repository.dto;

public interface ISpringCrudifyDTOObject<T> {
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


}
