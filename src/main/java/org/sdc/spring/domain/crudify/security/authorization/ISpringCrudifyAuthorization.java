package org.sdc.spring.domain.crudify.security.authorization;

import org.springframework.http.HttpMethod;

public interface ISpringCrudifyAuthorization {
	
	String getEndpoint();
	
	String getAuthorization();
	
	HttpMethod getHttpMethod();
	
	String toString();

}
