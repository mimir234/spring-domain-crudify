package org.jco.spring.domain.crudify.security.authorization;

import javax.inject.Inject;

import org.jco.spring.domain.crudify.security.authentication.ISpringCrudifySecurityException;
import org.jco.spring.domain.crudify.security.authentication.dao.ISpringCrudifyAuthenticationUserMapper;
import org.jco.spring.domain.crudify.security.authorization.bearer.SpringCrudifyBearerAuthorizationExtractor;
import org.jco.spring.domain.crudify.security.authorization.token.jwt.SpringCrudifyJwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
	
	@Inject
	private ISpringCrudifyAuthenticationUserMapper userMapper;

	private SpringCrudifyJwtTokenProvider authorizationProvider = null;
	
	@Bean 
	private ISpringCrudifyAuthorizationProvider getAuthorizationProvider() {
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
					this.authorizationProvider  = new SpringCrudifyJwtTokenProvider();
					break;
				}
				break;
			}
			break;		
		}	
		return this.authorizationProvider;
	}
	
	@Override
	public HttpSecurity configureFilterChain(HttpSecurity http) throws ISpringCrudifySecurityException {
		
		try {
			http.authorizeHttpRequests().and().addFilterBefore(new SpringCrudifyBearerAuthorizationExtractor(this.authorizationProvider, this.userMapper), UsernamePasswordAuthenticationFilter.class);
		} catch (Exception e) {
			throw new ISpringCrudifySecurityException(e);
		}
		
		return http;

	}

}
