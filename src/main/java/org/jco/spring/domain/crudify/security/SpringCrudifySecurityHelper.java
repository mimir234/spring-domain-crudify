package org.jco.spring.domain.crudify.security;

import java.util.Optional;

import javax.inject.Inject;

import org.jco.spring.domain.crudify.security.authentication.ISpringCrudifyAuthenticationManager;
import org.jco.spring.domain.crudify.security.authentication.ISpringCrudifySecurityException;
import org.jco.spring.domain.crudify.security.authorization.ISpringCrudifyAuthorizationManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Service;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Service
@ConditionalOnProperty(name = "spring.domain.crudify.security", havingValue = "enabled", matchIfMissing = true)
@SecurityScheme(name = "Bearer Authentication", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
public class SpringCrudifySecurityHelper implements ISpringCrudifySecurityHelper {

	@Inject
	private Optional<ISpringCrudifyAuthenticationManager> authenticationManager;

	@Inject
	private Optional<ISpringCrudifyAuthorizationManager> authorizationManager;
	
	@Override
	public HttpSecurity configureFilterChain(HttpSecurity http) throws ISpringCrudifySecurityException {

		this.authenticationManager.ifPresent(a -> {
			try {
				a.configureFilterChain(http);
			} catch (ISpringCrudifySecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		this.authorizationManager.ifPresent(a -> {
			try {
				a.configureFilterChain(http);
			} catch (ISpringCrudifySecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		return http;

	}

}
