package org.jco.spring.domain.crudify.security.keys;

import java.security.Key;

import io.jsonwebtoken.SignatureAlgorithm;

public interface ISpringCrudifyKeyManager {
	
	Key getKeyForCiphering(String realm);
	
	Key getKeyForUnciphering(String realm);
	
	void renew(String realm);
	
	void createRealm(String realm, SignatureAlgorithm algo);
	
	void createRealm(String realm, SignatureAlgorithm algo, SpringCrudifyKeyExpiration expiration);

}
