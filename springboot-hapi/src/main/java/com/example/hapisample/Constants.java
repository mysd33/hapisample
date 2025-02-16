package com.example.hapisample;

/**
 * 定数定義クラス
 */
public final class Constants {
    private Constants() {
    }

    // JP Core
    // TODO: 新しいバージョンだとdiff形式でどうなるか？
    // JPCoreは、diff形式にすると、SnapshotGeneratingValidationSupportの処理で、OutOfMemoryエラーが発生する
    // public static final String JP_CORE_NPM_PACKAGE =
    // "classpath:package/jp-core.r4-1.1.2-url.tgz";
    public static final String JP_CORE_NPM_PACKAGE = "classpath:package/jp-core.r4-1.1.2-url-snap.tgz";

    // JP FHIR Terminology
    public static final String JP_FHIR_TERMINOLOGY_NPM_PACKAGE = "classpath:package/jpfhir-terminology.r4-1.3.0.tgz";

    // 新JP-CLINS(電子カルテ情報共有サービス2文書5情報+患者サマリー)
    // TODO: 新しいバージョンだと、スナップショット形式でどうなるか？
    // public static final String JP_NEW_CLINS_NPM_PACKAGE =
    // "classpath:package/eCSCLINS.r4-1.10.0-snap.tgz";
    // スナップショット形式だとエラーにになってしまうため、diff形式を使用
    public static final String JP_NEW_CLINS_NPM_PACKAGE = "classpath:package/jp-eCSCLINS.r4-1.10.0.tgz";

    // 健康診断結果報告書
    public static final String JP_E_CHECKUP_REPORT_NPM_PACKAGE = "classpath:package/jp-eCheckupReport.r4-1.5.0-snap.tgz";

}
