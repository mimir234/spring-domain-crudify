package org.sdc.spring.domain.crudify.repository.dao.mongodb;

import org.sdc.spring.domain.crudify.repository.dto.ISpringCrudifyDTOObject;
import org.sdc.spring.domain.crudify.spec.ISpringCrudifyDomain;
import org.sdc.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.springframework.data.mongodb.core.MongoTemplate;

public class SpringCrudifyEngineMongoRepository extends SpringCrudifyMongoRepository<ISpringCrudifyEntity, ISpringCrudifyDTOObject<ISpringCrudifyEntity>>{
	
	public SpringCrudifyEngineMongoRepository(ISpringCrudifyDomain<ISpringCrudifyEntity, ISpringCrudifyDTOObject<ISpringCrudifyEntity>> domain, MongoTemplate mongo, String magicTenantId) {
		super(domain);
		this.mongo = mongo;
		this.magicTenantId = magicTenantId;
	}
	
}