package com.startupbidder.vo;

public interface ErrorCodes {
	int OK = 0;
	// security exceptions
	int NOT_LOGGED_IN = 1;
	int NOT_AN_OWNER = 2;
	int NOT_AN_ADMIN = 3;
	
	// validation
	int ENTITY_VALIDATION = 10;
	
	// general errors
	int DATASTORE_ERROR = 100;
}
