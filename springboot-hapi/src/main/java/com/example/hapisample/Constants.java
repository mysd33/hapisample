package com.example.hapisample;

/**
 * 定数定義クラス
 */
public final class Constants {
	private Constants() {
	}

	// JP Core
	public static final String JP_CORE_NPM_PACKAGE = "classpath:package/jp-core.r4-1.1.2-snap.tgz";
	// JPCoreは、diff形式にすると、SnapshotGeneratingValidationSupportの処理で、OutOfMemoryエラーが発生する
	//public static final String JP_CORE_NPM_PACKAGE = "classpath:package/jp-core.r4-1.1.2.tgz";
	
	// JP FHIR Terminology
	public static final String JP_FHIR_TERMINOLOGY_NPM_PACKAGE = "classpath:package/jpfhir-terminology.r4-1.2.0.tgz";
	
	// 新JP-CLINS(電子カルテ情報共有サービス2文書5情報+患者サマリー)
	//public static final String JP_NEW_CLINS_NPM_PACKAGE = "classpath:package/jp-clins.r4-1.3.0-rc3-snap.tgz";
	// スナップショット形式だとエラーにになってしまうため、diff形式を使用
	public static final String JP_NEW_CLINS_NPM_PACKAGE = "classpath:package/jp-clins.r4-1.3.0-rc3.tgz";

	// 診療情報提供書 old
	public static final String JP_E_REFERRAL_NPM_PACKAGE = "classpath:package/old/jp-eReferral.r4-1.1.6-snap.tgz";
	// 退院時サマリ old
	public static final String JP_E_DISCHARGE_SUMMARY_NPM_PACKAGE = "classpath:package/old/jp-eDischargeSummary.r4-1.1.6-snap.tgz";
	// JP-CLINS old
	public static final String JP_CLINS_NPM_PACKAGE = "classpath:package/old/jp-clins.r4-0.9.13-snap.tgz";
	// 健康診断結果報告書
	public static final String JP_E_CHECKUP_REPORT_NPM_PACKAGE = "classpath:package/jp-eCheckupReport.r4-1.1.2-snap.tgz";

}
