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

	// TODO: high-performance-modeプロパティは、使用しないのでいずれ削除予定
	@Deprecated(since = "0.0.1", forRemoval = true)
	private boolean highPerformanceMode = false;
}
