package com.topolyai.internet.access;

public interface PostRequestListener<T> {

	void execute(T result);
}
