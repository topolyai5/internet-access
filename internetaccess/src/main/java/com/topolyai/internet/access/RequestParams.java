package com.topolyai.internet.access;

import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Builder;

@Getter
@Setter
@Builder
public class RequestParams {

	private List<NameValuePair> nameValuePairs;
	private String url;
	private RequestMethod requestMethod;
	/**
	 * if async is true, the request will be sent in background, otherwise the
	 * app will wait for the response. Please note: if onNewThread is false,
	 * this is unnecessary
	 */
	private boolean async;
	/**
	 * if it is true, the request will be sent in new thread.
	 */
	private boolean onNewThread;
	private String filePath;

	public static RequestParamsBuilder defaultRequesParams() {
		return RequestParams.builder().async(true).onNewThread(true).requestMethod(RequestMethod.GET);
	}

    public static RequestParamsBuilder syncRequesParams() {
        return RequestParams.builder().async(false).onNewThread(true).requestMethod(RequestMethod.GET);
    }

}
