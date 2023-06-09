package org.sdc.spring.domain.crudify.engine;

import org.sdc.spring.domain.crudify.controller.ISpringCrudifyController;
import org.sdc.spring.domain.crudify.repository.ISpringCrudifyRepository;
import org.sdc.spring.domain.crudify.repository.dao.ISpringCrudifyDAORepository;
import org.sdc.spring.domain.crudify.ws.ISpringCrudifyRestService;

public interface ISpringCrudifyDynamicDomainEngine {

	ISpringCrudifyDAORepository<?> getDao(String name);

	ISpringCrudifyRepository<?> getRepository(String name);

	ISpringCrudifyController<?> getController(String name);

	ISpringCrudifyRestService<?> getService(String name);

}
