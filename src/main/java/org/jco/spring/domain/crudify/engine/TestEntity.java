package org.jco.spring.domain.crudify.engine;

import java.util.UUID;

import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntityFactory;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestEntity implements ISpringCrudifyEntity {

	@JsonProperty
	private String uuid;
	
	@JsonProperty
	private String id;
	
	@JsonProperty
	private String field;

	@Override
	public String getDomain() {
		return "Tests";
	}

	@Override
	public ISpringCrudifyEntityFactory<TestEntity> getFactory() {
		return new ISpringCrudifyEntityFactory<TestEntity>() {

			@Override
			public TestEntity newInstance() {
				TestEntity entity = new TestEntity();
				entity.setUuid(UUID.randomUUID().toString());
				return entity;
			}

			@Override
			public TestEntity newInstance(String uuid) {
				TestEntity entity = new TestEntity();
				entity.setUuid(uuid);
				return entity;
			}
		};
	}
	
	
	
}
