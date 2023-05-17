/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.jco.spring.domain.crudify.repository.dto;

import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import lombok.Getter;

/**
 * 
 * @author J.Colombet
 *
 * @param <Entity>
 */
@Data
@Getter
public abstract class AbstractSpringCrudifyDTOObject<Entity extends ISpringCrudifyEntity> implements ISpringCrudifyDTOObject<Entity> {
	
	@Id
	@Indexed(unique=true)
	protected String uuid;
	
	@Field
	protected String id;
	
	@Field
	protected String tenantId;

	protected AbstractSpringCrudifyDTOObject() {
	}

	protected AbstractSpringCrudifyDTOObject(String tenantId, Entity entity){
		this.tenantId = tenantId;
		this.id = entity.getId();
		this.uuid = entity.getUuid();
		this.create(entity);
	}

}
