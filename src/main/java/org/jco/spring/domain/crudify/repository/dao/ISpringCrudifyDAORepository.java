/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.jco.spring.domain.crudify.repository.dao;

import java.util.List;

public interface ISpringCrudifyDAORepository<T> {

	T findOneById(String id);
	
	T findOneByUuid(String uuid);

	List<T> findByTenantId(String tenantId);

	T findOneByUuidAndTenantId(String uuid, String tenantId);

	T findOneByIdAndTenantId(String id, String tenantId);

	<S extends T> S save(S object);

	void delete(T object);
	
}
