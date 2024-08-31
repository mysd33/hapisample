package hapisample;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.hl7.fhir.common.hapi.validation.support.CachingValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.CommonCodeSystemsTerminologyService;
import org.hl7.fhir.common.hapi.validation.support.InMemoryTerminologyServerValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.NpmPackageValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.SnapshotGeneratingValidationSupport;
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
 * マルチスレッド化の環境で、
 * 新しいJP-CLINS（電子カルテ情報共有サービス2文書５情報+患者サマリー）で、診療情報提供書のFHIRサンプルデータをバリデーションする。
 */
public class ValidationInMultiThreads {
	private static Logger logger = LoggerFactory.getLogger(ValidationInMultiThreads.class);
	// スレッドプールのサイズ
	private static final int THREAD_POOL_SIZE = 10;
	// 1テストメソッド当たりのマルチスレッドでのテスト実行回数
	private static final int NUMBER_OF_THREADS = 100;

	// （参考）
	// https://hapifhir.io/hapi-fhir/docs/model/parsers.html
	// https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html
	public static void main(String[] args) throws InterruptedException {
		// ラッチ
		final CountDownLatch latch = new CountDownLatch(NUMBER_OF_THREADS);

		// スレッドプール
		try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE)) {
			// 時間計測
			long startTime = System.nanoTime();
			// FHIRコンテキスト作成（JPCore、各文書プロファイルがR4で定義されているのでR4で作成）
			FhirContext ctx = FhirContext.forR4();
			// 時間計測
			long createContextTime = System.nanoTime();

			// Validatorの作成
			// 新しいJP-CLINSのnpmパッケージファイルに基づくValidationSuportを追加
			NpmPackageValidationSupport npmPackageNewJPClinsSupport = new NpmPackageValidationSupport(ctx);
			// 新しいJP-CLINSは、snapshot形式にすると、エラーが発生するため、diff形式を使用
			npmPackageNewJPClinsSupport.loadPackageFromClasspath(Constants.JP_NEW_CLINS_NPM_PACKAGE);

			// JPCoreのnpmパッケージファイルに基づくValidationSuportを追加
			NpmPackageValidationSupport npmPackageJPCoreSupport = new NpmPackageValidationSupport(ctx);
			// JPCoreは、diff形式にすると、SnapshotGeneratingValidationSupportの処理で、OutOfMemoryエラーが発生する
			npmPackageJPCoreSupport.loadPackageFromClasspath(Constants.JP_CORE_NPM_PACKAGE);

			// JPCoreのTerminologyのnpmパッケージファイルに基づくValidationSuportを追加
			NpmPackageValidationSupport npmPackageTerminologySupport = new NpmPackageValidationSupport(ctx);
			npmPackageTerminologySupport.loadPackageFromClasspath(Constants.JP_FHIR_TERMINOLOGY_NPM_PACKAGE);

			ValidationSupportChain validationSupportChain = new ValidationSupportChain(//
					// FHIRプロファイルに基づいているかの組み込みの検証ルール
					new DefaultProfileValidationSupport(ctx), //
					new CommonCodeSystemsTerminologyService(ctx), //
					new InMemoryTerminologyServerValidationSupport(ctx), //
					npmPackageTerminologySupport, //
					npmPackageJPCoreSupport, //
					npmPackageNewJPClinsSupport, //
					// diff形式の場合にはSnapshotGeneratingValidationSupportを使用する必要がある
					new SnapshotGeneratingValidationSupport(ctx));

			// @formatter:off
			/*
			ValidationSupportChain validationSupportChain = new ValidationSupportChain(//
					npmPackageNewJPClinsSupport, //
					npmPackageJPCoreSupport, //
					npmPackageTerminologySupport, //
					// FHIRプロファイルに基づいているかの組み込みの検証ルール
					new DefaultProfileValidationSupport(ctx), //
					new CommonCodeSystemsTerminologyService(ctx), //
					new InMemoryTerminologyServerValidationSupport(ctx), //
					new SnapshotGeneratingValidationSupport(ctx)
			);*/
			// @formatter:on
			// キャッシュ機能の設定
			CachingValidationSupport validationSupport = new CachingValidationSupport(validationSupportChain);
			FhirValidator validator = ctx.newValidator();
			IValidatorModule module = new FhirInstanceValidator(validationSupport);
			validator.registerValidatorModule(module);

			// 時間計測
			long createValidatorTime = System.nanoTime();

			// 診療情報提供書のHL7 FHIRのサンプルデータを読み込み
			String filePath = "file/input/Bundle-Bundle-CLINS-Referral-Example-01.json";

			logElaspedTime("Context作成時間", startTime, createContextTime);
			logElaspedTime("Validator作成時間", createContextTime, createValidatorTime);

			// バリデーション対象のFHIRデータの文字列を取得
			String jsonString = Files.readString(Paths.get(filePath));

			// TODO: マルチスレッド化でいきなりValidatorを実行すると
			// SnapshotGeneratingValidationSupportでjava.util.ConcurrentModificationExceptionのエラーが発生するので
			// 初回バリデーション実行しておく
			validator.validateWithResult(jsonString);
			// 処理結果の格納用リスト
			List<ValidationResult> results = Collections.synchronizedList(new ArrayList<>());
			// マルチスレッドでのテスト実行
			for (int i = 0; i < NUMBER_OF_THREADS; i++) {
				executor.submit(() -> {
					try {
						logger.info("バリデーション実行開始");
						long validationStartTime = System.nanoTime();
						// FHIRバリデーション実行
						ValidationResult validationResult = validator.validateWithResult(jsonString);
						// 時間計測
						long validationEndTime = System.nanoTime();
						if (validationResult.isSuccessful()) {
							logger.info("ドキュメントは有効です");
						} else {
							logger.warn("ドキュメントに不備があります");
							// 検証結果の出力
							for (SingleValidationMessage validationMessage : validationResult.getMessages()) {
								logger.warn("[{}]:[{}] {}", validationMessage.getSeverity(),
										validationMessage.getLocationString(), validationMessage.getMessage());
							}
						}
						logElaspedTime("Validation処理時間", validationStartTime, validationEndTime);
						// 処理結果の格納
						results.add(validationResult);
					} catch (Exception e) {
						logger.error("予期せぬエラーが発生しました", e);
					} finally {
						latch.countDown();
					}
				});
			}
			latch.await();

			// １つでもバリデーションの処理結果がNGのものがあればば、エラーログを出力
			results.stream().filter(result -> !result.isSuccessful()).forEach(result -> {
				logger.error("バリデーション結果が本来と違うものがあります");
			});

		} catch (InterruptedException e) {
			throw e;
		} catch (Exception e) {
			logger.error("予期せぬエラーが発生しました", e);
		}
	}

	private static void logElaspedTime(String label, long startTime, long endTime) {
		logger.info("{}：{}ms", label, TimeUnit.NANOSECONDS.toMicros(endTime - startTime) / 1000d);
	}

}
