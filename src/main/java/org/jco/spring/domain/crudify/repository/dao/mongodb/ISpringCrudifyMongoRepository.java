/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.jco.spring.domain.crudify.repository.dao.mongodb;

import org.jco.spring.domain.crudify.repository.dao.ISpringCrudifyDAORepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 
 * @author J.Colombet
 *
 * @param <T>
 */
@NoRepositoryBean
public interface ISpringCrudifyMongoRepository<T> extends MongoRepository <T, String>, ISpringCrudifyDAORepository<T> {

	@Query( count = true )
	Integer countByTenantId(String tenantId);

}
