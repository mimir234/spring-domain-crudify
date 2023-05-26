package org.sdc.spring.domain.crudify.security.authentication.dao;

import org.sdc.spring.domain.crudify.security.authentication.ISpringCrudifySecurityException;
import org.sdc.spring.domain.crudify.spec.ISpringCrudifyEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public abstract class AbstractSpringCrudifyAuthenticationUserMapper<UserEntity extends ISpringCrudifyEntity> implements ISpringCrudifyAuthenticationUserMapper {

	@Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
    	
		UserEntity userEntity = null;
		try {
			userEntity = this.getEntity(login);
		} catch (ISpringCrudifySecurityException e) {
			new UsernameNotFoundException(e.getMessage());
		}
	
		return this.mapUser(userEntity);
    }

	protected abstract UserEntity getEntity(String login) throws ISpringCrudifySecurityException;
	protected abstract UserDetails mapUser(UserEntity entity);
	
}
