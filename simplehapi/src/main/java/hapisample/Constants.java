package hapisample;

/**
 * 定数定義クラス
 */
public final class Constants {
    private Constants() {
    }

    // JP Core
    public static final String JP_CORE_NPM_PACKAGE = "classpath:package/jp-core.r4-1.1.2-url-snap.tgz";

    // TODO: 新しいバージョンだとdiff形式でどうなるか？
    // JPCoreは、diff形式にすると、SnapshotGeneratingValidationSupportの処理で、OutOfMemoryエラーが発生する
    // public static final String JP_CORE_NPM_PACKAGE = "classpath:package/jp-core.r4-1.1.2-url.tgz";

    // JP FHIR Terminology
    public static final String JP_FHIR_TERMINOLOGY_NPM_PACKAGE = "classpath:package/jpfhir-terminology.r4-1.2.5-url.tgz";

    // 新JP-CLINS(電子カルテ情報共有サービス2文書5情報+患者サマリー)
    // TODO: 新しいバージョンだと、スナップショット形式でどうなるか？
    // public static final String JP_NEW_CLINS_NPM_PACKAGE = "classpath:package/jp-eCheckupReport.r4-1.1.2-snap.tgz";
    // スナップショット形式だとエラーにになってしまうため、diff形式を使用
    public static final String JP_NEW_CLINS_NPM_PACKAGE = "classpath:package/jp-eCSCLINS.r4-1.6.0.tgz";

    // 健康診断結果報告書
    public static final String JP_E_CHECKUP_REPORT_NPM_PACKAGE = "classpath:package/jp-eCheckupReport.r4-1.3.2-snap.tgz";

    // 診療情報提供書 old
    @Deprecated(since = "0.0.1", forRemoval = true)
    public static final String JP_E_REFERRAL_NPM_PACKAGE = "classpath:package/old/jp-eReferral.r4-1.1.6-snap.tgz";
    // 退院時サマリ old
    @Deprecated(since = "0.0.1", forRemoval = true)
    public static final String JP_E_DISCHARGE_SUMMARY_NPM_PACKAGE = "classpath:package/old/jp-eDischargeSummary.r4-1.1.6-snap.tgz";
    // JP-CLINS old
    @Deprecated(since = "0.0.1", forRemoval = true)
    public static final String JP_CLINS_NPM_PACKAGE = "classpath:package/old/jp-clins.r4-0.9.13-snap.tgz";

}
