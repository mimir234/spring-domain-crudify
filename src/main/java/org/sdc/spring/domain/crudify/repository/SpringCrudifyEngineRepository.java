package org.sdc.spring.domain.crudify.repository;

import org.sdc.spring.domain.crudify.repository.dao.ISpringCrudifyDAORepository;
import org.sdc.spring.domain.crudify.repository.dto.ISpringCrudifyDTOObject;
import org.sdc.spring.domain.crudify.spec.ISpringCrudifyDomain;
import org.sdc.spring.domain.crudify.spec.ISpringCrudifyEntity;

public class SpringCrudifyEngineRepository extends SpringCrudifyRepository<ISpringCrudifyEntity, ISpringCrudifyDTOObject<ISpringCrudifyEntity>> {

	public SpringCrudifyEngineRepository(ISpringCrudifyDomain<ISpringCrudifyEntity, ISpringCrudifyDTOObject<ISpringCrudifyEntity>> domain, ISpringCrudifyDAORepository<ISpringCrudifyEntity, ISpringCrudifyDTOObject<ISpringCrudifyEntity>> daoRepository) {
		super(domain);
		this.daoRepository = (ISpringCrudifyDAORepository<ISpringCrudifyEntity, ISpringCrudifyDTOObject<ISpringCrudifyEntity>>) daoRepository;
	}

}
