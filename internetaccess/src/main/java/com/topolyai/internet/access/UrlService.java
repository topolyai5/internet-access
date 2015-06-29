package com.topolyai.internet.access;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class UrlService {

	public String post(String targetUrl, List<NameValuePair> nameValuePairs,
			HttpClient client) {
		targetUrl = targetUrl.replaceAll(" ", "+");
		client = (client == null) ? new DefaultHttpClient() : client;

		HttpPost httpPost = new HttpPost(targetUrl);
		if (nameValuePairs == null) {
			targetUrl = validateUrl(targetUrl);
			nameValuePairs = new ArrayList<NameValuePair>();
		}
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e1) {
		}

		return executeRequest(httpPost, client);
	}

	public String post(String targetUrl, List<NameValuePair> nameValuePairs) {
		return post(targetUrl, nameValuePairs, null);
	}

	private String executeRequest(HttpUriRequest request, HttpClient client) {

		String responseBody = "";
		request.addHeader("Content-Type", "text/html; charset=UTF-8");
		client.getParams().setParameter("http.protocol.version",
				HttpVersion.HTTP_1_1);
		client.getParams().setParameter("http.protocol.content-charset",
				"UTF-8");
		try {

			HttpResponse response = client.execute(request);
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				responseBody = EntityUtils.toString(resEntity, "UTF-8");
			}
		} catch (IOException e) {
		}

		return responseBody;
	}

	public String get(String url, HttpClient client) {
		client = (client == null) ? new DefaultHttpClient() : client;
		url = validateUrl(url);
		HttpGet httpGet = new HttpGet(url);
		return executeRequest(httpGet, client);
	}
	
	public String get(String url) {
		return get(url, null);
	}

	private String validateUrl(String url) {
		String ret = url;
		return ret.replaceAll(" ", "%20").replace("|", "%7C");
	}
}
