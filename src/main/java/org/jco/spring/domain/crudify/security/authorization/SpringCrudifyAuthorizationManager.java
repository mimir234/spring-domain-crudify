package org.jco.spring.domain.crudify.security.authorization;

import org.jco.spring.domain.crudify.security.authentication.ISpringCrudifySecurityException;
import org.jco.spring.domain.crudify.security.authorization.token.jwt.SpringCrudifyJwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "spring.domain.crudify.security.authentication", havingValue = "enabled", matchIfMissing = true)
public class SpringCrudifyAuthorizationManager implements ISpringCrudifyAuthorizationManager {

	
	@Value("${spring.domain.crudify.security.authorization}")
	private SpringCrudifyAuthorizationType authorizationType;
	
	@Value("${spring.domain.crudify.security.authorization.token.type}")
	private SpringCrudifyTokenAuthorizationType tokenAuthorizationType;
	
	@Value("${spring.domain.crudify.security.authorization.token.provider}")
	private SpringCrudifyTokenProviderType tokenProviderType;
	
	@Bean 
	private ISpringCrudifyAuthorizationProvider getAuthorizationProvider() {
		ISpringCrudifyAuthorizationProvider authorizationProvider = null;
		switch(this.authorizationType) {
		default:
		case token:
			switch (this.tokenAuthorizationType) {
			default:
			case jwt:
				switch (this.tokenProviderType) {
				case db:
					break;
				case inmemory: 
					break;
				default: 
				case none:
					authorizationProvider = new SpringCrudifyJwtTokenProvider();
					break;
				}
				break;
			}
			break;		
		}
		
		return authorizationProvider;
	}
	
	@Override
	public HttpSecurity configureFilterChain(HttpSecurity http) throws ISpringCrudifySecurityException {
		
		
		
		return http;

	}

}
