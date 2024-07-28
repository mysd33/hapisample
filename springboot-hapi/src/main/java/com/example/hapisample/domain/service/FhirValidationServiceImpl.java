package com.example.hapisample.domain.service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.example.hapisample.domain.utl.LogUtils;
import com.example.hapisample.domain.vo.FhirValidationResult;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * FHIRバリデーションを実施するFhirValidationService実装クラス
 */
@Slf4j
@Service
public class FhirValidationServiceImpl implements FhirValidationService {
	private final FhirContext fhirContext;
	private final FhirValidator checkupReportValidator;
	private final FhirValidator clinsValidator;

	@Value("classpath:file/Bundle-Bundle-CLINS-Referral-Example-01.json")
	private Resource initClinsDataResource;

	@Value("classpath:file/Bundle-Bundle-eCheckupReport-Sample-01.json")
	private Resource initCheckupReportDataResource;

	/**
	 * コンストラクタ
	 * 
	 * @param fhirContext            FHIRコンテキスト
	 * @param clinsValidator         JP-CLINS用バリデータ
	 * @param checkupReportValidator 健康診断結果報告書用バリデータ
	 */
	public FhirValidationServiceImpl(FhirContext fhirContext,
			@Qualifier("fhirClinsValidator") FhirValidator clinsValidator,
			@Qualifier("fhirCheckupReportValidator") FhirValidator checkupReportValidator) {
		this.fhirContext = fhirContext;		
		this.checkupReportValidator = checkupReportValidator;
		this.clinsValidator = clinsValidator;
	}

	/**
	 * FHIRバリデーションの初回実行には時間がかかるため、AP起動時にダミーデータでバリデーションの初回実行をしておく
	 * 
	 * @throws IOException
	 */
	@PostConstruct
	public void init() throws IOException {
		// JP-CLINS用Validator初回実行
		initValidator(clinsValidator, initClinsDataResource);

		// 健康診断結果報告書用Validator初回実行
		initValidator(checkupReportValidator, initCheckupReportDataResource);
	}

	@Override
	public FhirValidationResult validateClins(String fhirString) {
		return doValidate(clinsValidator, fhirString);
	}

	@Override
	public FhirValidationResult validateCheckupReport(String fhirString) {
		return doValidate(checkupReportValidator, fhirString);
	}

	@Override
	@Deprecated(since = "0.0.1", forRemoval = true)
	public FhirValidationResult validateDocument(String fhirString) {
		return doValidate(clinsValidator, fhirString);
	}

	private void initValidator(FhirValidator validator, Resource initDocumentDataResource) throws IOException {
		log.debug("バリデーション暖機処理実行開始");
		String fhirDocumentString = Files.readString(initDocumentDataResource.getFile().toPath());
		long startTime = System.nanoTime();
		validator.validateWithResult(fhirDocumentString);
		long endTime = System.nanoTime();
		LogUtils.logElaspedTimeMillSecondUnit(log, "バリデーション暖機処理実行完了", startTime, endTime);
	}

	private FhirValidationResult doValidate(FhirValidator validator, String fhirString) {
		log.debug("FHIRバリデーション開始[FHIRバージョン {}]", fhirContext.getVersion().getVersion().getFhirVersionString());
		// log.debug("バリデーション対象データ:{}", fhirString);
		// FHIRバリデーションの実行
		long startTime = System.nanoTime();
		ValidationResult validationResult = validator.validateWithResult(fhirString);
		long endTime = System.nanoTime();
		LogUtils.logElaspedTimeMillSecondUnit(log, "バリデーション実行完了", startTime, endTime);
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
