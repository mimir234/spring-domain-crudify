package org.sdc.spring.domain.crudify.spec;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class SpringCrudifyEntityHelper {
	
	public static ISpringCrudifyEntity getOneInstance(Class<ISpringCrudifyEntity> clazz) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Constructor<ISpringCrudifyEntity> constructor;
		constructor = (Constructor<ISpringCrudifyEntity>) clazz.getConstructor();
		ISpringCrudifyEntity entity = (ISpringCrudifyEntity) constructor.newInstance();
		return entity;
	}
	
	public static String getDomain(Class<ISpringCrudifyEntity> entity) {
		return entity.getAnnotation(SpringCrudifyEntityDomain.class).name();
	}
	
	@SuppressWarnings("unchecked")
	public static ISpringCrudifyEntityFactory<ISpringCrudifyEntity> getFactory(Class<ISpringCrudifyEntity> clazz) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		return (ISpringCrudifyEntityFactory<ISpringCrudifyEntity>) SpringCrudifyEntityHelper.getOneInstance(clazz).getFactory();
	}

}
