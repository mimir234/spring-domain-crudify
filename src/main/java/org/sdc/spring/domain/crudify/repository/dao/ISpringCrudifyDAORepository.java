/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.sdc.spring.domain.crudify.repository.dao;

import java.util.List;

import org.sdc.spring.domain.crudify.spec.filter.SpringCrudifyLiteral;
import org.sdc.spring.domain.crudify.spec.sort.SpringCrudifySort;
import org.springframework.data.domain.Pageable;

public interface ISpringCrudifyDAORepository<T> {

	List<T> findByTenantId(String tenantId, Pageable pageable, SpringCrudifyLiteral filter, SpringCrudifySort sort);

	T findOneByUuidAndTenantId(String uuid, String tenantId);

	T findOneByIdAndTenantId(String id, String tenantId);

	T save(T object);

	void delete(T object);
	
	long countByTenantId(String tenantId, SpringCrudifyLiteral filter);
	
}
