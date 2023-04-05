package org.jco.spring.domain.crudify.connector.async;

import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SpringCrudifyAsyncConnectorPair<T extends ISpringCrudifyEntity> {
	
	private Object locker;

	private SpringCrudifyAsyncConnectorEnvelop<T> entity;

}
