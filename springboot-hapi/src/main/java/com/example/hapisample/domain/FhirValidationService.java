package com.example.hapisample.domain;

/**
 * FHIRバリデーションを実施するServiceインタフェース
 */
public interface FhirValidationService {
	String validate(String fhirString);
}
