/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.sdc.spring.domain.crudify.spec;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author Jérémy COLOMBET
 * 
 * This interface describes the methods that an entity musts have in order to be used by the library and processed. 
 * 
 * An entity should provide methods to set/get 
 *    - Uuid : unique Id used by the library
 *    - Id : an Id that is not unique 
 *    
 * An entity must provide its domain
 *
 */
public interface ISpringCrudifyEntity {

	@JsonIgnore
	String getId(); 
	
	void setId(String id); 
	
	@JsonIgnore
	String getUuid(); 
	
	void setUuid(String uuid); 
	
	@JsonIgnore
	String getDomain();
	
	@JsonIgnore
	ISpringCrudifyEntityFactory<? extends ISpringCrudifyEntity> getFactory();	
}
