/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.jco.spring.domain.crudify;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface ISpringCrudifyEntity {

	@JsonIgnore
	String getId(); 
	
	void setId(String id); 
	
	@JsonIgnore
	String getUuid(); 
	
	void setUuid(String uuid); 

}
