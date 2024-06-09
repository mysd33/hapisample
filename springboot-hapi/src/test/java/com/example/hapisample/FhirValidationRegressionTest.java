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
		sut = new FhirValidationServiceImpl(ctx, fhirConfig.fhirDocumentValidator(ctx),
				fhirConfig.fhirCheckupReportValidator(ctx));
		// 暖機処理（initメソッド）を呼び出しておく
		Resource initDocumentDataResourceValue = new ClassPathResource("file/Bundle-BundleReferralExample01.json");
		Field initDocumentDataResourceField = sut.getClass().getDeclaredField("initDocumentDataResource");
		initDocumentDataResourceField.setAccessible(true);
		initDocumentDataResourceField.set(sut, initDocumentDataResourceValue);

		Resource initCheckupReportDataResourcValue = new ClassPathResource(
				"file/Bundle-Bundle-eCheckupReport-Sample-01.json");
		Field initCheckupReportDataResourceField = sut.getClass().getDeclaredField("initCheckupReportDataResource");
		initCheckupReportDataResourceField.setAccessible(true);
		initCheckupReportDataResourceField.set(sut, initCheckupReportDataResourcValue);
		sut.init();
	}

	// データドリブンテスト
	@ParameterizedTest
	@MethodSource
	void testValidateDocument(String inputFilePath, String expectedResult, List<String> errorMessages)
			throws IOException {
		String jsonString = Files.readString(new ClassPathResource(inputFilePath).getFile().toPath());
		FhirValidationResult expected;
		if (FhirValidationResult.OK.equals(expectedResult)) {
			expected = FhirValidationResult.builder().result(expectedResult).build();
		} else {
			expected = FhirValidationResult.builder().result(expectedResult).details(errorMessages).build();
		}
		// FHIRバリデーション実行
		FhirValidationResult actual = sut.validateDocument(jsonString);
		// バリデーション結果の検証
		assertEquals(expected, actual);
	}

	// テストケース
	static Stream<Arguments> testValidateDocument() {
		return Stream.of(
				// テストケース1
				arguments("testdata/Bundle-BundleReferralExample01.json", FhirValidationResult.OK, null),
				// テストケース2
				arguments("testdata/Bundle-BundleReferralExample02.json", FhirValidationResult.NG, List.of(
						// org.hl7.fhir.validationのバージョンを6.1.4に変更することで、日本語対応されたので、エラーメッセージが変わり、エラーになる
						
						//"[ERROR]:[Bundle] Rule bdl-3: 'Entry.Requestバッチ/トランザクション/履歴に必須、それ以外の場合は禁止されています / entry.request mandatory for batch/transaction/history, otherwise prohibited' Failed",
						//"[ERROR]:[Bundle] Rule bdl-4: 'Batch-Response/Transaction-Response/historyに必須であり、それ以外の場合は禁止されています / entry.response mandatory for batch-response/transaction-response/history, otherwise prohibited' Failed",
						//"[ERROR]:[Bundle] Rule bdl-12: 'メッセージには最初のリソースとしてメッセージヘッダーが必要です / A message must have a MessageHeader as the first resource' Failed",
						//"[ERROR]:[Bundle] Bundle.type: minimum required = 1, but only found 0 (from http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral)"))
						"[ERROR]:[Bundle] ルール bdl-3: 'Entry.Requestバッチ/トランザクション/履歴に必須、それ以外の場合は禁止されています / entry.request mandatory for batch/transaction/history, otherwise prohibited' が失敗しました",
						"[ERROR]:[Bundle] ルール bdl-4: 'Batch-Response/Transaction-Response/historyに必須であり、それ以外の場合は禁止されています / entry.response mandatory for batch-response/transaction-response/history, otherwise prohibited' が失敗しました",
						"[ERROR]:[Bundle] ルール bdl-12: 'メッセージには最初のリソースとしてメッセージヘッダーが必要です / A message must have a MessageHeader as the first resource' が失敗しました",
						"[ERROR]:[Bundle] Bundle.type: 最小必要値 = 1、見つかった値 = 0 (from http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral)",

						// WARNINGのエラーメッセージが増えた
						"[WARNING]:[Bundle.entry[0].resource] ルール dom-6: '資源は堅牢な管理のために物語を持つべきである。' が失敗しました (Best Practice Recommendation)",
						"[WARNING]:[Bundle.entry[0].resource.entry[1].resource/*Patient/jppatientExample01Inline*/] ルール dom-6: 'リソースには、堅牢な管理のための叙述(Narative)が必要です / A resource should have narrative for robust management' が失敗しました (Best Practice Recommendation)", 
						"[WARNING]:[Bundle.entry[0].resource.entry[2].resource/*Encounter/encounterReferralExample01Inline*/] ルール dom-6: '資源は堅牢な管理のために物語を持つべきである。' が失敗しました (Best Practice Recommendation)", 
						"[WARNING]:[Bundle.entry[0].resource.entry[3].resource/*Practitioner/referralFromPractitionerExample01Inline*/] ルール dom-6: 'リソースには、堅牢な管理のための叙述(Narative)が必要です / A resource should have narrative for robust management' が失敗しました (Best Practice Recommendation)",
						"[WARNING]:[Bundle.entry[4].resource] ルール dom-6: 'リソースには、堅牢な管理のための叙述(Narative)が必要です / A resource should have narrative for robust management' が失敗しました (Best Practice Recommendation)",
						"[WARNING]:[Bundle.entry[0].resource.entry[5].resource/*Organization/referralFromOrganizationExample01Inline*/] ルール dom-6: '資源は堅牢な管理のために物語を持つべきである。' が失敗しました (Best Practice Recommendation)",
						"[WARNING]:[Bundle.entry[6].resource] ルール dom-6: '資源は堅牢な管理のために物語を持つべきである。' が失敗しました (Best Practice Recommendation)",
						"[WARNING]:[Bundle.entry[0].resource.entry[7].resource/*Encounter/purposeReferralExample01Inline*/] ルール dom-6: '資源は堅牢な管理のために物語を持つべきである。' が失敗しました (Best Practice Recommendation)",
						"[WARNING]:[Bundle.entry[0].resource.entry[8].resource/*Condition/cc1ReferralExample01Inline*/] ルール dom-6: '資源は堅牢な管理のために物語を持つべきである。' が失敗しました (Best Practice Recommendation)",
						"[WARNING]:[Bundle.entry[0].resource.entry[9].resource/*Condition/cc2ReferralExample01Inline*/] ルール dom-6: '資源は堅牢な管理のために物語を持つべきである。' が失敗しました (Best Practice Recommendation)",
						"[WARNING]:[Bundle.entry[0].resource.entry[10].resource/*Condition/piReferralExample01Inline*/] ルール dom-6: '資源は堅牢な管理のために物語を持つべきである。' が失敗しました (Best Practice Recommendation)",
						"[WARNING]:[Bundle.entry[0].resource.entry[11].resource/*Condition/ph1ReferralExample01Inline*/] ルール dom-6: '資源は堅牢な管理のために物語を持つべきである。' が失敗しました (Best Practice Recommendation)",
						"[WARNING]:[Bundle.entry[0].resource.entry[12].resource/*Condition/ph2ReferralExample01Inline*/] ルール dom-6: '資源は堅牢な管理のために物語を持つべきである。' が失敗しました (Best Practice Recommendation)",
						"[WARNING]:[Bundle.entry[0].resource.entry[13].resource/*AllergyIntolerance/alg1ReferralExample01Inline*/] ルール dom-6: 'リソースには、堅牢な管理のための叙述(Narative)が必要です / A resource should have narrative for robust management' が失敗しました (Best Practice Recommendation)",
						"[WARNING]:[Bundle.entry[0].resource.entry[14].resource/*AllergyIntolerance/alg1ReferralExample02Inline*/] ルール dom-6: 'リソースには、堅牢な管理のための叙述(Narative)が必要です / A resource should have narrative for robust management' が失敗しました (Best Practice Recommendation)",
						"[WARNING]:[Bundle.entry[0].resource.entry[15].resource/*Observation/psobsCommonExample01Inline*/] ルール dom-6: 'リソースには、堅牢な管理のための叙述(Narative)が必要です / A resource should have narrative for robust management' が失敗しました (Best Practice Recommendation)",
						"[WARNING]:[Bundle.entry[15].resource/*Observation/psobsCommonExample01Inline*/] すべてのObservationにはperformerが必要です",
						"[WARNING]:[Bundle.entry[15].resource/*Observation/psobsCommonExample01Inline*/] すべてのObservationにはeffectiveDateTimeまたはeffectivePeriodが必要です",
						"[WARNING]:[Bundle.entry[0].resource.entry[16].resource/*DocumentReference/ccourseReferralExample01Inline*/] ルール dom-6: 'リソースには、堅牢な管理のための叙述(Narative)が必要です / A resource should have narrative for robust management' が失敗しました (Best Practice Recommendation)"))				
				// TODO: 以降に、テストケースを追加していく
		);
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
				arguments("testdata/Bundle-Bundle-eCheckupReport-Sample-01.json", FhirValidationResult.OK, null)
		);

	}
}
