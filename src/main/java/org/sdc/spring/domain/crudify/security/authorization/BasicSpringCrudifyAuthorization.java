package org.sdc.spring.domain.crudify.security.authorization;

import org.springframework.http.HttpMethod;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BasicSpringCrudifyAuthorization implements ISpringCrudifyAuthorization {

	private String endpoint;
	private String authorization; 
	private HttpMethod httpMethod;
	
	@Override
	public String toString() {
		
		return "[endpoint ["+this.endpoint+"] authorization ["+this.authorization+"] httpMethod ["+this.httpMethod+"]]";
	}


}
