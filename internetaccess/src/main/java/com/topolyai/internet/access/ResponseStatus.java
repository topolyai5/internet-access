package com.topolyai.internet.access;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Builder;

@Getter
@Setter
@Builder
public class ResponseStatus {

	private int httpStatus;
	private String response;

}
