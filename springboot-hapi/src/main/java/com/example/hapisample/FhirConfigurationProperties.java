package com.example.hapisample;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Builder;
import lombok.Data;

/**
 * FHIRバリデーションに関するプロパティクラス
 */
@Data
@Builder
@ConfigurationProperties(prefix = "fhir")
public class FhirConfigurationProperties {
	// ハイパフォーマンスモード（）
	@Deprecated
	private boolean highPerformanceMode = false;
}
