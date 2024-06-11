package com.example.hapisample;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.example.hapisample.domain.service.FhirValidationServiceImpl;
import com.example.hapisample.domain.vo.FhirValidationResult;

import ca.uhn.fhir.context.FhirContext;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * 
 * FhirValidationの性能改善版と通常版でのバリデーション結果に差異がないことを確認する比較テストコードの例<br>
 * 
 * 性能改善版の通常版でのバリデーション結果に差異がないか自動検知する
 * 
 */
class FhirValidationCompareTest {
	// 暖機処理用のFHIRのデータファイル
	private static final String INIT_FOR_FHIR_REFERRAL_FILE_PATH = "file/Bundle-BundleReferralExample01.json";
	private static final String INIT_FOR_FHIR_CHECKUP_REPORT_FILE_PATH = "file/Bundle-Bundle-eCheckupReport-Sample-01.json";
	private static final String INIT_FOR_FHIR_CLINS_FILE_PATH = "file/AllergyIntolerance-Example-JP-AllergyIntolerance-CLINS-eCS-01.json";
	// テスト対象の通常のFHIR Validation機能
	private static FhirValidationServiceImpl defaultSut;
	// テスト対象の性能改善版のFHIR Validation機能
	private static FhirValidationServiceImpl highPerformanceSut;

	// テスト対象を高速に起動できるように@BeforeAllで初期化しておく
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		// ログをデバックレベルに設定
		((Logger) LoggerFactory.getLogger(FhirConfig.class)).setLevel(Level.DEBUG);
		((Logger) LoggerFactory.getLogger(FhirValidationServiceImpl.class)).setLevel(Level.DEBUG);
		// FhirConfigのBean定義通りに、FhirValidationServiceImplインスタンスを作成
		initDefaultSut();
		initHighPerformanceSut();
	}

	// 通常のFHIR Validation機能の初期化
	private static void initDefaultSut() throws IOException, NoSuchFieldException, IllegalAccessException {
		FhirConfig fhirConfig = new FhirConfig();
		FhirContext ctx = fhirConfig.fhirContext();
		defaultSut = new FhirValidationServiceImpl(ctx, fhirConfig.fhirDocumentValidator(ctx),
				fhirConfig.fhirCheckupReportValidator(ctx), fhirConfig.fhirClinsValidator(ctx));
		initValidator(defaultSut);
	}

	// 性能改善版のFHIR Validation機能の初期化
	private static void initHighPerformanceSut() throws IOException, NoSuchFieldException, IllegalAccessException {
		FhirHighPerformanceConfig fhirConfig = new FhirHighPerformanceConfig();
		FhirContext ctx = fhirConfig
				.fhirContext(FhirConfigurationProperties.builder().highPerformanceMode(true).build());
		highPerformanceSut = new FhirValidationServiceImpl(ctx, fhirConfig.fhirDocumentValidator(ctx),
				fhirConfig.fhirCheckupReportValidator(ctx), fhirConfig.fhirClinsValidator(ctx));
		initValidator(highPerformanceSut);
	}

	// 暖機処理
	private static void initValidator(FhirValidationServiceImpl service) throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException, IOException {
		// 医療文書
		Resource initDocumentDataResourceValue = new ClassPathResource(INIT_FOR_FHIR_REFERRAL_FILE_PATH);
		Field initDocumentDataResourceField = service.getClass().getDeclaredField("initDocumentDataResource");
		initDocumentDataResourceField.setAccessible(true);
		initDocumentDataResourceField.set(service, initDocumentDataResourceValue);
		// 健康診断結果報告書
		Resource initCheckupReportDataResourcValue = new ClassPathResource(INIT_FOR_FHIR_CHECKUP_REPORT_FILE_PATH);
		Field initCheckupReportDataResourceField = service.getClass().getDeclaredField("initCheckupReportDataResource");
		initCheckupReportDataResourceField.setAccessible(true);
		initCheckupReportDataResourceField.set(service, initCheckupReportDataResourcValue);
		// 臨床情報
		Resource initClinsDataResourceValue = new ClassPathResource(INIT_FOR_FHIR_CLINS_FILE_PATH);
		Field initClinsDataResourceField = service.getClass().getDeclaredField("initClinsDataResource");
		initClinsDataResourceField.setAccessible(true);
		initClinsDataResourceField.set(service, initClinsDataResourceValue);
		// 暖機処理（initメソッド）を呼び出しておく
		service.init();

	}

	// データドリブンテスト
	@ParameterizedTest
	@MethodSource
	void testValidateDocument(String inputFilePath) throws IOException {
		String jsonString = Files.readString(new ClassPathResource(inputFilePath).getFile().toPath());
		// 通常版でのFHIRバリデーション実行結果を期待値とする
		FhirValidationResult expected = defaultSut.validateDocument(jsonString);
		// 性能改善版でのFHIRバリデーション実行結果を実際の値として比較する。
		FhirValidationResult actual = highPerformanceSut.validateDocument(jsonString);
		// バリデーション結果の比較検証
		assertEquals(expected, actual);
	}

	// テストケース
	static Stream<Arguments> testValidateDocument() {
		return Stream.of(
				// テストケース1
				arguments("testdata/Bundle-BundleReferralExample01.json"),
				// テストケース2
				arguments("testdata/Bundle-BundleReferralExample02.json")
		// TODO: 以降に、テストケースを追加していく
		);
	}

	// データドリブンテスト
	@ParameterizedTest
	@MethodSource
	void testValidateCheckupReport(String inputFilePath) throws IOException {
		String jsonString = Files.readString(new ClassPathResource(inputFilePath).getFile().toPath());
		// 通常版でのFHIRバリデーション実行結果を期待値とする
		FhirValidationResult expected = defaultSut.validateCheckupReport(jsonString);
		// 性能改善版でのFHIRバリデーション実行結果を実際の値として比較する。
		FhirValidationResult actual = highPerformanceSut.validateCheckupReport(jsonString);
		// バリデーション結果の比較検証
		assertEquals(expected, actual);
	}

	// テストケース
	static Stream<Arguments> testValidateCheckupReport() {
		return Stream.of(
				// テストケース1
				arguments("testdata/Bundle-Bundle-eCheckupReport-Sample-01.json")
		// TODO: 以降に、テストケースを追加していく
		);
	}

	// データドリブンテスト
	@ParameterizedTest
	@MethodSource
	void testValidateClins(String inputFilePath) throws IOException {
		String jsonString = Files.readString(new ClassPathResource(inputFilePath).getFile().toPath());
		// 通常版でのFHIRバリデーション実行結果を期待値とする
		FhirValidationResult expected = defaultSut.validateClins(jsonString);
		// 性能改善版でのFHIRバリデーション実行結果を実際の値として比較する。
		FhirValidationResult actual = highPerformanceSut.validateClins(jsonString);
		// バリデーション結果の比較検証
		assertEquals(expected, actual);
	}

	// テストケース
	static Stream<Arguments> testValidateClins() {
		return Stream.of(
				// テストケース1
				arguments("testdata/AllergyIntolerance-Example-JP-AllergyIntolerance-CLINS-eCS-01.json"),
				
				//TODO: 通常モードではOKだが、パフォーマンス改善モードだとエラーになり、バリデーション結果が異なってしまう
				// テストケース2
				arguments("testdata/AllergyIntolerance-Example-JP-AllergyIntolerance-CLINS-eCS-02.json"),
				// テストケース3
				arguments("testdata/AllergyIntolerance-Example-JP-DrugContraindications-CLINS-eCS-03.json")
		// TODO: 以降に、テストケースを追加していく
		);
	}
}
