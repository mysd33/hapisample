package com.example.hapisample.domain.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

/**
 * FHIRバリデーションの結果を格納するValueObject
 */
@Builder
@Getter
public class FhirValidationResult {
	public static final String OK = "OK";
	public static final String NG = "NG";
	private final String result;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private final List<String> details;
	
}
