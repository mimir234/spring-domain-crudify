package org.jco.spring.domain.crudify.security.authorization;

import org.springframework.security.core.Authentication;

public interface ISpringCrudifyAuthorizationProvider {

	String getAuthorization(Authentication authentication);

}
