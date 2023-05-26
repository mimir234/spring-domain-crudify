package org.sdc.spring.domain.crudify.security.authentication.modes.loginpassword;

import org.sdc.spring.domain.crudify.security.authentication.ISpringAuthenticationRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SpringCrudifyLoginPasswordAuthenticationRequest implements ISpringAuthenticationRequest {
	
	private String login;
	
	private String password;

}
