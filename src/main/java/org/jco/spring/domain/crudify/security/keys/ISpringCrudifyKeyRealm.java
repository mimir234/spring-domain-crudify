package org.jco.spring.domain.crudify.security.keys;

import java.security.Key;

public interface ISpringCrudifyKeyRealm {

	Key getCipheringKey() throws SpringCrudifyKeyExpiredException;

	Key getUncipheringKey() throws SpringCrudifyKeyExpiredException;

}
