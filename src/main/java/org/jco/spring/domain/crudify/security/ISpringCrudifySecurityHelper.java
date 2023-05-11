package org.jco.spring.domain.crudify.security;

import java.util.List;

import org.jco.spring.domain.crudify.security.authentication.ISpringCrudifySecurityException;
import org.jco.spring.domain.crudify.security.authorization.ISpringCrudifyAuthorization;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface ISpringCrudifySecurityHelper {

	HttpSecurity configureFilterChain(HttpSecurity http) throws ISpringCrudifySecurityException;

	List<ISpringCrudifyAuthorization> getAuthorizations();

}
