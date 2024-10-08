package com.example.hapisample.app.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.hapisample.domain.service.FhirValidationService;
import com.example.hapisample.domain.vo.FhirValidationResult;

import lombok.RequiredArgsConstructor;

/**
 * FHIRバリデーションを実施するためのRestControllerクラス
 */
@RestController
@RequestMapping("/api/v1/fhir/validate")
@RequiredArgsConstructor
public class FhirValidationRestController {
	private final FhirValidationService service;
	
	/**
	 * JP-CLINSのFHIRバリデーションを実施する
	 * 
	 * @param fhirString バリデーション対象のFHIRデータの文字列
	 * @return バリデーション結果のメッセージ文字列
	 */
	@PostMapping("/clins")
	@ResponseStatus(HttpStatus.OK)
	public FhirValidationResult validateClins(@RequestBody(required = true) String fhirString) {
		return service.validateClins(fhirString);
	}
	
	/**
	 * 健康診断結果報告書のFHIRバリデーションを実施する
	 * 
	 * @param fhirString バリデーション対象のFHIRデータの文字列
	 * @return バリデーション結果のメッセージ文字列
	 */
	@PostMapping("/checkup-report")
	@ResponseStatus(HttpStatus.OK)
	public FhirValidationResult validateCheckupReport(@RequestBody(required = true) String fhirString) {
		return service.validateCheckupReport(fhirString);
	}	
	
	/**
	 * 医療文書（診療情報提供書、退院時サマリ）のFHIRバリデーションを実施する
	 * 
	 * @deprecated JP-CLINSに統合されたことから、このメソッドは削除予定です。代わりに{@link #validateClins(String)}を使用してください。
	 * @param fhirString バリデーション対象のFHIRデータの文字列
	 * @return バリデーション結果のメッセージ文字列
	 */
	@Deprecated(since = "0.0.1", forRemoval = true)
	@PostMapping("/document")
	@ResponseStatus(HttpStatus.OK)
	public FhirValidationResult validate(@RequestBody(required = true) String fhirString) {
		return service.validateDocument(fhirString);
	}
	
}
