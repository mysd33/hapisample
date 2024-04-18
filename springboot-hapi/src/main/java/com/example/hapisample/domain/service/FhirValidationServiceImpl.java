package com.example.hapisample.domain.service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.example.hapisample.domain.utl.LogUtils;
import com.example.hapisample.domain.vo.FhirValidationResult;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * FHIRバリデーションを実施するFhirValidationService実装クラス
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FhirValidationServiceImpl implements FhirValidationService {
	private final FhirContext fhirContext;
	private final FhirValidator validator;

	@Value("classpath:file/Bundle-BundleReferralExample01.json")
	private Resource resource;

	/**
	 * FHIRバリデーションの初回実行には時間がかかるため、AP起動時にダミーデータでバリデーションの初回実行をしておく
	 * 
	 * @throws IOException
	 */
	@PostConstruct
	void init() throws IOException {
		String fhirString = Files.readString(resource.getFile().toPath());
		log.debug("バリデーション暖機処理実行開始");
		long startTime = System.currentTimeMillis();
		validator.validateWithResult(fhirString);
		long endTime = System.currentTimeMillis();
		LogUtils.logElaspedTime(log, "バリデーション暖機処理実行完了", startTime, endTime);
	}

	@Override
	public FhirValidationResult validate(String fhirString) {
		log.debug("FHIRバリデーション開始[FHIRバージョン {}]", fhirContext.getVersion().getVersion().getFhirVersionString());
		// log.debug("バリデーション対象データ:{}", fhirString);
		// FHIRバリデーションの実行
		long startTime = System.currentTimeMillis();
		ValidationResult validationResult = validator.validateWithResult(fhirString);
		long endTime = System.currentTimeMillis();
		LogUtils.logElaspedTime(log, "バリデーション実行完了", startTime, endTime);
		// バリデーション結果が正常の場合
		if (validationResult.isSuccessful()) {
			log.info("ドキュメントは有効です");
			return FhirValidationResult.builder().result(FhirValidationResult.OK).build();
		}
		// バリデーション結果がエラーの場合
		log.warn("ドキュメントに不備があります");
		// 検証結果の出力
		List<String> messages = new ArrayList<>();
		// バリデーションエラーメッセージの取得
		validationResult.getMessages().forEach(validationMessage -> {
			String messsage = String.format("[%s]:[%s] %s", validationMessage.getSeverity().toString(),
					validationMessage.getLocationString(), validationMessage.getMessage());
			log.warn(messsage);
			messages.add(messsage);
		});
		return FhirValidationResult.builder()//
				.result(FhirValidationResult.NG)//
				.details(messages).build();
	}

}
