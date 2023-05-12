package org.jco.spring.domain.crudify.engine;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jco.spring.domain.crudify.repository.dao.SpringCrudifyDao;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SpringCrudifyEntity {

	String dto();

	SpringCrudifyDao db();
	
	boolean authorize_creation();

	boolean authorize_read_all();

	boolean authorize_read_one();

	boolean authorize_update_one();

	boolean authorize_delete_one();

	boolean authorize_delete_all();

	boolean authorize_count();

}
