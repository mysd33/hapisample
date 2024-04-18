package com.example.hapisample.domain;

import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.SingleValidationMessage;
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
	private static final String OK = "OK";
	private final FhirContext fhirContext;
	private final FhirValidator validator;

	@Value("classpath:file/Bundle-BundleReferralExample01.json")
	private Resource resource;

	/**
	 * FHIRバリデーションの初回実行には時間がかかるため、AP起動時にダミーデータでバリデーションの初回実行をしておく
	 * @throws IOException
	 */
	@PostConstruct
	void init() throws IOException {
		String fhirString = Files.readString(resource.getFile().toPath());
		log.debug("バリデーション暖機処理実行開始");		
		long startTime = System.currentTimeMillis();
		validator.validateWithResult(fhirString);
		long endTime = System.currentTimeMillis();
		logElaspedTime("バリデーション暖機処理実行完了", startTime, endTime);
	}

	@Override
	public String validate(String fhirString) {
		log.debug("FHIRバリデーション開始[FHIRバージョン {}]", fhirContext.getVersion().getVersion().getFhirVersionString());
		//log.debug("バリデーション対象データ:{}", fhirString);
		// FHIRバリデーションの実行
		long startTime = System.currentTimeMillis();
		ValidationResult validationResult = validator.validateWithResult(fhirString);
		long endTime = System.currentTimeMillis();
		logElaspedTime("バリデーション実行完了", startTime, endTime);
		// バリデーション結果が正常の場合
		if (validationResult.isSuccessful()) {
			log.info("ドキュメントは有効です");
			return OK;
		}
		// バリデーション結果がエラーの場合
		log.warn("ドキュメントに不備があります");
		// 検証結果の出力
		StringBuilder sb = new StringBuilder();
		// バリデーションエラーメッセージの取得
		for (SingleValidationMessage validationMessage : validationResult.getMessages()) {
			String messsage = String.format("[%s]:[%s] %s", validationMessage.getSeverity().toString(),
					validationMessage.getLocationString(), validationMessage.getMessage());
			log.warn(messsage);
			sb.append(messsage).append(System.lineSeparator());
		}
		return sb.toString();
	}
	
	private static void logElaspedTime(String label, long startTime, long endTime) {
		log.debug("{}：{}ms", label, endTime - startTime);
	}
}
