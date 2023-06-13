package org.sdc.spring.domain.crudify.repository.dao.mongodb;

import org.sdc.spring.domain.crudify.repository.dto.ISpringCrudifyDTOObject;
import org.sdc.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.springframework.data.mongodb.core.MongoTemplate;

public class SpringCrudifyEngineMongoRepository extends AbstractSpringCrudifyMongoRepository<ISpringCrudifyDTOObject<ISpringCrudifyEntity>>{

	private Class<?> clazz;

	public SpringCrudifyEngineMongoRepository(Class<?> dtoClass, MongoTemplate mongo, String magicTenantId) {
		this.mongo = mongo;
		this.magicTenantId = magicTenantId;
		this.clazz = dtoClass;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Class<ISpringCrudifyDTOObject<ISpringCrudifyEntity>> getDTOClass() {
		return (Class<ISpringCrudifyDTOObject<ISpringCrudifyEntity>>) this.clazz;
	}

	@Override
	public void setMagicTenantId(String magicTenantId) {
		this.magicTenantId = magicTenantId;
	}

	@Override
	public void setDtoClass(Class<?> dtoClass) {
		this.clazz = dtoClass;
	}
	
}