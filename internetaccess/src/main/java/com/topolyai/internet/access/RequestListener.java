package com.topolyai.internet.access;

public interface RequestListener<T> {

	void preExecute();
	
	void postExecute(T result);
}
