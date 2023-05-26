package org.sdc.spring.domain.crudify.spec;

public interface ISpringCrudifyEntityFactory<T> {
	
	T newInstance();

	T newInstance(String uuid);

}
