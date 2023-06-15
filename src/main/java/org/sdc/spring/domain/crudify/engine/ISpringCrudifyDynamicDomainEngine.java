package org.sdc.spring.domain.crudify.engine;

import org.sdc.spring.domain.crudify.controller.ISpringCrudifyController;
import org.sdc.spring.domain.crudify.repository.ISpringCrudifyRepository;
import org.sdc.spring.domain.crudify.repository.dao.ISpringCrudifyDAORepository;
import org.sdc.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.sdc.spring.domain.crudify.repository.dto.ISpringCrudifyDTOObject;
import org.sdc.spring.domain.crudify.ws.ISpringCrudifyRestService;

public interface ISpringCrudifyDynamicDomainEngine {

	ISpringCrudifyDAORepository<? extends ISpringCrudifyEntity, ? extends ISpringCrudifyDTOObject<? extends ISpringCrudifyEntity>> getDao(String name);

	ISpringCrudifyRepository<? extends ISpringCrudifyEntity, ? extends ISpringCrudifyDTOObject<? extends ISpringCrudifyEntity>> getRepository(String name);

	ISpringCrudifyController<? extends ISpringCrudifyEntity, ? extends ISpringCrudifyDTOObject<? extends ISpringCrudifyEntity>> getController(String name);

	ISpringCrudifyRestService<? extends ISpringCrudifyEntity, ? extends ISpringCrudifyDTOObject<? extends ISpringCrudifyEntity>> getService(String name);

}
