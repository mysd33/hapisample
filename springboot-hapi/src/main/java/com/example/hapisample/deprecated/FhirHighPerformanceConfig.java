package com.example.hapisample.deprecated;

import java.io.IOException;
import java.util.List;

import org.hl7.fhir.common.hapi.validation.support.CachingValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.CommonCodeSystemsTerminologyService;
import org.hl7.fhir.common.hapi.validation.support.InMemoryTerminologyServerValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.NpmPackageValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.PrePopulatedValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.SnapshotGeneratingValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.hl7.fhir.convertors.factory.VersionConvertorFactory_40_50;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.hapisample.Constants;
import com.example.hapisample.FhirConfigurationProperties;
import com.example.hapisample.domain.utl.LogUtils;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.context.support.IValidationSupport;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.IValidatorModule;
import lombok.extern.slf4j.Slf4j;

/**
 * FHIR Validationのパフォーマンス向上版のBean定義<br>
 * 
 * @deprecated 本モードは、FHIRバリデーションが動作しないケースが出たため、使用しないこととしましたので、削除予定です。
 */
@Deprecated(since = "0.0.1", forRemoval = true)
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "fhir", name = "high-performance-mode", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(FhirConfigurationProperties.class)
public class FhirHighPerformanceConfig {

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
		long startTime = System.nanoTime();
		// R5モデルで作成
		FhirContext ctx = FhirContext.forR5();
		long endTime = System.nanoTime();
		LogUtils.logElaspedTimeMillSecondUnit(log, "FHIRContext作成", startTime, endTime);
		return ctx;
	}
	
	/**
	 * JP-CLINS（(電子カルテ情報共有サービス2文書5情報+患者サマリー)用のFhirValidatorのBean定義<br>
	 * 
	 * FhirValidatorはスレッドセーフ（なお、個々のモジュールはスレッドセーフではない）なので、Bean定義してインスタンスを再利用できるようにする
	 * 
	 * @see https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/validation/FhirValidator.html
	 */
	@Bean
	FhirValidator fhirClinsValidator(FhirContext ctxForR5) throws IOException {
		long startTime = System.nanoTime();

		// R4のContextを一時的に作成
		FhirContext tmpCtxForR4 = FhirContext.forR4();

		// Validatorの作成
		// 臨床情報（JP-CLINS）のnpmパッケージファイルに基づくValidationSuportを追加
		NpmPackageValidationSupport npmPackageNewJPClinsSupport = new NpmPackageValidationSupport(tmpCtxForR4);
		// 新しいJP-CLINSは、snapshot形式にすると、エラーが発生するため、diff形式を使用
		npmPackageNewJPClinsSupport.loadPackageFromClasspath(Constants.JP_NEW_CLINS_NPM_PACKAGE);

		// JPCoreのnpmパッケージファイルに基づくValidationSuportを追加
		NpmPackageValidationSupport npmPackageJPCoreSupport = new NpmPackageValidationSupport(tmpCtxForR4);
		// JPCoreは、diff形式にすると、SnapshotGeneratingValidationSupportの処理で、OutOfMemoryエラーが発生する
		npmPackageJPCoreSupport.loadPackageFromClasspath(Constants.JP_CORE_NPM_PACKAGE);

		// JPCoreのTerminologyのnpmパッケージファイルに基づくValidationSuportを追加
		NpmPackageValidationSupport npmPackageTerminologySupport = new NpmPackageValidationSupport(tmpCtxForR4);
		npmPackageTerminologySupport.loadPackageFromClasspath(Constants.JP_FHIR_TERMINOLOGY_NPM_PACKAGE);

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
				toR5(ctxForR5, defaultProfileValidationSupport), //
				new CommonCodeSystemsTerminologyService(ctxForR5), //
				new InMemoryTerminologyServerValidationSupport(ctxForR5), //
				toR5(ctxForR5, npmPackageTerminologySupport), //
				toR5(ctxForR5, npmPackageJPCoreSupport), //
				toR5(ctxForR5, npmPackageNewJPClinsSupport), //
				
				// TODO:diff形式の場合にはSnapshotGeneratingValidationSupportを使用する必要があるが、
				// R4→R5事前変換では、エラーが出て動かない（SnapshotGeneratingValidationSupport自身もR4、R5を化かす必要がある）
				new SnapshotGeneratingValidationSupport(ctxForR5));
		// @formatter:off
		/*
		ValidationSupportChain validationSupportChain = new ValidationSupportChain(//
				// R5モデルに変換したValidationSupportを登録
				toR5(ctxForR5, npmPackageNewJPClinsSupport), //
				toR5(ctxForR5, npmPackageJPCoreSupport), //
				toR5(ctxForR5, npmPackageTerminologySupport), //
				toR5(ctxForR5, defaultProfileValidationSupport), //
				new CommonCodeSystemsTerminologyService(ctxForR5), //
				new InMemoryTerminologyServerValidationSupport(ctxForR5),//
				// diff形式の場合にはSnapshotGeneratingValidationSupportを使用する必要がある
				new SnapshotGeneratingValidationSupport(ctxForR5)
		);*/
		// @formatter:on
		// キャッシュ機能の設定
		CachingValidationSupport validationSupport = new CachingValidationSupport(validationSupportChain);
		FhirValidator validator = ctxForR5.newValidator();
		IValidatorModule module = new FhirInstanceValidator(validationSupport);
		validator.registerValidatorModule(module);
		long endTime = System.nanoTime();
		LogUtils.logElaspedTimeMillSecondUnit(log, "JP-CLINS FHIRValidator作成", startTime, endTime);
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
	FhirValidator fhirCheckupReportValidator(FhirContext ctxForR5) throws IOException {
		long startTime = System.nanoTime();

		// R4のContextを一時的に作成
		FhirContext tmpCtxForR4 = FhirContext.forR4();

		// Validatorの作成
		// 健康診断結果報告書のnpmパッケージファイルに基づくValidationSuportを追加
		NpmPackageValidationSupport npmPackageECheckupReportSupport = new NpmPackageValidationSupport(tmpCtxForR4);
		npmPackageECheckupReportSupport.loadPackageFromClasspath(Constants.JP_E_CHECKUP_REPORT_NPM_PACKAGE);

		// JPCoreのnpmパッケージファイルに基づくValidationSuportを追加
		NpmPackageValidationSupport npmPackageJPCoreSupport = new NpmPackageValidationSupport(tmpCtxForR4);
		npmPackageJPCoreSupport.loadPackageFromClasspath(Constants.JP_CORE_NPM_PACKAGE);

		// JPCoreのTerminologyのnpmパッケージファイルに基づくValidationSuportを追加
		NpmPackageValidationSupport npmPackageTerminologySupport = new NpmPackageValidationSupport(tmpCtxForR4);
		npmPackageTerminologySupport.loadPackageFromClasspath(Constants.JP_FHIR_TERMINOLOGY_NPM_PACKAGE);

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
				toR5(ctxForR5, defaultProfileValidationSupport), //
				new CommonCodeSystemsTerminologyService(ctxForR5), //
				new InMemoryTerminologyServerValidationSupport(ctxForR5), //
				toR5(ctxForR5, npmPackageTerminologySupport), //
				toR5(ctxForR5, npmPackageJPCoreSupport), //
				toR5(ctxForR5, npmPackageECheckupReportSupport));
		// @formatter:off
		/*
		ValidationSupportChain validationSupportChain = new ValidationSupportChain(//
				// R5モデルに変換したValidationSupportを登録
				toR5(ctxForR5, npmPackageECheckupReportSupport), //
				toR5(ctxForR5, npmPackageJPCoreSupport), //
				toR5(ctxForR5, npmPackageTerminologySupport), //
				toR5(ctxForR5, defaultProfileValidationSupport), //
				new CommonCodeSystemsTerminologyService(ctxForR5), //
				new InMemoryTerminologyServerValidationSupport(ctxForR5)//
		);*/
		// @formatter:on
		// キャッシュ機能の設定
		CachingValidationSupport validationSupport = new CachingValidationSupport(validationSupportChain);
		FhirValidator validator = ctxForR5.newValidator();
		IValidatorModule module = new FhirInstanceValidator(validationSupport);
		validator.registerValidatorModule(module);
		long endTime = System.nanoTime();
		LogUtils.logElaspedTimeMillSecondUnit(log, "健康診断結果報告書FHIRValidator作成", startTime, endTime);
		return validator;
	}

	/**
	 * R4モデルのValidationSupportをR5モデルのデータ構造のValidationSuportに変換する
	 * 
	 * @param ctxForR5               使用するR5のFhirContext
	 * @param validationSupportForR4 変換元のR4モデルのValidationSupport
	 * @return 変換後のR5モデルのValidationSupport
	 */
	@Deprecated(since = "0.0.1", forRemoval = true)
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
