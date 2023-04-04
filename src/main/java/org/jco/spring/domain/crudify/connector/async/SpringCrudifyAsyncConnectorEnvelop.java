package org.jco.spring.domain.crudify.connector.async;

import org.jco.spring.domain.crudify.connector.ISpringCrudifyConnector.SpringCrudifyConnectorOperation;
import org.jco.spring.domain.crudify.spec.ISpringCrudifyEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SpringCrudifyAsyncConnectorEnvelop <T extends ISpringCrudifyEntity> {
	
	private SpringCrudifyAsyncMessageType type;
	
	private String messageUuid; 
	
	private String transactionUuid;

	private String tenantId;

	private String domain;
	
	private SpringCrudifyAsyncResponseStatus status;

	private SpringCrudifyConnectorOperation operation;
	
	private T entity;
	
	private String responseDirective;
	
	private String responseMessage;
	
}
