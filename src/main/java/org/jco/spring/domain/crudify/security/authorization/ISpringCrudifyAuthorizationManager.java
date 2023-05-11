package org.jco.spring.domain.crudify.security.authorization;

import org.jco.spring.domain.crudify.security.authentication.ISpringCrudifySecurityException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface ISpringCrudifyAuthorizationManager {

	HttpSecurity configureFilterChain(HttpSecurity http) throws ISpringCrudifySecurityException;

}
