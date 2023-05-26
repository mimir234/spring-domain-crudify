package org.sdc.spring.domain.crudify.security.keys;

import java.util.concurrent.TimeUnit;

public record SpringCrudifyKeyExpiration(long time, TimeUnit unit) {
	
	

}
