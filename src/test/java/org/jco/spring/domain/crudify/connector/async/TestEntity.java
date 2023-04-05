package org.jco.spring.domain.crudify.connector.async;

import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestEntity implements ISpringCrudifyEntity {
	
	@JsonProperty
	private String uuid;
	
	@JsonProperty
	private String id;

}
