package org.jco.spring.domain.crudify.security.keys;

import java.security.Key;

import io.jsonwebtoken.SignatureAlgorithm;

public class SpringCrudifyInMemoryKeyManager implements ISpringCrudifyKeyManager {

	@Override
	public Key getKeyForCiphering(String realm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Key getKeyForUnciphering(String realm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void renew(String realm) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createRealm(String realm, SignatureAlgorithm algo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createRealm(String realm, SignatureAlgorithm algo, SpringCrudifyKeyExpiration expiration) {
		// TODO Auto-generated method stub
		
	}

}
