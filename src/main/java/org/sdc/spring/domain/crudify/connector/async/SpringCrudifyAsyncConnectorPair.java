package org.sdc.spring.domain.crudify.connector.async;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SpringCrudifyAsyncConnectorPair {
	
	private Object locker;

	private SpringCrudifyAsyncConnectorEnvelop<?> entity;

}
