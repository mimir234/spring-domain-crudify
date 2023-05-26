package org.sdc.spring.domain.crudify.engine;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.sdc.spring.domain.crudify.repository.dao.SpringCrudifyDao;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SpringCrudifyEntity {

	String dto();

	SpringCrudifyDao db() default SpringCrudifyDao.mongo;
	
	boolean authorize_creation() default true;

	boolean authorize_read_all() default true;

	boolean authorize_read_one() default true;

	boolean authorize_update_one() default true;

	boolean authorize_delete_one() default true;

	boolean authorize_delete_all() default true;

	boolean authorize_count() default true;
	
	String controller() default "";

}
