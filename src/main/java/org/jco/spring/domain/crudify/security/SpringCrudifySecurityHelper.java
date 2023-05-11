package org.jco.spring.domain.crudify.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.jco.spring.domain.crudify.security.authentication.ISpringCrudifyAuthenticationManager;
import org.jco.spring.domain.crudify.security.authentication.ISpringCrudifySecurityException;
import org.jco.spring.domain.crudify.security.authorization.ISpringCrudifyAuthorization;
import org.jco.spring.domain.crudify.security.authorization.ISpringCrudifyAuthorizationManager;
import org.jco.spring.domain.crudify.security.authorization.bearer.SpringCrudifyBearerAuthorizationExtractor;
import org.jco.spring.domain.crudify.security.tenants.SpringCrudifyTenantVerifier;
import org.jco.spring.domain.crudify.ws.AbstractSpringCrudifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
	
	@Autowired
	private List<AbstractSpringCrudifyService<?>> services;
	
	@Autowired
	private Optional<SpringCrudifyTenantVerifier> tenantVerifier;
	
	@Override
	public HttpSecurity configureFilterChain(HttpSecurity http) throws ISpringCrudifySecurityException {
		
		getAuthorizations().forEach(a -> {
			try {
				http.authorizeHttpRequests().requestMatchers(a.getHttpMethod(), a.getEndpoint()).hasAnyAuthority(a.getRole());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

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
		
		this.tenantVerifier.ifPresent(t -> {
			try {
				http.authorizeHttpRequests().and().addFilterAfter(t, UsernamePasswordAuthenticationFilter.class);
			} catch (Exception e) {
				
			}
		});

		return http;

	}

	@Override
	public List<ISpringCrudifyAuthorization> getAuthorizations() {
		List<ISpringCrudifyAuthorization> authorizations = new ArrayList<ISpringCrudifyAuthorization>();
		
		this.services.forEach(service -> {
			List<ISpringCrudifyAuthorization> serviceAuthorizations = service.createAuthorizations();
			authorizations.addAll(serviceAuthorizations);
		});
		
		return authorizations;
	}

}
