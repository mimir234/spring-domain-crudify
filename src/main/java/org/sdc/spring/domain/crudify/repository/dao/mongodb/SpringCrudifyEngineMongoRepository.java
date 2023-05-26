package org.sdc.spring.domain.crudify.repository.dao.mongodb;

import org.sdc.spring.domain.crudify.repository.dto.AbstractSpringCrudifyDTOObject;
import org.sdc.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.springframework.data.mongodb.core.MongoTemplate;

public class SpringCrudifyEngineMongoRepository extends AbstractSpringCrudifyMongoRepository<AbstractSpringCrudifyDTOObject<ISpringCrudifyEntity>>{

	private Class<?> clazz;

	public SpringCrudifyEngineMongoRepository(Class<?> dtoClass, MongoTemplate mongo, String magicTenantId) {
		this.mongo = mongo;
		this.magicTenantId = magicTenantId;
		this.clazz = dtoClass;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Class<AbstractSpringCrudifyDTOObject<ISpringCrudifyEntity>> getDTOClass() {
		return (Class<AbstractSpringCrudifyDTOObject<ISpringCrudifyEntity>>) this.clazz;
	}
	
}