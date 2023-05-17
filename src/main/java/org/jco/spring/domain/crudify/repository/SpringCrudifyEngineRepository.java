package org.jco.spring.domain.crudify.repository;

import org.jco.spring.domain.crudify.repository.dao.ISpringCrudifyDAORepository;
import org.jco.spring.domain.crudify.repository.dto.ISpringCrudifyDTOObject;
import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntity;

@SuppressWarnings("unchecked")
public class SpringCrudifyEngineRepository extends AbstractSpringCrudifyRepository<ISpringCrudifyEntity, ISpringCrudifyDTOObject<ISpringCrudifyEntity>> {

	private Class<?> dtoClass;
	
	private Class<?> entityClass;

	public SpringCrudifyEngineRepository(Class<?> entityClass, Class<?> dtoClass, ISpringCrudifyDAORepository<?> daoRepository) {
		this.entityClass = entityClass;
		this.dtoClass = dtoClass;
		this.daoRepository = (ISpringCrudifyDAORepository<ISpringCrudifyDTOObject<ISpringCrudifyEntity>>) daoRepository;
		
		this.getDomain();
	}

	@Override
	public Class<ISpringCrudifyEntity> getEntityClass() {
		return (Class<ISpringCrudifyEntity>) this.entityClass;
	}

	@Override
	protected Class<ISpringCrudifyDTOObject<ISpringCrudifyEntity>> getDTOClass() {
		return (Class<ISpringCrudifyDTOObject<ISpringCrudifyEntity>>) this.dtoClass;
	}

}
