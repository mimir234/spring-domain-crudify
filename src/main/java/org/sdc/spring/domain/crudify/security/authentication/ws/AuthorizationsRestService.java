package org.sdc.spring.domain.crudify.security.authentication.ws;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.sdc.spring.domain.crudify.security.authorization.BasicSpringCrudifyAuthorization;
import org.sdc.spring.domain.crudify.security.authorization.ISpringCrudifyAuthorization;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@ComponentScan("com.citech.iot")
@Tag(name = "Authorizations", description = "The Spring Domain Crudify built-in authorizations API")
@RestController
@ConditionalOnProperty(name = "spring.domain.crudify.security.exposeAuthorizations", havingValue = "enabled", matchIfMissing = true)
public class AuthorizationsRestService {
	
	private List<ISpringCrudifyAuthorization> authorizations = new ArrayList<ISpringCrudifyAuthorization>();

	@GetMapping("/authorizations")
	public ResponseEntity<?> getRoles() {
		List<String> auths = new ArrayList<String>();
		
		this.authorizations.forEach(r -> {
			auths.add(r.getAuthorization());
		});
		
		List<String> listWithoutDuplicates = new ArrayList<String>(new HashSet<>(auths));
		
        return new ResponseEntity<>(listWithoutDuplicates, HttpStatus.ACCEPTED);
    }

	public List<ISpringCrudifyAuthorization> getCustomAuthorizations() {
		List<ISpringCrudifyAuthorization> auths = new ArrayList<ISpringCrudifyAuthorization>();
		auths.add(new BasicSpringCrudifyAuthorization("/authorizations", "authorizations-read", HttpMethod.GET));

		return auths;
		
	}

	public void setAuthorizations(List<ISpringCrudifyAuthorization> authorizations) {
		this.authorizations.addAll(authorizations);
	}

}
