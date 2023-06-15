package org.sdc.spring.domain.crudify.controller;

import java.util.List;
import java.util.Optional;

import org.sdc.spring.domain.crudify.business.ISpringCrudifyBusiness;
import org.sdc.spring.domain.crudify.connector.ISpringCrudifyConnector;
import org.sdc.spring.domain.crudify.events.ISpringCrudifyEventPublisher;
import org.sdc.spring.domain.crudify.repository.ISpringCrudifyRepository;
import org.sdc.spring.domain.crudify.repository.dto.ISpringCrudifyDTOObject;
import org.sdc.spring.domain.crudify.spec.ISpringCrudifyDomain;
import org.sdc.spring.domain.crudify.spec.ISpringCrudifyEntity;

public class SpringCrudifyEngineController extends SpringCrudifyController<ISpringCrudifyEntity, ISpringCrudifyDTOObject<ISpringCrudifyEntity>> {

	public SpringCrudifyEngineController(
			ISpringCrudifyDomain<ISpringCrudifyEntity, ISpringCrudifyDTOObject<ISpringCrudifyEntity>> domain,
			Optional<ISpringCrudifyRepository<ISpringCrudifyEntity, ISpringCrudifyDTOObject<ISpringCrudifyEntity>>> repository,
			Optional<ISpringCrudifyConnector<ISpringCrudifyEntity, List<ISpringCrudifyEntity>, ISpringCrudifyDTOObject<ISpringCrudifyEntity>>> connector,
			Optional<ISpringCrudifyBusiness<ISpringCrudifyEntity>> business,
			Optional<ISpringCrudifyEventPublisher> event ) {
		super(domain);
		this.repository = repository;
		this.connector = connector;
		this.business = business;
		this.eventPublisher = event;
	}

}
