package org.jco.spring.domain.crudify.security;

import org.jco.spring.domain.crudify.security.authentication.ISpringCrudifySecurityException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface ISpringCrudifySecurityHelper {

	HttpSecurity configureFilterChain(HttpSecurity http) throws ISpringCrudifySecurityException;

}
