package com.topolyai.internet.access;

public interface RequestListener {

	void preExecute();
	
	void postExecute(ResponseStatus result);
}
