package com.example.hapisample;

import java.io.IOException;

import org.hl7.fhir.common.hapi.validation.support.CachingValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.CommonCodeSystemsTerminologyService;
import org.hl7.fhir.common.hapi.validation.support.InMemoryTerminologyServerValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.NpmPackageValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.hapisample.domain.utl.LogUtils;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.IValidatorModule;
import lombok.extern.slf4j.Slf4j;

/**
 * FHIR ValidationのBean定義
 */
@Slf4j
@Configuration
public class FhirConfig {
	private static final String JP_E_REFERRAL_NPM_PACKAGE = "classpath:package/jp-eReferral.r4-1.1.6-snap.tgz";
	private static final String JP_E_DISCHARGE_SUMMARY_NPM_PACKAGE = "classpath:package/jp-eDischargeSummary.r4-1.1.6-snap.tgz";
	private static final String JP_CORE_NPM_PACKAGE = "classpath:package/jp-core.r4-1.1.2-snap.tgz";
	private static final String JP_FHIR_TERMINOLOGY_NPM_PACKAGE = "classpath:package/jpfhir-terminology.r4-1.1.1.tgz";

	/**
	 * FhirContextのBean定義<br>
	 * 
	 * FhirContextはスレッドセーフであり、パフォーマンス上、解析またはエンコードが必要なすべてのリソースクラスをスキャンして、それらのクラスの内部モデルを構築するため、作成にコストがかかる。<br>
	 * （ただし、複数インスタンス作成しても問題はないが、処理毎に、新しいFhirContextを作成するとパフォーマンスが低下することがある）
	 * その理由から、アプリケーションの存続期間中FhirContextインスタンスを1つ作成し、そのインスタンスを再利用するようドキュメントで既定されているため、Bean定義してインスタンスを再利用できるようにする
	 * 
	 * @see https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/context/FhirContext.html
	 */
	@Bean
	FhirContext fhirContext() {
		long startTime = System.currentTimeMillis();
		// R4モデルで作成
		FhirContext ctx = FhirContext.forR4();
		long endTime = System.currentTimeMillis();
		LogUtils.logElaspedTime(log, "FHIRContext作成", startTime, endTime);
		return ctx;
	}

	/**
	 * FhirValidatorのBean定義<br>
	 * 
	 * FhirValidatorはスレッドセーフ（なお、個々のモジュールはスレッドセーフではない）なので、Bean定義してインスタンスを再利用できるようにする
	 * 
	 * @see https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/validation/FhirValidator.html
	 */
	@Bean
	FhirValidator fhirValidator(FhirContext ctx) throws IOException {
		long startTime = System.currentTimeMillis();
		// Validatorの作成
		// 診療情報提供書のnpmパッケージファイルに基づくValidationSuportを追加
		NpmPackageValidationSupport npmPackageEReferralSupport = new NpmPackageValidationSupport(ctx);
		npmPackageEReferralSupport.loadPackageFromClasspath(JP_E_REFERRAL_NPM_PACKAGE);
		
		// 退院時サマリののnpmパッケージファイルに基づくValidationSuportを追加
		NpmPackageValidationSupport npmPackageDischargeSummarySupport = new NpmPackageValidationSupport(ctx);
		npmPackageDischargeSummarySupport.loadPackageFromClasspath(JP_E_DISCHARGE_SUMMARY_NPM_PACKAGE);

		// JPCoreのnpmパッケージファイルに基づくValidationSuportを追加
		NpmPackageValidationSupport npmPackageJPCoreSupport = new NpmPackageValidationSupport(ctx);
		npmPackageJPCoreSupport.loadPackageFromClasspath(JP_CORE_NPM_PACKAGE);

		// JPCoreのTerminologyのnpmパッケージファイルに基づくValidationSuportを追加
		NpmPackageValidationSupport npmPackageTerminologySupport = new NpmPackageValidationSupport(ctx);
		npmPackageTerminologySupport.loadPackageFromClasspath(JP_FHIR_TERMINOLOGY_NPM_PACKAGE);

		ValidationSupportChain validationSupportChain = new ValidationSupportChain(//
				npmPackageEReferralSupport, //
				npmPackageDischargeSummarySupport, //
				npmPackageJPCoreSupport, //
				npmPackageTerminologySupport,
				// FHIRベースプロファイルの組み込みの検証ルール
				new DefaultProfileValidationSupport(ctx), //
				new CommonCodeSystemsTerminologyService(ctx), //
				new InMemoryTerminologyServerValidationSupport(ctx)//
		);
		// キャッシュ機能の設定
		CachingValidationSupport validationSupport = new CachingValidationSupport(validationSupportChain);
		FhirValidator validator = ctx.newValidator();
		IValidatorModule module = new FhirInstanceValidator(validationSupport);
		validator.registerValidatorModule(module);
		long endTime = System.currentTimeMillis();
		LogUtils.logElaspedTime(log, "FHIRValidator作成", startTime, endTime);
		return validator;
	}

}
