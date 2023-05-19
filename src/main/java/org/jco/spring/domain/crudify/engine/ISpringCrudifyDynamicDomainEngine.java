package org.jco.spring.domain.crudify.engine;

import org.jco.spring.domain.crudify.controller.ISpringCrudifyController;
import org.jco.spring.domain.crudify.repository.ISpringCrudifyRepository;
import org.jco.spring.domain.crudify.repository.dao.ISpringCrudifyDAORepository;
import org.jco.spring.domain.crudify.ws.AbstractSpringCrudifyService;

public interface ISpringCrudifyDynamicDomainEngine {

	ISpringCrudifyDAORepository<?> getDao(String name);

	ISpringCrudifyRepository<?> getRepository(String name);

	ISpringCrudifyController<?> getController(String name);

	AbstractSpringCrudifyService<?> getService(String name);

}
