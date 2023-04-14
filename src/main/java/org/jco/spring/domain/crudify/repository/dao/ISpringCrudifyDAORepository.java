/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.jco.spring.domain.crudify.repository.dao;

import java.util.List;

import org.jco.spring.domain.crudify.repository.dto.ISpringCrudifyDTOObject;
import org.jco.spring.domain.crudify.spec.filter.SpringCrudifyLiteral;
import org.springframework.data.domain.Pageable;

public interface ISpringCrudifyDAORepository<T extends ISpringCrudifyDTOObject<?>> {

	List<T> findByTenantId(String tenantId, Pageable pageable, SpringCrudifyLiteral filter);

	T findOneByUuidAndTenantId(String uuid, String tenantId);

	T findOneByIdAndTenantId(String id, String tenantId);

	T save(T object);

	void delete(T object);
	
	long countByTenantId(String tenantId);
	
}
