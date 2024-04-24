package hapisample;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.hl7.fhir.common.hapi.validation.support.CachingValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.CommonCodeSystemsTerminologyService;
import org.hl7.fhir.common.hapi.validation.support.InMemoryTerminologyServerValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.NpmPackageValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.IValidatorModule;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;

/**
 * 退院時サマリーのFHIRサンプルデータをバリデーションする
 */
public class ParsingSampleMain2 {
	private static Logger logger = LoggerFactory.getLogger(ParsingSampleMain2.class);

	// （参考）
	// https://hapifhir.io/hapi-fhir/docs/model/parsers.html
	// https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html
	public static void main(String[] args) {
		// あえて分かりやすくするため１つのメソッドに手続き的に書いてあるので、本当に実装したい場合は保守性の高いモジュール化されたコードを書くこと
		try {
			// 時間計測
			long startTime = System.nanoTime();
			// FHIRコンテキスト作成（JPCore、各文書プロファイルがR4で定義されているのでR4で作成）
			FhirContext ctx = FhirContext.forR4();
			// 時間計測
			long createContextTime = System.nanoTime();

			// Validatorの作成
			// 退院時サマリーのnpmパッケージファイルに基づくValidationSuportを追加
			NpmPackageValidationSupport npmPackageEDischargeSummary = new NpmPackageValidationSupport(ctx);
			// npmPackageEDischargeSummary.loadPackageFromClasspath("classpath:package/jp-eDischargeSummary.r4-1.1.6.tgz");
			npmPackageEDischargeSummary
					.loadPackageFromClasspath("classpath:package/jp-eDischargeSummary.r4-1.1.6-snap.tgz");

			// JPCoreのnpmパッケージファイルに基づくValidationSuportを追加
			NpmPackageValidationSupport npmPackageJPCoreSupport = new NpmPackageValidationSupport(ctx);
			// JPCoreは、diff形式にすると、SnapshotGeneratingValidationSupportの処理で、OutOfMemoryエラーが発生する
			// npmPackageJPCoreSupport.loadPackageFromClasspath("classpath:package/jp-core.r4-1.1.2.tgz");
			npmPackageJPCoreSupport.loadPackageFromClasspath("classpath:package/jp-core.r4-1.1.2-snap.tgz");

			// JPCoreのTerminologyのnpmパッケージファイルに基づくValidationSuportを追加
			NpmPackageValidationSupport npmPackageTerminologySupport = new NpmPackageValidationSupport(ctx);
			npmPackageTerminologySupport.loadPackageFromClasspath("classpath:package/jpfhir-terminology.r4-1.1.1.tgz");

			ValidationSupportChain validationSupportChain = new ValidationSupportChain(//
					// FHIRプロファイルに基づいているかの組み込みの検証ルール
					new DefaultProfileValidationSupport(ctx), //
					new CommonCodeSystemsTerminologyService(ctx), //
					new InMemoryTerminologyServerValidationSupport(ctx), //
					npmPackageTerminologySupport, //
					npmPackageJPCoreSupport, //
					npmPackageEDischargeSummary// , //
			// diff形式の場合にはSnapshotGeneratingValidationSupportを使用する必要があるがsnapshotでは不要
			// new SnapshotGeneratingValidationSupport(ctx)
			);
			// @formatter:off
			/*
			ValidationSupportChain validationSupportChain = new ValidationSupportChain(//
					npmPackageEDischargeSummary, //
					npmPackageJPCoreSupport, //
					npmPackageTerminologySupport, //
					// FHIRプロファイルに基づいているかの組み込みの検証ルール
					new DefaultProfileValidationSupport(ctx), //
					new CommonCodeSystemsTerminologyService(ctx), //
					new InMemoryTerminologyServerValidationSupport(ctx)// , //
			// diff形式の場合にはSnapshotGeneratingValidationSupportを使用する必要があるがsnapshotでは不要
			// new SnapshotGeneratingValidationSupport(ctx)
			);*/
			// @fomatter:on
			// キャッシュ機能の設定
			CachingValidationSupport validationSupport = new CachingValidationSupport(validationSupportChain);
			FhirValidator validator = ctx.newValidator();
			IValidatorModule module = new FhirInstanceValidator(validationSupport);
			validator.registerValidatorModule(module);

			// 時間計測
			long createValidatorTime = System.nanoTime();

			// 退院時サマリーのHL7 FHIRのサンプルデータを読み込み
			//String filePath = "file/input/Bundle-BundleReferralExample01.json";
			String filePath = "file/input/Todo.json";

			// 生のFHIRデータ(json文字列）に対して、直接FHIRバリデーション実行
			String jsonString = Files.readString(Paths.get(filePath));
			logger.info("バリデーション初回");
			// 初回
			ValidationResult validationResult = validator.validateWithResult(jsonString);
			// 時間計測
			long validationTime = System.nanoTime();
			// 処理時間変化の確認のため、もう一回2回目実行
			logger.info("バリデーション2回目");
			validationResult = validator.validateWithResult(jsonString);
			// 時間計測2
			long validationTime2 = System.nanoTime();

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

			logElaspedTime("Context作成時間", startTime, createContextTime);
			logElaspedTime("Validator作成時間", createContextTime, createValidatorTime);
			logElaspedTime("Validation処理時間（初回）", createValidatorTime, validationTime);
			logElaspedTime("Validation処理時間（2回目）", validationTime, validationTime2);

		} catch (Exception e) {
			logger.error("予期せぬエラーが発生しました", e);
		}
	}

	private static void logElaspedTime(String label, long startTime, long endTime) {						
		logger.info("{}：{}ms", label, TimeUnit.NANOSECONDS.toMicros(endTime - startTime) / 1000d);
	}

}
