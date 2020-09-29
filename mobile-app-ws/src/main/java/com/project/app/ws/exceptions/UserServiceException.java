package com.project.app.ws.exceptions;

public class UserServiceException extends RuntimeException{

	private static final long serialVersionUID = 6759503966526827033L;

	public UserServiceException(String message)
	{
		super(message);
	}
}
