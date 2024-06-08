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
		sut = new FhirValidationServiceImpl(ctx, fhirConfig.fhirValidator(ctx));		
		// 暖機処理（initメソッド）を呼び出しておく
		Resource initDataResourceValue = new ClassPathResource("file/Bundle-BundleReferralExample01.json");
		Field initDataResourceField = sut.getClass().getDeclaredField("initDataResource");
		initDataResourceField.setAccessible(true);
		initDataResourceField.set(sut, initDataResourceValue);
		sut.init();
	}

	// データドリブンテスト
	@ParameterizedTest
	@MethodSource
	void testValidate(String inputFilePath, String expectedResult, List<String> errorMessages) throws IOException {
		String jsonString = Files.readString(new ClassPathResource(inputFilePath).getFile().toPath());
		FhirValidationResult expected;
		if (FhirValidationResult.OK.equals(expectedResult)) {
			expected = FhirValidationResult.builder().result(expectedResult).build();
		} else {
			expected = FhirValidationResult.builder().result(expectedResult).details(errorMessages).build();
		}
		// FHIRバリデーション実行
		FhirValidationResult actual = sut.validate(jsonString);
		// バリデーション結果の検証
		assertEquals(expected, actual);
	}

	// テストケース
	static Stream<Arguments> testValidate() {
		return Stream.of(
				// テストケース1
				arguments("testdata/Bundle-BundleReferralExample01.json", FhirValidationResult.OK, null),
				// テストケース2
				arguments("testdata/Bundle-BundleReferralExample02.json", FhirValidationResult.NG, List.of(
						"[ERROR]:[Bundle] Rule bdl-3: 'Entry.Requestバッチ/トランザクション/履歴に必須、それ以外の場合は禁止されています / entry.request mandatory for batch/transaction/history, otherwise prohibited' Failed",
						"[ERROR]:[Bundle] Rule bdl-4: 'Batch-Response/Transaction-Response/historyに必須であり、それ以外の場合は禁止されています / entry.response mandatory for batch-response/transaction-response/history, otherwise prohibited' Failed",
						"[ERROR]:[Bundle] Rule bdl-12: 'メッセージには最初のリソースとしてメッセージヘッダーが必要です / A message must have a MessageHeader as the first resource' Failed",
						"[ERROR]:[Bundle] Bundle.type: minimum required = 1, but only found 0 (from http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral)"))
		// TODO: 以降に、テストケースを追加していく
		);

	}
}