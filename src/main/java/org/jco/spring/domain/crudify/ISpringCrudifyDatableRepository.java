/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.jco.spring.domain.crudify;

import java.util.Date;
import java.util.List;

public interface ISpringCrudifyDatableRepository<T> {

	List<T> getEntitiesWithDateLessThan(String tenantId, String assetId, Date date);

	boolean doesExists(String tenantId, T entity);

}
