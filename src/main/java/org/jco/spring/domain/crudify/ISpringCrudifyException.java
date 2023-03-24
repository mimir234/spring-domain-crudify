/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package org.jco.spring.domain.crudify;

public class ISpringCrudifyException extends Exception {

	public ISpringCrudifyException(String string, Exception e) {
		super( string, e);
	}

	public ISpringCrudifyException(String string) {
		super( string);
	}

	public ISpringCrudifyException(Exception e) {
		super(e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7250698341986031569L;

}
