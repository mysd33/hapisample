package com.example.hapisample;

import java.io.IOException;

import org.hl7.fhir.common.hapi.validation.support.CachingValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.CommonCodeSystemsTerminologyService;
import org.hl7.fhir.common.hapi.validation.support.InMemoryTerminologyServerValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.NpmPackageValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.SnapshotGeneratingValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.hapisample.domain.utl.LogUtils;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.IValidatorModule;
import lombok.extern.slf4j.Slf4j;

/**
 * FHIR ValidationのデフォルトのBean定義
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "fhir", name = "high-performance-mode", havingValue = "false", matchIfMissing = true)
@EnableConfigurationProperties(FhirConfigurationProperties.class)
public class FhirConfig {
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
		long startTime = System.nanoTime();
		// R4モデルで作成
		FhirContext ctx = FhirContext.forR4();
		long endTime = System.nanoTime();
		LogUtils.logElaspedTimeMillSecondUnit(log, "FHIRContext作成", startTime, endTime);
		return ctx;
	}
	
	/**
	 * 新JP-CLINS（(電子カルテ情報共有サービス2文書5情報+患者サマリー)用のFhirValidatorのBean定義<br>
	 * 
	 * FhirValidatorはスレッドセーフ（なお、個々のモジュールはスレッドセーフではない）なので、Bean定義してインスタンスを再利用できるようにする
	 * 
	 * @see https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/validation/FhirValidator.html
	 */
	@Bean
	FhirValidator fhirNewClinsValidator(FhirContext ctx) throws IOException {
		long startTime = System.nanoTime();
		// Validatorの作成
		// JP-CLINSのnpmパッケージファイルに基づくValidationSuportを追加
		NpmPackageValidationSupport npmPackageNewJPClinsSupport = new NpmPackageValidationSupport(ctx);
		npmPackageNewJPClinsSupport.loadPackageFromClasspath(Constants.JP_NEW_CLINS_NPM_PACKAGE);

		// JPCoreのnpmパッケージファイルに基づくValidationSuportを追加
		NpmPackageValidationSupport npmPackageJPCoreSupport = new NpmPackageValidationSupport(ctx);
		npmPackageJPCoreSupport.loadPackageFromClasspath(Constants.JP_CORE_NPM_PACKAGE);

		// JPCoreのTerminologyのnpmパッケージファイルに基づくValidationSuportを追加
		NpmPackageValidationSupport npmPackageTerminologySupport = new NpmPackageValidationSupport(ctx);
		npmPackageTerminologySupport.loadPackageFromClasspath(Constants.JP_FHIR_TERMINOLOGY_NPM_PACKAGE);
		ValidationSupportChain validationSupportChain = new ValidationSupportChain(//
				// FHIRベースプロファイルの組み込みの検証ルール
				new DefaultProfileValidationSupport(ctx), //
				new CommonCodeSystemsTerminologyService(ctx), //
				new InMemoryTerminologyServerValidationSupport(ctx), //
				npmPackageTerminologySupport, //
				npmPackageJPCoreSupport, //
				npmPackageNewJPClinsSupport,
				new SnapshotGeneratingValidationSupport(ctx));				
		// @formatter:off
		/*
		ValidationSupportChain validationSupportChain = new ValidationSupportChain(//
				npmPackageNewJPClinsSupport, //
				npmPackageJPCoreSupport, //
				npmPackageTerminologySupport,
				// FHIRベースプロファイルの組み込みの検証ルール
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
		long endTime = System.nanoTime();
		LogUtils.logElaspedTimeMillSecondUnit(log, "新JP-CLINS FHIRValidator作成", startTime, endTime);
		return validator;
	}	

	/**
	 * 健康診断結果報告書用のFhirValidatorのBean定義<br>
	 * 
	 * 医療文書（診療情報提供書、退院時サマリ）のプロファイルと一緒に１つのバリデータで定義してしまうと、
	 * 本来は正常のバリデーション結果がエラーに変わってしまうため、干渉しないよう別バリデータとして定義<br>
	 * 
	 * FhirValidatorはスレッドセーフ（なお、個々のモジュールはスレッドセーフではない）なので、Bean定義してインスタンスを再利用できるようにする
	 * 
	 * @see https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/validation/FhirValidator.html
	 */
	@Bean
	FhirValidator fhirCheckupReportValidator(FhirContext ctx) throws IOException {
		long startTime = System.nanoTime();
		// Validatorの作成
		// 健康診断結果報告書のnpmパッケージファイルに基づくValidationSuportを追加
		NpmPackageValidationSupport npmPackageECheckupReportSupport = new NpmPackageValidationSupport(ctx);
		npmPackageECheckupReportSupport.loadPackageFromClasspath(Constants.JP_E_CHECKUP_REPORT_NPM_PACKAGE);

		// JPCoreのnpmパッケージファイルに基づくValidationSuportを追加
		NpmPackageValidationSupport npmPackageJPCoreSupport = new NpmPackageValidationSupport(ctx);
		npmPackageJPCoreSupport.loadPackageFromClasspath(Constants.JP_CORE_NPM_PACKAGE);

		// JPCoreのTerminologyのnpmパッケージファイルに基づくValidationSuportを追加
		NpmPackageValidationSupport npmPackageTerminologySupport = new NpmPackageValidationSupport(ctx);
		npmPackageTerminologySupport.loadPackageFromClasspath(Constants.JP_FHIR_TERMINOLOGY_NPM_PACKAGE);
		ValidationSupportChain validationSupportChain = new ValidationSupportChain(//
				// FHIRベースプロファイルの組み込みの検証ルール
				new DefaultProfileValidationSupport(ctx), //
				new CommonCodeSystemsTerminologyService(ctx), //
				new InMemoryTerminologyServerValidationSupport(ctx), //
				npmPackageTerminologySupport, //
				npmPackageJPCoreSupport, //
				npmPackageECheckupReportSupport);				
		// @formatter:off
		/*
		ValidationSupportChain validationSupportChain = new ValidationSupportChain(//
				npmPackageECheckupReportSupport, //
				npmPackageJPCoreSupport, //
				npmPackageTerminologySupport,
				// FHIRベースプロファイルの組み込みの検証ルール
				new DefaultProfileValidationSupport(ctx), //
				new CommonCodeSystemsTerminologyService(ctx), //
				new InMemoryTerminologyServerValidationSupport(ctx)//
		);*/
		// @formatter:on
		// キャッシュ機能の設定
		CachingValidationSupport validationSupport = new CachingValidationSupport(validationSupportChain);
		FhirValidator validator = ctx.newValidator();
		IValidatorModule module = new FhirInstanceValidator(validationSupport);
		validator.registerValidatorModule(module);
		long endTime = System.nanoTime();
		LogUtils.logElaspedTimeMillSecondUnit(log, "健康診断結果報告書FHIRValidator作成", startTime, endTime);
		return validator;
	}
	
	
	/**
	 * 医療文書（診療情報提供書、退院時サマリ）用のFhirValidatorのBean定義<br>
	 * 
	 * FhirValidatorはスレッドセーフ（なお、個々のモジュールはスレッドセーフではない）なので、Bean定義してインスタンスを再利用できるようにする
	 * 
	 * @see https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/validation/FhirValidator.html
	 */
	@Bean
	@Deprecated(since = "0.0.1", forRemoval = true)
	FhirValidator fhirDocumentValidator(FhirContext ctx) throws IOException {
		long startTime = System.nanoTime();
		// Validatorの作成
		// 診療情報提供書のnpmパッケージファイルに基づくValidationSuportを追加
		NpmPackageValidationSupport npmPackageEReferralSupport = new NpmPackageValidationSupport(ctx);
		npmPackageEReferralSupport.loadPackageFromClasspath(Constants.JP_E_REFERRAL_NPM_PACKAGE);

		// 退院時サマリののnpmパッケージファイルに基づくValidationSuportを追加
		NpmPackageValidationSupport npmPackageEDischargeSummarySupport = new NpmPackageValidationSupport(ctx);
		npmPackageEDischargeSummarySupport.loadPackageFromClasspath(Constants.JP_E_DISCHARGE_SUMMARY_NPM_PACKAGE);

		// JPCoreのnpmパッケージファイルに基づくValidationSuportを追加
		NpmPackageValidationSupport npmPackageJPCoreSupport = new NpmPackageValidationSupport(ctx);
		npmPackageJPCoreSupport.loadPackageFromClasspath(Constants.JP_CORE_NPM_PACKAGE);

		// JPCoreのTerminologyのnpmパッケージファイルに基づくValidationSuportを追加
		NpmPackageValidationSupport npmPackageTerminologySupport = new NpmPackageValidationSupport(ctx);
		npmPackageTerminologySupport.loadPackageFromClasspath(Constants.JP_FHIR_TERMINOLOGY_NPM_PACKAGE);
		ValidationSupportChain validationSupportChain = new ValidationSupportChain(//
				// FHIRベースプロファイルの組み込みの検証ルール
				new DefaultProfileValidationSupport(ctx), //
				new CommonCodeSystemsTerminologyService(ctx), //
				new InMemoryTerminologyServerValidationSupport(ctx), //
				npmPackageTerminologySupport, //
				npmPackageJPCoreSupport, //
				npmPackageEReferralSupport, //
				npmPackageEDischargeSummarySupport);
		// @formatter:off
		/*
		ValidationSupportChain validationSupportChain = new ValidationSupportChain(//
				npmPackageEReferralSupport, //
				npmPackageEDischargeSummarySupport, //
				npmPackageJPCoreSupport, //
				npmPackageTerminologySupport,
				// FHIRベースプロファイルの組み込みの検証ルール
				new DefaultProfileValidationSupport(ctx), //
				new CommonCodeSystemsTerminologyService(ctx), //
				new InMemoryTerminologyServerValidationSupport(ctx)//
		);*/
		// @formatter:on
		// キャッシュ機能の設定
		CachingValidationSupport validationSupport = new CachingValidationSupport(validationSupportChain);
		FhirValidator validator = ctx.newValidator();
		IValidatorModule module = new FhirInstanceValidator(validationSupport);
		validator.registerValidatorModule(module);
		long endTime = System.nanoTime();
		LogUtils.logElaspedTimeMillSecondUnit(log, "医療文書FHIRValidator作成", startTime, endTime);
		return validator;
	}
	
	/**
	 * 臨床情報（JP-CLINS）用のFhirValidatorのBean定義<br>
	 * 
	 * FhirValidatorはスレッドセーフ（なお、個々のモジュールはスレッドセーフではない）なので、Bean定義してインスタンスを再利用できるようにする
	 * 
	 * @see https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/validation/FhirValidator.html
	 */
	@Bean
	@Deprecated(since = "0.0.1", forRemoval = true)
	FhirValidator fhirClinsValidator(FhirContext ctx) throws IOException {
		long startTime = System.nanoTime();
		// Validatorの作成
		// 臨床情報（JP-CLINS）のnpmパッケージファイルに基づくValidationSuportを追加
		NpmPackageValidationSupport npmPackageJPClinsSupport = new NpmPackageValidationSupport(ctx);
		npmPackageJPClinsSupport.loadPackageFromClasspath(Constants.JP_CLINS_NPM_PACKAGE);

		// JPCoreのnpmパッケージファイルに基づくValidationSuportを追加
		NpmPackageValidationSupport npmPackageJPCoreSupport = new NpmPackageValidationSupport(ctx);
		npmPackageJPCoreSupport.loadPackageFromClasspath(Constants.JP_CORE_NPM_PACKAGE);

		// JPCoreのTerminologyのnpmパッケージファイルに基づくValidationSuportを追加
		NpmPackageValidationSupport npmPackageTerminologySupport = new NpmPackageValidationSupport(ctx);
		npmPackageTerminologySupport.loadPackageFromClasspath(Constants.JP_FHIR_TERMINOLOGY_NPM_PACKAGE);
		ValidationSupportChain validationSupportChain = new ValidationSupportChain(//
				// FHIRベースプロファイルの組み込みの検証ルール
				new DefaultProfileValidationSupport(ctx), //
				new CommonCodeSystemsTerminologyService(ctx), //
				new InMemoryTerminologyServerValidationSupport(ctx), //
				npmPackageTerminologySupport, //
				npmPackageJPCoreSupport, //
				npmPackageJPClinsSupport);				
		// @formatter:off
		/*
		ValidationSupportChain validationSupportChain = new ValidationSupportChain(//
				npmPackageJPClinsSupport, //
				npmPackageJPCoreSupport, //
				npmPackageTerminologySupport,
				// FHIRベースプロファイルの組み込みの検証ルール
				new DefaultProfileValidationSupport(ctx), //
				new CommonCodeSystemsTerminologyService(ctx), //
				new InMemoryTerminologyServerValidationSupport(ctx)//
		);*/
		// @formatter:on
		// キャッシュ機能の設定
		CachingValidationSupport validationSupport = new CachingValidationSupport(validationSupportChain);
		FhirValidator validator = ctx.newValidator();
		IValidatorModule module = new FhirInstanceValidator(validationSupport);
		validator.registerValidatorModule(module);
		long endTime = System.nanoTime();
		LogUtils.logElaspedTimeMillSecondUnit(log, "臨床情報FHIRValidator作成", startTime, endTime);
		return validator;
	}	



}
