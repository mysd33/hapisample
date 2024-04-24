package com.example.hapisample;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * FHIRバリデーションに関するプロパティクラス
 */
@Data
@ConfigurationProperties(prefix = "fhir")
public class FhirConfigurationProperties {
	// ハイパフォーマンスモード
	private boolean highPerformanceMode = false;
}
