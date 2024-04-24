package com.example.hapisample;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.example.hapisample.domain.service.FhirValidationServiceImpl;

import ca.uhn.fhir.context.FhirContext;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import lombok.extern.slf4j.Slf4j;

/**
 * FhirValidationのパフォーマンステストコードの例<br>
 * 
 * HAPIの通常の実装版と、性能改善版（R4→R5事前変換）の比較ができるようになっている。
 */
@Slf4j
class FhirValidationPerformanceTest {
	// 測定時の試行回数
	private static final int ATTEMPT_COUNT = 10;
	// 暖機処理用のFHIRのデータファイル
	private static final String INIT_FOR_FHIR_DATA_FILE_PATH = "file/Bundle-BundleReferralExample01.json";
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
		defaultSut = new FhirValidationServiceImpl(ctx, fhirConfig.fhirValidator(ctx));
		initValidator(defaultSut);
	}

	// 性能改善版のFHIR Validation機能の初期化
	private static void initHighPerformanceSut() throws IOException, NoSuchFieldException, IllegalAccessException {
		FhirHighPerformanceConfig fhirConfig = new FhirHighPerformanceConfig();
		FhirContext ctx = fhirConfig.fhirContext();
		highPerformanceSut = new FhirValidationServiceImpl(ctx, fhirConfig.fhirValidator(ctx));
		initValidator(highPerformanceSut);
	}

	// 暖機処理
	private static void initValidator(FhirValidationServiceImpl service) throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException, IOException {
		Resource initDataResourceValue = new ClassPathResource(INIT_FOR_FHIR_DATA_FILE_PATH);
		Field initDataResourceField = service.getClass().getDeclaredField("initDataResource");
		initDataResourceField.setAccessible(true);
		initDataResourceField.set(service, initDataResourceValue);
		service.init();
	}

	// 性能比較
	@Test
	void testValidate() throws IOException {
		// TODO: 性能テスト用のテストデータは、現状、暖機処理用と同じデータを使っているが、実際に試したデータファイルのパスに変えるとよい。
		String inputFilePath = INIT_FOR_FHIR_DATA_FILE_PATH;
		String jsonString = Files.readString(new ClassPathResource(inputFilePath).getFile().toPath());

		// 通常のFHIR Validation機能の速度測定
		List<Long> duraionsForDefault = new ArrayList<>();
		for (int i = 0; i < ATTEMPT_COUNT; i++) {
			long start = System.nanoTime();
			// FHIRバリデーション実行
			defaultSut.validate(jsonString);
			long duration = System.nanoTime() - start;
			duraionsForDefault.add(duration);
		}

		// 性能改善版のFHIR Validation機能の速度測定
		List<Long> durationsForHighPerformance = new ArrayList<>();
		for (int i = 0; i < ATTEMPT_COUNT; i++) {
			long start = System.nanoTime();
			// FHIRバリデーション実行
			highPerformanceSut.validate(jsonString);
			long duration = System.nanoTime() - start;
			durationsForHighPerformance.add(duration);
		}
		log.info("試行回数:{}回", ATTEMPT_COUNT);
		log.info("通常版のFHIR Validation機能の平均処理時間:{}ms", //
				TimeUnit.NANOSECONDS
						.toMillis((long) duraionsForDefault.stream().mapToLong(d -> d).average().getAsDouble()));
		log.info("性能改善版のFHIR Validation機能の平均処理時間:{}ms", //
				TimeUnit.NANOSECONDS.toMillis(
						(long) durationsForHighPerformance.stream().mapToLong(d -> d).average().getAsDouble()));
	}

}
