package com.example.hapisample;

import java.io.IOException;
import java.util.List;

import org.hl7.fhir.common.hapi.validation.support.CachingValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.CommonCodeSystemsTerminologyService;
import org.hl7.fhir.common.hapi.validation.support.InMemoryTerminologyServerValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.NpmPackageValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.PrePopulatedValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.hl7.fhir.convertors.factory.VersionConvertorFactory_40_50;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.hapisample.domain.utl.LogUtils;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.context.support.IValidationSupport;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.IValidatorModule;
import lombok.extern.slf4j.Slf4j;

/**
 * FHIR Validationのパフォーマンス向上版のBean定義
 * 
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "fhir", name = "high-performance-mode", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(FhirConfigurationProperties.class)
public class FhirHighPerformanceConfig {
	private static final String JP_E_REFERRAL_NPM_PACKAGE = "classpath:package/jp-eReferral.r4-1.1.6-snap.tgz";
	private static final String JP_E_DISCHARGE_SUMMARY_NPM_PACKAGE = "classpath:package/jp-eDischargeSummary.r4-1.1.6-snap.tgz";
	private static final String JP_CORE_NPM_PACKAGE = "classpath:package/jp-core.r4-1.1.2-snap.tgz";
	private static final String JP_FHIR_TERMINOLOGY_NPM_PACKAGE = "classpath:package/jpfhir-terminology.r4-1.1.1.tgz";

	/**
	 * FhirContextのBean定義<br>
	 * 
	 * FhirContextはスレッドセーフであり、パフォーマンス上、解析またはエンコードが必要なすべてのリソースクラスをスキャンして、それらのクラスの内部モデルを構築するため、作成にコストがかかる。<br>
	 * （ただし、複数インスタンス作成しても問題はないが、処理毎に、新しいFhirContextを作成するとパフォーマンスが低下することがある）
	 * その理由から、アプリケーションの存続期間中FhirContextインスタンスを1つ作成し、そのインスタンスを再利用するようドキュメントで既定されているため、Bean定義してインスタンスを再利用できるようにする<br>
	 * 
	 * また、ハイパフォーマンス版Bean定義では、HAPIがR5モデルでのデータ構造を基本として実装されていることから、R4モデルでのプロファイルを使ったバリデーションは、互換性担保の作り上
	 * 都度、R5モデルのデータ構造に変換するオーバヘッドがあるため、事前にR5モデルに変換してから動作させるようにする。
	 * 
	 * @see https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/context/FhirContext.html
	 */
	@Bean
	FhirContext fhirContext(FhirConfigurationProperties fhirConfigurationProperties) {
		log.info("ハイパフォーマンスモード:{}", fhirConfigurationProperties.isHighPerformanceMode());
		log.info("FHIR性能向上版Bean生成");
		long startTime = System.currentTimeMillis();
		// R5モデルで作成
		FhirContext ctx = FhirContext.forR5();
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
	FhirValidator fhirValidator(FhirContext ctxForR5) throws IOException {
		long startTime = System.currentTimeMillis();

		// R4のContextを一時的に作成
		FhirContext tmpCtxForR4 = FhirContext.forR4();

		// Validatorの作成
		// 診療情報提供書のnpmパッケージファイルに基づくValidationSuportを追加
		NpmPackageValidationSupport npmPackageEReferralSupport = new NpmPackageValidationSupport(tmpCtxForR4);
		npmPackageEReferralSupport.loadPackageFromClasspath(JP_E_REFERRAL_NPM_PACKAGE);

		// 退院時サマリののnpmパッケージファイルに基づくValidationSuportを追加
		NpmPackageValidationSupport npmPackageDischargeSummarySupport = new NpmPackageValidationSupport(tmpCtxForR4);
		npmPackageDischargeSummarySupport.loadPackageFromClasspath(JP_E_DISCHARGE_SUMMARY_NPM_PACKAGE);

		// JPCoreのnpmパッケージファイルに基づくValidationSuportを追加
		NpmPackageValidationSupport npmPackageJPCoreSupport = new NpmPackageValidationSupport(tmpCtxForR4);
		npmPackageJPCoreSupport.loadPackageFromClasspath(JP_CORE_NPM_PACKAGE);

		// JPCoreのTerminologyのnpmパッケージファイルに基づくValidationSuportを追加
		NpmPackageValidationSupport npmPackageTerminologySupport = new NpmPackageValidationSupport(tmpCtxForR4);
		npmPackageTerminologySupport.loadPackageFromClasspath(JP_FHIR_TERMINOLOGY_NPM_PACKAGE);

		// FHIRベースプロファイルの組み込みの検証ルール
		DefaultProfileValidationSupport defaultProfileValidationSupport = new DefaultProfileValidationSupport(
				tmpCtxForR4);
		// R5変換前に、引数はダミーのURLでよいので、StructureDefinition, ValueSet、CodeSystemsをロードするようにする
		// 参考
		// ca.uhn.fhir.context.support.DefaultProfileValidationSupportBundleStrategy.fetchAllStructureDefinitions()メソッド
		// ca.uhn.fhir.context.support.DefaultProfileValidationSupportBundleStrategy.fetchCodeSystemOrValueSet(String,
		// boolean)メソッド
		defaultProfileValidationSupport.flush();
		defaultProfileValidationSupport.fetchAllStructureDefinitions();
		defaultProfileValidationSupport.fetchValueSet("http://dummy");

		ValidationSupportChain validationSupportChain = new ValidationSupportChain(//
				// R5モデルに変換したValidationSupportを登録
				toR5(ctxForR5, npmPackageEReferralSupport), //
				toR5(ctxForR5, npmPackageDischargeSummarySupport), //
				toR5(ctxForR5, npmPackageJPCoreSupport), //
				toR5(ctxForR5, npmPackageTerminologySupport), //
				toR5(ctxForR5, defaultProfileValidationSupport), //
				new CommonCodeSystemsTerminologyService(ctxForR5), //
				new InMemoryTerminologyServerValidationSupport(ctxForR5)//
		);
		// キャッシュ機能の設定
		CachingValidationSupport validationSupport = new CachingValidationSupport(validationSupportChain);
		FhirValidator validator = ctxForR5.newValidator();
		IValidatorModule module = new FhirInstanceValidator(validationSupport);
		validator.registerValidatorModule(module);
		long endTime = System.currentTimeMillis();
		LogUtils.logElaspedTime(log, "FHIRValidator作成", startTime, endTime);
		return validator;
	}

	/**
	 * R4モデルのValidationSupportをR5モデルのデータ構造のValidationSuportに変換する
	 * 
	 * @param ctxForR5               使用するR5のFhirContext
	 * @param validationSupportForR4 変換元のR4モデルのValidationSupport
	 * @return 変換後のR5モデルのValidationSupport
	 */
	private PrePopulatedValidationSupport toR5(FhirContext ctxForR5, IValidationSupport validationSupportForR4) {
		PrePopulatedValidationSupport validationSupportForR5 = new PrePopulatedValidationSupport(ctxForR5);
		// HL7 FHIR Converterを使って変換しデータコピー
		// https://hapifhir.io/hapi-fhir/docs/model/converter.html
		for (IBaseResource resourceForR4 : validationSupportForR4.fetchAllConformanceResources()) {
			validationSupportForR5.addResource(
					VersionConvertorFactory_40_50.convertResource((org.hl7.fhir.r4.model.Resource) resourceForR4));
		}
		List<IBaseResource> searchParameters = validationSupportForR4.fetchAllSearchParameters();
		if (searchParameters != null) {
			for (IBaseResource searchParameter : searchParameters) {
				validationSupportForR5.addResource(VersionConvertorFactory_40_50
						.convertResource((org.hl7.fhir.r4.model.Resource) searchParameter));
			}
		}

		return validationSupportForR5;
	}

}
