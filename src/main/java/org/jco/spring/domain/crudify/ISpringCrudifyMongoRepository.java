/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.jco.spring.domain.crudify;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 
 * @author J.Colombet
 *
 * @param <T>
 */
@NoRepositoryBean
public interface ISpringCrudifyMongoRepository<T> extends MongoRepository <T, String>{

	T findOneById(String id);
	
	T findOneByUuid(String uuid);

	List<T> findByTenantId(String tenantId);

	T findOneByUuidAndTenantId(String uuid, String tenantId);

	T findOneByIdAndTenantId(String id, String tenantId);
	
}
