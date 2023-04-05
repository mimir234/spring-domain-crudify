package org.jco.spring.domain.crudify.spec;

public interface ISpringCrudifyEntityFactory<T extends ISpringCrudifyEntity> {
	
	T newInstance();

	T newInstance(String uuid);

}
