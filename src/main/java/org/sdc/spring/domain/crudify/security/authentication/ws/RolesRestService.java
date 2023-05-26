package org.sdc.spring.domain.crudify.security.authentication.ws;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.sdc.spring.domain.crudify.security.authentication.modes.loginpassword.SpringCrudifyLoginPasswordAuthenticationRequest;
import org.sdc.spring.domain.crudify.security.authorization.BasicSpringCrudifyAuthorization;
import org.sdc.spring.domain.crudify.security.authorization.ISpringCrudifyAuthorization;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@ComponentScan("com.citech.iot")
@Tag(name = "Roles", description = "The Spring Domain Crudify built-in roles API")
@RestController
@ConditionalOnProperty(name = "spring.domain.crudify.security.exposeRoles", havingValue = "enabled", matchIfMissing = true)
public class RolesRestService {
	
	private ArrayList<ISpringCrudifyAuthorization> roles;

	@GetMapping("/roles")
	public ResponseEntity<?> getRoles() {
		List<String> auths = new ArrayList<String>();
		
		this.roles.forEach(r -> {
			auths.add(r.getRole());
		});
		
		List<String> listWithoutDuplicates = new ArrayList<String>(new HashSet<>(auths));
		
        return new ResponseEntity<>(listWithoutDuplicates, HttpStatus.ACCEPTED);
    }

	public List<ISpringCrudifyAuthorization> getCustomAuthorizations() {
		List<ISpringCrudifyAuthorization> auths = new ArrayList<ISpringCrudifyAuthorization>();
		auths.add(new BasicSpringCrudifyAuthorization("/roles", "roles-read", HttpMethod.GET));

		return auths;
		
	}

	public void setRoles(ArrayList<ISpringCrudifyAuthorization> authorizations) {
		this.roles = authorizations;
	}

}
