package com.example.hapisample.domain.service;

import com.example.hapisample.domain.vo.FhirValidationResult;

/**
 * FHIRバリデーションを実施するServiceインタフェース
 */
public interface FhirValidationService {
	FhirValidationResult validate(String fhirString);
}
