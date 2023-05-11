package org.jco.spring.domain.crudify.security.authorization;

import org.jco.spring.domain.crudify.security.keys.SpringCrudifyKeyExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public interface ISpringCrudifyAuthorizationProvider {

	String getAuthorization(Authentication authentication) throws SpringCrudifyKeyExpiredException;

	String getUserNameFromAuthorization(String token) throws SpringCrudifyKeyExpiredException;

	boolean validateAuthorization(String token, UserDetails userDetails) throws SpringCrudifyKeyExpiredException;

}
