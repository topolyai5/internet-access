package io.internetaccess;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Builder;

@Getter
@Setter
@Builder
public class ResponseStatus {

	private int httpStatus;
	private String response;
	private List<Header> headers;

}
