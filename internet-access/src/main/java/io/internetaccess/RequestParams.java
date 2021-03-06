package io.internetaccess;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.nio.charset.Charset;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.entity.ContentType;
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
	private Bitmap bitmap;
	private ContentType contentType;
    private AsyncTask asyncTask;
    private List<Header> headers;
	private String json;

	public static RequestParamsBuilder defaultRequestParams() {
		return RequestParams.builder().async(true).onNewThread(true).contentType(ContentType.APPLICATION_JSON).requestMethod(RequestMethod.GET);
	}

    public static RequestParamsBuilder syncRequestParams() {
        return RequestParams.builder().async(false).onNewThread(true).contentType(ContentType.APPLICATION_JSON).requestMethod(RequestMethod.GET);
    }

	public static RequestParamsBuilder requestParamsDefaultContentType() {
		return RequestParams.builder().async(true).onNewThread(true).contentType(ContentType.create("text/html", Charset.forName("UTF-8"))).requestMethod(RequestMethod.GET);
	}

	public static RequestParamsBuilder syncRequestParamsDefaultContentTyp() {
		return RequestParams.builder().async(false).onNewThread(true).requestMethod(RequestMethod.GET);
	}

}
