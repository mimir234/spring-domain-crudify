package org.jco.spring.domain.crudify.engine;

import java.util.Date;

import org.jco.spring.domain.crudify.repository.dto.AbstractSpringCrudifyDTOObject;
import org.jco.spring.domain.crudify.repository.dto.ISpringCrudifyDTOFactory;
import org.jco.spring.domain.crudify.repository.dto.ISpringCrudifyDTOObject;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "test")
public class TestDTO extends AbstractSpringCrudifyDTOObject<TestEntity> {

	public TestDTO(String tenantId, TestEntity entity) {
		super(tenantId, entity);
	}
	
	@Field
	private String field;

	@Override
	public void create(TestEntity entity) {
		this.field = entity.getField();
	}

	@Override
	public TestEntity convert() {
		return new TestEntity(this.uuid, this.id, this.field);
	}

	@Override
	public void update(ISpringCrudifyDTOObject<TestEntity> object) {
	
	}

	@Override
	public ISpringCrudifyDTOFactory<TestEntity, TestDTO> getFactory() {
		return new ISpringCrudifyDTOFactory<TestEntity, TestDTO>() {
			
			@Override
			public TestDTO newInstance(String tenantId, TestEntity entity) {
				return new TestDTO(tenantId, entity);
			}
		};
	}

}
