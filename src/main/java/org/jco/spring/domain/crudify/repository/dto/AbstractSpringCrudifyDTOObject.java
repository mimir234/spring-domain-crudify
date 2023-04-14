/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.jco.spring.domain.crudify.repository.dto;

import org.bson.BsonType;
import org.bson.codecs.pojo.annotations.BsonRepresentation;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import lombok.Getter;

/**
 * 
 * @author J.Colombet
 *
 * @param <T>
 */
@Data
@Getter
public abstract class AbstractSpringCrudifyDTOObject<T> implements ISpringCrudifyDTOObject<T> {
	
	@Id
	@Indexed(unique=true)
	@Field
	@BsonRepresentation(value = BsonType.STRING)
	protected String techUuid;
	
	@Field
	protected String id;
	
	@Field
	protected String tenantId;

	public AbstractSpringCrudifyDTOObject() {
		
	}

	public AbstractSpringCrudifyDTOObject(String tenantId, T entity){
		this.tenantId = tenantId;
		this.create(entity);
	}

}
