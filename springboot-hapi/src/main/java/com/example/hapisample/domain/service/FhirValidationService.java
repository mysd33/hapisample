package com.example.hapisample.domain.service;

import com.example.hapisample.domain.vo.FhirValidationResult;

/**
 * FHIRバリデーションを実施するServiceインタフェース
 */
public interface FhirValidationService {
	
	/**
	 * JP-CLINSのFHIRバリデーションを実施する
	 * 
	 * @param fhirString バリデーション対象のFHIRデータの文字列
	 * @return バリデーション結果のメッセージ文字列
	 * 
	 */
	FhirValidationResult validateClins(String fhirString);
	
	/**
	 * 健康診断結果報告書のFHIRバリデーションを実施する
	 * 
	 * @param fhirString バリデーション対象のFHIRデータの文字列
	 * @return バリデーション結果のメッセージ文字列
	 */
	FhirValidationResult validateCheckupReport(String fhirString);
		
	/**
	 * 医療文書（診療情報提供書、退院時サマリ）のFHIRバリデーションを実施する
	 * 
	 * @deprecated JP-CLINSに統合されたことから、このメソッドは削除予定です。代わりに{@link #validateClins(String)}を使用してください。
	 * 
	 * @param fhirString バリデーション対象のFHIRデータの文字列
	 * @return バリデーション結果のメッセージ文字列
	 */
	@Deprecated(since = "0.0.1", forRemoval = true)
	FhirValidationResult validateDocument(String fhirString);
	
}
