/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.sdc.spring.domain.crudify.repository.dao;

import java.util.List;

import org.sdc.spring.domain.crudify.repository.dto.ISpringCrudifyDTOObject;
import org.sdc.spring.domain.crudify.spec.ISpringCrudifyDomainable;
import org.sdc.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.sdc.spring.domain.crudify.spec.filter.SpringCrudifyLiteral;
import org.sdc.spring.domain.crudify.spec.sort.SpringCrudifySort;
import org.springframework.data.domain.Pageable;

public interface ISpringCrudifyDAORepository<Entity extends ISpringCrudifyEntity, Dto extends ISpringCrudifyDTOObject<Entity>> extends ISpringCrudifyDomainable<Entity, Dto>{

	List<Dto> findByTenantId(String tenantId, Pageable pageable, SpringCrudifyLiteral filter, SpringCrudifySort sort);

	Dto findOneByUuidAndTenantId(String uuid, String tenantId);

	Dto findOneByIdAndTenantId(String id, String tenantId);

	Dto save(Dto object);

	void delete(Dto object);
	
	long countByTenantId(String tenantId, SpringCrudifyLiteral filter);

	void setMagicTenantId(String magicTenantId);
}
