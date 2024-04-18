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
 * 診療情報提供書のFHIRサンプルデータをバリデーション＆パースする
 */
public class ParsingSampleMain {
	private static Logger logger = LoggerFactory.getLogger(ParsingSampleMain.class);

	// （参考）
	// https://hapifhir.io/hapi-fhir/docs/model/parsers.html
	// https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html
	public static void main(String[] args) {
		// あえて分かりやすくするため１つのメソッドに手続き的に書いてあるので、本当に実装したい場合は保守性の高いモジュール化されたコードを書くこと
		try {
			// 時間計測
			long startTime = System.currentTimeMillis();
			// FHIRコンテキスト作成（JPCore、各文書プロファイルがR4で定義されているのでR4で作成）
			FhirContext ctx = FhirContext.forR4();
			// 時間計測
			long createContextTime = System.currentTimeMillis();

			// Validatorの作成
			// 診療情報提供書のnpmパッケージファイルに基づくValidationSuportを追加
			NpmPackageValidationSupport npmPackageEReferralSupport = new NpmPackageValidationSupport(ctx);
			// npmPackageEReferralSupport.loadPackageFromClasspath("classpath:package/jp-eReferral.r4-1.1.6.tgz");
			npmPackageEReferralSupport.loadPackageFromClasspath("classpath:package/jp-eReferral.r4-1.1.6-snap.tgz");

			// JPCoreのnpmパッケージファイルに基づくValidationSuportを追加
			NpmPackageValidationSupport npmPackageJPCoreSupport = new NpmPackageValidationSupport(ctx);
			// JPCoreは、diff形式にすると、SnapshotGeneratingValidationSupportの処理で、OutOfMemoryエラーが発生する
			// npmPackageJPCoreSupport.loadPackageFromClasspath("classpath:package/jp-core.r4-1.1.2.tgz");
			npmPackageJPCoreSupport.loadPackageFromClasspath("classpath:package/jp-core.r4-1.1.2-snap.tgz");

			// JPCoreのTerminologyのnpmパッケージファイルに基づくValidationSuportを追加
			NpmPackageValidationSupport npmPackageTerminologySupport = new NpmPackageValidationSupport(ctx);
			npmPackageTerminologySupport.loadPackageFromClasspath("classpath:package/jpfhir-terminology.r4-1.1.1.tgz");

			ValidationSupportChain validationSupportChain = new ValidationSupportChain(//
					npmPackageEReferralSupport, npmPackageJPCoreSupport, npmPackageTerminologySupport,
					// FHIRプロファイルに基づいているかの組み込みの検証ルール
					new DefaultProfileValidationSupport(ctx), //
					new CommonCodeSystemsTerminologyService(ctx), //
					new InMemoryTerminologyServerValidationSupport(ctx)// , //
			// diff形式の場合にはSnapshotGeneratingValidationSupportを使用する必要があるがsnapshotでは不要
			// new SnapshotGeneratingValidationSupport(ctx)
			);
			// キャッシュ機能の設定
			CachingValidationSupport validationSupport = new CachingValidationSupport(validationSupportChain);
			FhirValidator validator = ctx.newValidator();
			IValidatorModule module = new FhirInstanceValidator(validationSupport);
			validator.registerValidatorModule(module);

			// 時間計測
			long createValidatorTime = System.currentTimeMillis();

			// 診療情報提供書のHL7 FHIRのサンプルデータを読み込み
			String filePath = "file/input/Bundle-BundleReferralExample01.json";

			// 生のFHIRデータ(json文字列）に対して、直接FHIRバリデーション実行
			String jsonString = Files.readString(Paths.get(filePath));
			logger.info("バリデーション初回");
			// 初回
			ValidationResult validationResult = validator.validateWithResult(jsonString);
			// 時間計測
			long validationTime = System.currentTimeMillis();
			// 処理時間変化の確認のため、もう一回2回目実行
			logger.info("バリデーション2回目");
			validationResult = validator.validateWithResult(jsonString);
			// 時間計測2
			long validationTime2 = System.currentTimeMillis();

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

			// 時間計測
			long parseStartTime = System.currentTimeMillis();
			// パーサを作成
			IParser parser = ctx.newJsonParser();
			// 時間計測
			long createParserTime = System.currentTimeMillis();

			// サンプルデータをパースしBundleリソースを取得
			InputStream is = new BufferedInputStream(new FileInputStream(filePath));
			Bundle bundle = parser.parseResource(Bundle.class, is);
			// 時間計測
			long parseTime = System.currentTimeMillis();

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
			// 時間計測
			long walkFhirModelTime = System.currentTimeMillis();

			logElaspedTime("Context作成時間", startTime, createContextTime);
			logElaspedTime("Validator作成時間", createContextTime, createValidatorTime);
			logElaspedTime("Validation処理時間（初回）", createValidatorTime, validationTime);
			logElaspedTime("Validation処理時間（2回目）", validationTime, validationTime2);
			logElaspedTime("Parser作成時間", parseStartTime, createParserTime);
			logElaspedTime("Parse処理時間", createParserTime, parseTime);
			logElaspedTime("モデル処理時間", parseTime, walkFhirModelTime);

		} catch (Exception e) {
			logger.error("予期せぬエラーが発生しました", e);
		}
	}

	private static void logElaspedTime(String label, long startTime, long endTime) {
		logger.info("{}：{}ms", label, endTime - startTime);
	}

}
