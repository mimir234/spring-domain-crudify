/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.jco.spring.domain.crudify.repository;

import java.util.List;

import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.jco.spring.domain.crudify.spec.filter.SpringCrudifyLiteral;

/**
 * 
 * @author JérémyCOLOMBET
 *
 * @param <T>
 */
public interface ISpringCrudifyRepository<T extends ISpringCrudifyEntity> {

	boolean doesExists(String tenantId, T entity);

	List<T> getEntities(String tenantId, int pageSize, int pageIndex, SpringCrudifyLiteral filter);

	void save(String tenantId, T entity);

	T update(String tenantId, T entity);

	T getOneById(String tenantId, String id);

	void delete(String tenantId, T entity);
	
	boolean doesExists(String tenantId, String id);

	T getOneByUuid(String tenantId, String uuid);

	long getTotalCount(String tenantId);
	
	void setEntityClazz();

}
