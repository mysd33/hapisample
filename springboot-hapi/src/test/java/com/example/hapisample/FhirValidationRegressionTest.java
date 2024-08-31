package com.example.hapisample;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.List;
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
 * FhirValidationの自動回帰テストコードの例<br>
 * 
 * プロファイルの改訂やHAPIのバージョンアップ等の際、SpringBootを起動することなく、バリデーションのロジックのみを高速に自動回帰テストできるようにする仕組みを想定したテストコード
 */
class FhirValidationRegressionTest {
	// 暖機処理用のFHIRのデータファイル
	private static final String INIT_FOR_FHIR_CLINS_FILE_PATH = "file/Bundle-Bundle-CLINS-Referral-Example-01.json";
	private static final String INIT_FOR_FHIR_CHECKUP_REPORT_FILE_PATH = "file/Bundle-Bundle-eCheckupReport-Sample-01.json";

	// テスト対象
	private static FhirValidationServiceImpl sut;

	// テスト対象を高速に起動できるように@BeforeAllで初期化しておく
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		// ログをデバックレベルに設定
		((Logger) LoggerFactory.getLogger(FhirConfig.class)).setLevel(Level.DEBUG);
		((Logger) LoggerFactory.getLogger(FhirValidationServiceImpl.class)).setLevel(Level.DEBUG);
		// FhirConfigのBean定義通りに、FhirValidationServiceImplインスタンスを作成
		FhirConfig fhirConfig = new FhirConfig();
		FhirContext ctx = fhirConfig.fhirContext();
		sut = new FhirValidationServiceImpl(ctx, fhirConfig.fhirClinsValidator(ctx),
				fhirConfig.fhirCheckupReportValidator(ctx));
		// 暖機処理
		// JP-CLINS
		Resource initClinsDataResourceValue = new ClassPathResource(INIT_FOR_FHIR_CLINS_FILE_PATH);
		Field initClinsDataResourceField = sut.getClass().getDeclaredField("initClinsDataResource");
		initClinsDataResourceField.setAccessible(true);
		initClinsDataResourceField.set(sut, initClinsDataResourceValue);
		// 健康診断結果報告書
		Resource initCheckupReportDataResourcValue = new ClassPathResource(INIT_FOR_FHIR_CHECKUP_REPORT_FILE_PATH);
		Field initCheckupReportDataResourceField = sut.getClass().getDeclaredField("initCheckupReportDataResource");
		initCheckupReportDataResourceField.setAccessible(true);
		initCheckupReportDataResourceField.set(sut, initCheckupReportDataResourcValue);
		// 暖機処理（initメソッド）を呼び出しておく
		sut.init();
	}


	// データドリブンテスト
	@ParameterizedTest
	@MethodSource
	void testValidateCheckupReport(String inputFilePath, String expectedResult, List<String> errorMessages)
			throws IOException {
		String jsonString = Files.readString(new ClassPathResource(inputFilePath).getFile().toPath());
		FhirValidationResult expected;
		if (FhirValidationResult.OK.equals(expectedResult)) {
			expected = FhirValidationResult.builder().result(expectedResult).build();
		} else {
			expected = FhirValidationResult.builder().result(expectedResult).details(errorMessages).build();
		}
		// FHIRバリデーション実行
		FhirValidationResult actual = sut.validateCheckupReport(jsonString);
		// バリデーション結果の検証
		assertEquals(expected, actual);
	}

	// テストケース
	static Stream<Arguments> testValidateCheckupReport() {
		return Stream.of(
				// テストケース1
				// org.hl7.fhir.validationのバージョンを6.1.4に変更することで、健診結果報告書のバリデーションがOKになる
				arguments("testdata/Bundle-Bundle-eCheckupReport-Sample-01.json", FhirValidationResult.OK, null));

	}

	// JP-CLINSのデータドリブンテスト
	@ParameterizedTest
	@MethodSource
	void testValidateClins(String inputFilePath, String expectedResult, List<String> errorMessages) throws IOException {
		String jsonString = Files.readString(new ClassPathResource(inputFilePath).getFile().toPath());
		FhirValidationResult expected;
		if (FhirValidationResult.OK.equals(expectedResult)) {
			expected = FhirValidationResult.builder().result(expectedResult).build();
		} else {
			expected = FhirValidationResult.builder().result(expectedResult).details(errorMessages).build();
		}
		// FHIRバリデーション実行
		FhirValidationResult actual = sut.validateClins(jsonString);
		// バリデーション結果の検証
		assertEquals(expected, actual);
	}

	// テストケース
	static Stream<Arguments> testValidateClins() {
		return Stream.of(
				// テストケース1
				arguments("testdata/Bundle-Bundle-CLINS-Referral-Example-01.json",
						FhirValidationResult.OK, null),
				// テストケース2
				arguments("testdata/Bundle-Bundle-CLINS-PCS-Example-01.json",
						FhirValidationResult.OK, null),				
				// テストケース3
				// HAPI ver7.2.2ではバリデーションエラーだったが、バリデーションOKになる
				arguments("testdata/Bundle-Bundle-CLINS-Observations-Example-01.json",
						FhirValidationResult.OK, null)				
		// TODO: 以降に、テストケースを追加していく
		);
	}
}
