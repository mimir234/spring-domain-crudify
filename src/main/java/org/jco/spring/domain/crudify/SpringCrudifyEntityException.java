/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.jco.spring.domain.crudify;

import lombok.Getter;

@Getter
public class SpringCrudifyEntityException extends Exception {

	private int code = BAD_REQUEST;
	
	public SpringCrudifyEntityException(String string) {
		super(string);
	}

	public SpringCrudifyEntityException(int code, String string) {
		super(string);
		this.code = code;
	}

	public SpringCrudifyEntityException(Exception e) {
		super(e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8581388689485492204L;
	public static final int ENTITY_NOT_FOUND = 1;
	public static final int BAD_REQUEST = 2;

}
