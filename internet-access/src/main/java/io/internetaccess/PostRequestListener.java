package io.internetaccess;

public interface PostRequestListener<T> {

	void execute(T result, int httpCode, String message);
}
