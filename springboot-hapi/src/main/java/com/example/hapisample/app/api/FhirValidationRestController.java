package com.example.hapisample.app.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.hapisample.domain.FhirValidationService;

import lombok.RequiredArgsConstructor;

/**
 * FHIRバリデーションを実施するためのRestControllerクラス
 */
@RestController
@RequestMapping("/api/v1/fhir")
@RequiredArgsConstructor
public class FhirValidationRestController {
	private final FhirValidationService service;

	/**
	 * FHIRバリデーションを実施する
	 * 
	 * @param fhirString バリデーション対象のFHIRデータの文字列
	 * @return バリデーション結果のメッセージ文字列
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.OK)
	public String validate(@RequestBody(required = true) String fhirString) {
		// 本来はJSONでのレスポンスが望ましいが、ここでは簡単のため、バリデーションのエラーメッセージの文字列をそのまま返却している
		return service.validate(fhirString);
	}
}
