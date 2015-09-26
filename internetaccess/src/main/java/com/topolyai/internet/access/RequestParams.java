package com.topolyai.internet.access;

import java.util.List;

import org.apache.http.NameValuePair;

public class RequestParams {

	private List<NameValuePair> nameValuePairs;
	private String url;
	private String requestType;
	private boolean async;
	private boolean onNewThread;
	private String filePath;
	private Class<?> responseClass;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Class<?> getResponseClass() {
		return responseClass;
	}

	public void setResponseClass(Class<?> responseClass) {
		this.responseClass = responseClass;
	}

	public List<NameValuePair> getNameValuePairs() {
		return nameValuePairs;
	}

	public void setNameValuePairs(List<NameValuePair> nameValuePairs) {
		this.nameValuePairs = nameValuePairs;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	/**
	 * if async is true, the request will be sent in background, otherwise the
	 * app will wait for the response. Please note: if onNewThread is false,
	 * this is unnecessary
	 * 
	 * @return
	 */
	public boolean isAsync() {
		return async;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

	/**
	 * if it is true, the request will be sent in new thread.
	 * 
	 * @return
	 */
	public boolean isOnNewThread() {
		return onNewThread;
	}

	public void setOnNewThread(boolean onNewThread) {
		this.onNewThread = onNewThread;
	}

	public static RequestParams getDefaultRequesParams() {
		return getDefaultRequesParams(true);
	}

	public static RequestParams getDefaultRequesParams(boolean async) {
		RequestParams requestParams = new RequestParams();
		requestParams.setAsync(async);
		requestParams.setOnNewThread(true);
		requestParams.setRequestType("get");
		return requestParams;
	}

}
