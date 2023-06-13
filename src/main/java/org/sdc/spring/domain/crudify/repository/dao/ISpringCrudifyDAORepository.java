/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.sdc.spring.domain.crudify.repository.dao;

import java.util.List;

import org.sdc.spring.domain.crudify.repository.dto.ISpringCrudifyDTOObject;
import org.sdc.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.sdc.spring.domain.crudify.spec.filter.SpringCrudifyLiteral;
import org.sdc.spring.domain.crudify.spec.sort.SpringCrudifySort;
import org.springframework.data.domain.Pageable;

public interface ISpringCrudifyDAORepository<DTO extends ISpringCrudifyDTOObject<ISpringCrudifyEntity>> {

	List<DTO> findByTenantId(String tenantId, Pageable pageable, SpringCrudifyLiteral filter, SpringCrudifySort sort);

	DTO findOneByUuidAndTenantId(String uuid, String tenantId);

	DTO findOneByIdAndTenantId(String id, String tenantId);

	DTO save(DTO object);

	void delete(DTO object);
	
	long countByTenantId(String tenantId, SpringCrudifyLiteral filter);

	void setMagicTenantId(String magicTenantId);

	void setDtoClass(Class<?> dtoClass);
	
}
