package hapisample;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.hl7.fhir.common.hapi.validation.support.CachingValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.CommonCodeSystemsTerminologyService;
import org.hl7.fhir.common.hapi.validation.support.InMemoryTerminologyServerValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.NpmPackageValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.SnapshotGeneratingValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.IValidatorModule;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;

/**
 * 診療情報提供書のFHIRサンプルデータをパースする
 */
public class ParsingSampleMain {
    private static Logger logger = LoggerFactory.getLogger(ParsingSampleMain.class);

    // （参考）
    // https://hapifhir.io/hapi-fhir/docs/model/parsers.html
    // https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html
    public static void main(String[] args) {
        // あえて分かりやすくするため１つのメソッドに手続き的に書いてあるので、本当に実装したい場合は保守性の高いモジュール化されたコード書くこと
        try {
            // FHIRコンテキスト作成
            FhirContext ctx = FhirContext.forR4();

            // Validatorの作成
            // 診療情報提供書の文書プロファイルのスナップショット形式のnpmパッケージファイルに基づくValidationSuportを追加
            NpmPackageValidationSupport npmPackageEreferralSupport = new NpmPackageValidationSupport(ctx);
            //npmPackageEreferralSupport.loadPackageFromClasspath("classpath:package/jp-ereferral-0.9.7-snap.tgz");
            npmPackageEreferralSupport.loadPackageFromClasspath("classpath:package/jp-ereferral-0.9.7-snap-fixed.tgz");

            // 退院時サマリの文書プロファイルのスナップショット形式のnpmパッケージファイルに基づくValidationSuportを追加
            // 診療情報提供書の文書プロファイルのnpmパッケージだと足りない定義情報あるので
            NpmPackageValidationSupport npmPackageEdissummarySupport = new NpmPackageValidationSupport(ctx);
            npmPackageEdissummarySupport.loadPackageFromClasspath("classpath:package/jp-edissummary-0.9.7-snap.tgz");

            // 診療情報提供書の文書プロファイルのnpmパッケージだけだと足りない定義情報があるので
            // JPCoreのnpmパッケージファイルも読み込むようValidationSupport追加
            NpmPackageValidationSupport npmPackageJPCoreSupport = new NpmPackageValidationSupport(ctx);
            // JPCoreのサイト（https://jpfhir.jp/fhir/core/1.1.1/package.tgz）からダウンロードできるpackage.tgzを利用した場合だと、アウトオブメモリエラーになってしまう
            // npmPackageJPCoreSupport.loadPackageFromClasspath("classpath:package/package.tgz");
            // simplifier.net（https://simplifier.net/packages/jp-core.r4/1.1.1-rc/snapshots/download）のパッケージならアウトオブメモリにならない
            npmPackageJPCoreSupport.loadPackageFromClasspath("classpath:package/jp-core.r4-1.1.1-rc.tgz");

            ValidationSupportChain validationSupportChain = new ValidationSupportChain(//
                    npmPackageEreferralSupport, //
                    npmPackageEdissummarySupport, //
                    npmPackageJPCoreSupport, //
                    // FHIRプロファイルに基づいているかの組み込みの検証ルール
                    new DefaultProfileValidationSupport(ctx), //
                    new CommonCodeSystemsTerminologyService(ctx), //
                    new InMemoryTerminologyServerValidationSupport(ctx), //
                    new SnapshotGeneratingValidationSupport(ctx));
            CachingValidationSupport validationSupport = new CachingValidationSupport(validationSupportChain);
            FhirValidator validator = ctx.newValidator();
            IValidatorModule module = new FhirInstanceValidator(validationSupport);
            validator.registerValidatorModule(module);
            
            // 診療情報提供書のHL7 FHIRのサンプルデータを読み込み
            //String filePath = "file/input/Bundle-BundleReferralExample01.json"
            String filePath = "file/input/Bundle-BundleReferralExample01-fixed.json";                       
            
            // 生のFHIRデータ(json文字列）に対して、直接FHIRバリデーション実行
            String jsonString = Files.readString(Paths.get(filePath));
            ValidationResult validationResult = validator.validateWithResult(jsonString);

            if (validationResult.isSuccessful()) {
                logger.info("ドキュメントは有効です");
            } else {
                logger.warn("ドキュメントに不備があります");
                // 検証結果の出力
                for (SingleValidationMessage validationMessage : validationResult.getMessages()) {
                    logger.warn("[{}]:[{}] {}", validationMessage.getSeverity(), validationMessage.getLocationString(),
                            validationMessage.getMessage());
                }
            }
            
            // パーサを作成
            IParser parser = ctx.newJsonParser();
            // サンプルデータをパースしBundleリソースを取得
            InputStream is = new BufferedInputStream(new FileInputStream(filePath));
            Bundle bundle = parser.parseResource(Bundle.class, is);
            
            // Bundleリソースを解析
            logger.info("Bundle type:{}", bundle.getType().getDisplay());
            // BundleからEntryを取得
            List<BundleEntryComponent> entries = bundle.getEntry();
            String subjectRefId = null;
            // Entry内のResourceを取得
            for (BundleEntryComponent entry : entries) {
                Resource resource = entry.getResource();
                ResourceType resourceType = resource.getResourceType();
                logger.info("Resource Type: {}", resourceType.name());
                switch (resourceType) {
                case Composition:
                    // Compositionリソースを解析する例
                    Composition composition = (Composition) resource;
                    String title = composition.getTitle();
                    logger.info("文書名: {}", title);
                    // subjectの参照先のUUIDを取得
                    Reference subjectRef = composition.getSubject();
                    subjectRefId = subjectRef.getReference();
                    logger.info("subject display: {}", subjectRef.getDisplay());
                    logger.info("subject reference Id: {}", subjectRefId);
                    // TODO: 各参照先のUUIDを取得する処理の追加
                    break;
                case Patient:
                    // Patientリソースを解析する例
                    if (!entry.getFullUrl().equals(subjectRefId)) {
                        break;
                    }
                    logger.info("Composition.subjectの参照先のPatient:{}", subjectRefId);
                    Patient patient = (Patient) resource;
                    // 患者番号の取得
                    logger.info("患者番号:{}", patient.getIdentifier().get(0).getValue());
                    // 患者氏名の取得
                    List<HumanName> humanNames = patient.getName();
                    humanNames.forEach(humanName -> {
                        String valueCode = humanName.getExtensionString(
                                "http://hl7.org/fhir/StructureDefinition/iso21090-EN-representation");
                        if ("IDE".equals(valueCode)) {
                            logger.info("患者氏名:{}", humanName.getText());
                        } else {
                            logger.info("患者カナ氏名:{}", humanName.getText());
                        }
                    });
                    break;
                // TODO: リソース毎に処理の追加
                default:
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("予期せぬエラーが発生しました", e);
        }
    }
}
