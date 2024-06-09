package com.example.hapisample.domain.service;

import com.example.hapisample.domain.vo.FhirValidationResult;

/**
 * FHIRバリデーションを実施するServiceインタフェース
 */
public interface FhirValidationService {
	/**
	 * 医療文書（診療情報提供書、退院時サマリ）のFHIRバリデーションを実施する
	 * 
	 * @param fhirString バリデーション対象のFHIRデータの文字列
	 * @return バリデーション結果のメッセージ文字列
	 */
	FhirValidationResult validateDocument(String fhirString);
	
	/**
	 * 健康診断結果報告書のFHIRバリデーションを実施する
	 * 
	 * @param fhirString バリデーション対象のFHIRデータの文字列
	 * @return バリデーション結果のメッセージ文字列
	 */
	FhirValidationResult validateCheckupReport(String fhirString);
}
