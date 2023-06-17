# HAPI FHIRのサンプル

- [HAPI FHIR](https://hapifhir.io/)を使って、[診療情報提供書HL7FHIR記述仕様](https://std.jpfhir.jp/)に基づくサンプルデータ（Bundle-BundleReferralExample01.json）に対して検証し、Bundleリソースとしてパースするサンプルプログラムです。

- また、FHIRリソース(Bundle)として作成したオブジェクトを、FHIRのJSON文字列で出力（シリアライズ）するサンプルプログラムもあります。
    - 処方情報のFHIR記述仕様書(https://jpfhir.jp/fhir/ePrescriptionData/igv1/)に従い、JSONのほんの一部分を生成しています。

## プロファイルの検証（バリデーション）とパース
- FHIRプロファイルでの検証
    - [HAPI FHIRのバリデータの機能](https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html)を使用して、検証しています。
    - デフォルトの組み込みのバリデータである[DefaultProfileValidationSupport](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html#defaultprofilevalidationsupport)クラスにより、検証できます。

- JPCoreプロファイル、文書情報プロファイルでの検証
    - [JPCoreプロファイル](https://jpfhir.jp/fhir/core/)のサイトにJPCoreプロファイルの構造定義ファイルがあります。
    - また、[https://std.jpfhir.jp/](https://std.jpfhir.jp/)のサイトに、JPCoreを含むスナップショット形式の[診療情報提供書の文書情報プロファイル（IGpackage2023.4.27 snapshot形式: jp-ereferral-0.9.6-snap.tgz）](https://jpfhir.jp/fhir/eReferral/jp-ereferral-0.9.7-snap.tgz)があります。
        - 現状、2023/06/10時点で、Webページの表記は「0.9.6」となっていますが、ダウンロードリンク先のファイルの実際のバージョン番号は「0.9.7」になっており、jp-eferral-0.9.7-snap.tgzとなります。
    - いずれも、[FHIR package仕様](https://registry.fhir.org/learn)に従ったnpmパッケージ形式です。
    - HAPIのバリデータでは、[NpmPackageValidationSupport](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html#npmpackagevalidationsupport)クラスにより、パッケージを読み込み、検証できます。
    - NpmPackageValidationSupportクラスによる、パッケージを使ったバリデーションの実装方法については、[HAPI FHIRのドキュメントの「Validating Using Packages」](https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html#packages)を参考に実装しています。

- FHIRデータのパース
    - [HAPI FHIRのパーサ](https://hapifhir.io/hapi-fhir/docs/model/parsers.html)を使って、パースをしています。    

## シリアライズ
- FHIRデータからJSONへのシリアライズ
    - パース同様、[HAPI FHIRのパーサ](https://hapifhir.io/hapi-fhir/docs/model/parsers.html)を使って、シリアライズをしています。  


## 実行方法
- 検証・パースするサンプルAPの使い方
    - Java、Mavenでビルドし、「ParsingSampleMain」クラスを実行してください。
    - 通常は、Eclipse等のIDEを使って実行するのが簡単です。

- JSONシリアライズするサンプルAPの使い方
    - Java、Mavenでビルドし、「SerializingSampleMain」クラスを実行してください。
    - 通常は、Eclipse等のIDEを使って実行するのが簡単です。

## 検証・パースの実行結果の例
- パース処理はうまくいっていますが、文書情報プロファイルおよびJPCoreプロファイルに対する検証（バリデーション）で大量のエラーメッセージが出てしまっています。
- おそらく、診療情報提供書の文書プロファイルのnpmパッケージだけだと、JPCoreの定義情報が足りないためではないかと推測されます。
    - ただし、試しに、JPCoreのnpmパッケージファイル(package.tgz)を読み込むようにNpmPackageValidationSupportを追加してみたところ、予期せぬ例外（NullPointerException）が発生してしまいます。

```sh
22:32:12.752 [main] INFO  ca.uhn.fhir.util.VersionUtil - HAPI FHIR version 6.4.4 - Rev 107a1bd073
22:32:12.775 [main] INFO  ca.uhn.fhir.context.FhirContext - Creating new FHIR context for FHIR version [R4]
22:32:14.157 [main] INFO  ca.uhn.fhir.util.XmlUtil - Unable to determine StAX implementation: java.xml/META-INF/MANIFEST.MF not found
22:32:15.644 [main] INFO  c.uhn.fhir.validation.FhirValidator - Ph-schematron library not found on classpath, will not attempt to perform schematron validation
22:32:15.712 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading structure definitions from classpath: /org/hl7/fhir/r4/model/profile/profiles-resources.xml
22:32:16.993 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading structure definitions from classpath: /org/hl7/fhir/r4/model/profile/profiles-types.xml
22:32:17.125 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading structure definitions from classpath: /org/hl7/fhir/r4/model/profile/profiles-others.xml
22:32:17.429 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading structure definitions from classpath: /org/hl7/fhir/r4/model/extension/extension-definitions.xml
22:32:19.270 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading CodeSystem/ValueSet from classpath: /org/hl7/fhir/r4/model/valueset/valuesets.xml
22:32:19.569 [main] WARN  c.u.fhir.parser.LenientErrorHandler - Unknown element 'author' found while parsing
22:32:19.571 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading CodeSystem/ValueSet from classpath: /org/hl7/fhir/r4/model/valueset/v2-tables.xml
22:32:19.858 [main] WARN  c.u.fhir.parser.LenientErrorHandler - Unknown element 'author' found while parsing
22:32:19.859 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading CodeSystem/ValueSet from classpath: /org/hl7/fhir/r4/model/valueset/v3-codesystems.xml
22:32:19.967 [main] WARN  c.u.fhir.parser.LenientErrorHandler - Unknown element 'author' found while parsing

# ここから、バリデーションの結果。うまくエラーが大量に出ている。
22:32:21.079 [main] WARN  hapisample.Main - ドキュメントに不備があります
22:32:21.080 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[0] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory
22:32:21.081 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[1]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[1] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory
22:32:21.081 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[2]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[2] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory
22:32:21.081 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[3]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[3] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory
22:32:21.081 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[4]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[4] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory
22:32:21.082 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[5]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[5] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory
22:32:21.082 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[6]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[6] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory
22:32:21.082 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[7]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[7] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory
22:32:21.082 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[8]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[8] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory
22:32:21.082 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[9]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[9] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory
22:32:21.083 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[10]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[10] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory
22:32:21.083 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[11]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[11] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory
22:32:21.083 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[12]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[12] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory
22:32:21.083 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[13]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[13] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory
22:32:21.083 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[14]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[14] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory
22:32:21.084 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[15]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[15] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory
22:32:21.084 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[16]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[16] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory
22:32:21.084 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[17]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[17] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory
22:32:21.084 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[18]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[18] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_FamilyMemberHistory
22:32:21.084 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[0] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest
22:32:21.084 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[1]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[1] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest
22:32:21.084 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[2]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[2] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest
22:32:21.084 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[3]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[3] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest
22:32:21.085 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[4]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[4] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest
22:32:21.085 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[5]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[5] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest
22:32:21.085 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[6]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[6] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest
22:32:21.085 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[7]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[7] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest
22:32:21.085 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[8]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[8] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest
22:32:21.085 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[9]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[9] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest
22:32:21.085 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[10]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[10] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest
22:32:21.086 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[11]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[11] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest
22:32:21.086 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[12]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[12] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest
22:32:21.086 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[13]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[13] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest
22:32:21.086 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[14]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[14] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest
22:32:21.086 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[15]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[15] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest
22:32:21.086 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[16]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[16] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest
22:32:21.086 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[17]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[17] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest
22:32:21.086 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[18]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[18] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest
22:32:21.087 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[0] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology
22:32:21.087 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[1]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[1] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology
22:32:21.087 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[2]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[2] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology
22:32:21.087 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[3]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[3] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology
22:32:21.087 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[4]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[4] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology
22:32:21.087 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[5]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[5] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology
22:32:21.087 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[6]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[6] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology
22:32:21.088 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[7]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[7] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology
22:32:21.088 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[8]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[8] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology
22:32:21.088 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[9]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[9] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology
22:32:21.088 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[10]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[10] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology
22:32:21.088 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[11]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[11] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology
22:32:21.088 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[12]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[12] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology
22:32:21.088 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[13]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[13] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology
22:32:21.089 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[14]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[14] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology
22:32:21.089 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[15]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[15] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology
22:32:21.089 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[16]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[16] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology
22:32:21.089 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[17]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[17] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology
22:32:21.089 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[18]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[18] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_ImagingStudy_Radiology
22:32:21.089 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[0] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common
22:32:21.089 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[1]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[1] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common
22:32:21.090 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[2]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[2] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common
22:32:21.090 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[3]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[3] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common
22:32:21.090 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[4]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[4] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common
22:32:21.090 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[5]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[5] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common
22:32:21.090 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[6]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[6] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common
22:32:21.090 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[7]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[7] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common
22:32:21.091 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[8]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[8] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common
22:32:21.091 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[9]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[9] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common
22:32:21.091 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[10]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[10] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common
22:32:21.091 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[11]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[11] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common
22:32:21.091 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[12]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[12] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common
22:32:21.091 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[13]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[13] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common
22:32:21.091 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[14]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[14] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common
22:32:21.091 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[15]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[15] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common
22:32:21.092 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[16]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[16] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common
22:32:21.092 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[17]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[17] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common
22:32:21.092 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[18]] Slicing cannot be evaluated: Problem evaluating slicing expression for element in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5 path Bundle.entry[18] (fhirPath = true and resource.conformsTo('http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common')): Unable to resolve the reference http://jpfhir.jp/fhir/core/StructureDefinition/JP_DiagnosticReport_Common
22:32:21.092 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
22:32:21.092 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[1]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
22:32:21.092 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[2]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
22:32:21.092 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[3]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
22:32:21.092 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[4]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
22:32:21.093 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[5]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
22:32:21.093 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[6]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
22:32:21.093 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[7]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
22:32:21.093 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[8]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
22:32:21.093 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[9]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
22:32:21.093 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[10]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
22:32:21.094 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[11]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
22:32:21.094 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[12]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
22:32:21.094 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[13]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
22:32:21.094 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[14]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
22:32:21.094 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[15]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
22:32:21.094 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[16]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
22:32:21.094 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[17]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
22:32:21.094 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[18]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
22:32:21.094 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle] Bundle.entry:composition: Found 0 matches, but unable to check minimum required (Bundle.entry) due to lack of slicing validation (from http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
22:32:21.095 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle] Bundle.entry:patient: Found 0 matches, but unable to check minimum required (Bundle.entry) due to lack of slicing validation (from http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
22:32:21.095 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle] Bundle.entry:practitioners: Found 0 matches, but unable to check minimum required (Bundle.entry) due to lack of slicing validation (from http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
22:32:21.095 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle] Bundle.entry:organization: Found 0 matches, but unable to check minimum required (Bundle.entry) due to lack of slicing validation (from http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
22:32:21.095 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle] Bundle.entry:problem: Found 0 matches, but unable to check minimum required (Bundle.entry) due to lack of slicing validation (from http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
22:32:21.095 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle.entry[0].resource.ofType(Composition).encounter] Details for urn:uuid:7cad1f19-3435-451d-9a71-a81b61f3358e matching against profile http://hl7.org/fhir/StructureDefinition/Encounter|4.0.1
22:32:21.095 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle.entry[0].resource.ofType(Composition).author[0]] Details for urn:uuid:3e6a0ba2-d781-4fd7-9de6-e077b690daed matching against profile http://hl7.org/fhir/StructureDefinition/Practitioner|4.0.1
22:32:21.095 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle.entry[0].resource.ofType(Composition).author[1]] Details for urn:uuid:8a888471-9781-4fb7-b5c4-b34afcdea638 matching against profile http://hl7.org/fhir/StructureDefinition/Organization|4.0.1
22:32:21.096 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle.entry[0].resource.ofType(Composition).custodian] Details for urn:uuid:8a888471-9781-4fb7-b5c4-b34afcdea638 matching against profile http://hl7.org/fhir/StructureDefinition/Organization|4.0.1
22:32:21.096 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle.entry[0].resource.author[0]] This element does not match any known slice defined in the profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Composition_eReferral|1.1.5
22:32:21.096 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle.entry[0].resource.author[1]] This element does not match any known slice defined in the profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Composition_eReferral|1.1.5
22:32:21.096 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource] Composition.author:authorPractitioner: minimum required = 1, but only found 0 (from http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Composition_eReferral|1.1.5)
22:32:21.096 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource] Composition.author:authorOrganization: minimum required = 1, but only found 0 (from http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Composition_eReferral|1.1.5)
22:32:21.096 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.type] None of the codings provided are in the value set 'valueSet' (http://jpfhir.jp/fhir/Common/ValueSet/doc-typecodes|1.1.5), and a coding from this value set is required) (codes = http://jpfhir.jp/fhir/Common/CodeSystem/doc-typecodes#57133-1)
22:32:21.096 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.category[0]] None of the codings provided are in the value set 'valueSet' (http://jpfhir.jp/fhir/Common/ValueSet/doc-typecodes|1.1.5), and a coding from this value set is required) (codes = http://jpfhir.jp/fhir/Common/CodeSystem/doc-typecodes#57133-1)
22:32:21.096 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle.entry[0].resource.encounter] Details for urn:uuid:7cad1f19-3435-451d-9a71-a81b61f3358e matching against profile http://hl7.org/fhir/StructureDefinition/Encounter|4.0.1
22:32:21.097 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle.entry[0].resource.author[0]] Details for urn:uuid:3e6a0ba2-d781-4fd7-9de6-e077b690daed matching against profile http://hl7.org/fhir/StructureDefinition/Practitioner|4.0.1
22:32:21.097 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle.entry[0].resource.author[1]] Details for urn:uuid:8a888471-9781-4fb7-b5c4-b34afcdea638 matching against profile http://hl7.org/fhir/StructureDefinition/Organization|4.0.1
22:32:21.097 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.custodian] Unable to find a match for profile urn:uuid:8a888471-9781-4fb7-b5c4-b34afcdea638 among choices: http://jpfhir.jp/fhir/eClinicalSummary/StructureDefinition/JP_Organization_eClinicalSummary
22:32:21.097 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle.entry[0].resource.custodian] Details for urn:uuid:8a888471-9781-4fb7-b5c4-b34afcdea638 matching against profile http://jpfhir.jp/fhir/eClinicalSummary/StructureDefinition/JP_Organization_eClinicalSummary|1.1.5
22:32:21.097 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle.entry[0].resource.section[0].entry[0]] This element does not match any known slice defined in the profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Composition_eReferral|1.1.5
22:32:21.097 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle.entry[0].resource.section[0].entry[2]] This element does not match any known slice defined in the profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Composition_eReferral|1.1.5
22:32:21.097 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.section[0]] Composition.section:referralToSection.entry:referralToOrganization: minimum required = 1, but only found 0 (from http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Composition_eReferral|1.1.5)
22:32:21.097 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle.entry[0].resource.section[1].entry[0]] This element does not match any known slice defined in the profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Composition_eReferral|1.1.5
22:32:21.098 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle.entry[0].resource.section[1].entry[2]] This element does not match any known slice defined in the profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Composition_eReferral|1.1.5
22:32:21.098 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.section[1]] Composition.section:referralFromSection.entry:referralFromOrganization: minimum required = 1, but only found 0 (from http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Composition_eReferral|1.1.5)
22:32:21.098 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.section[2].section[0].entry[0]] Unable to resolve the profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Encounter'
22:32:21.098 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.section[2].section[0].entry[0]] The type 'Encounter' is not a valid Target for this element (must be one of [CanonicalType[http://jpfhir.jp/fhir/core/StructureDefinition/JP_Encounter]])
22:32:21.098 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.section[2].section[0].entry[0]] Invalid Resource target type. Found Encounter, but expected one of ([])
22:32:21.098 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.section[2].section[1].entry[0]] Unable to resolve the profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Condition'
22:32:21.098 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.section[2].section[1].entry[0]] The type 'Condition' is not a valid Target for this element (must be one of [CanonicalType[http://jpfhir.jp/fhir/core/StructureDefinition/JP_Condition]])
22:32:21.098 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.section[2].section[1].entry[0]] Invalid Resource target type. Found Condition, but expected one of ([])
22:32:21.098 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.section[2].section[1].entry[1]] Unable to resolve the profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Condition'
22:32:21.099 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.section[2].section[1].entry[1]] The type 'Condition' is not a valid Target for this element (must be one of [CanonicalType[http://jpfhir.jp/fhir/core/StructureDefinition/JP_Condition]])
22:32:21.099 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.section[2].section[1].entry[1]] Invalid Resource target type. Found Condition, but expected one of ([])
22:32:21.099 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.section[2].section[2].entry[0]] Unable to resolve the profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Condition'
22:32:21.099 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.section[2].section[2].entry[0]] The type 'Condition' is not a valid Target for this element (must be one of [CanonicalType[http://jpfhir.jp/fhir/core/StructureDefinition/JP_Condition]])
22:32:21.099 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.section[2].section[2].entry[0]] Invalid Resource target type. Found Condition, but expected one of ([])
22:32:21.099 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.section[2].section[3].entry[0]] Unable to resolve the profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Condition'
22:32:21.099 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.section[2].section[3].entry[0]] The type 'Condition' is not a valid Target for this element (must be one of [CanonicalType[http://jpfhir.jp/fhir/core/StructureDefinition/JP_Condition]])
22:32:21.099 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.section[2].section[3].entry[0]] Invalid Resource target type. Found Condition, but expected one of ([])
22:32:21.099 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.section[2].section[3].entry[1]] Unable to resolve the profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Condition'
22:32:21.099 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.section[2].section[3].entry[1]] The type 'Condition' is not a valid Target for this element (must be one of [CanonicalType[http://jpfhir.jp/fhir/core/StructureDefinition/JP_Condition]])
22:32:21.100 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.section[2].section[3].entry[1]] Invalid Resource target type. Found Condition, but expected one of ([])
22:32:21.100 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.section[2].section[4].entry[0]] Unable to resolve the profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_AllergyIntolerance'
22:32:21.100 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.section[2].section[4].entry[0]] The type 'AllergyIntolerance' is not a valid Target for this element (must be one of [CanonicalType[http://jpfhir.jp/fhir/core/StructureDefinition/JP_AllergyIntolerance]])
22:32:21.100 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.section[2].section[4].entry[0]] Invalid Resource target type. Found AllergyIntolerance, but expected one of ([])
22:32:21.100 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.section[2].section[4].entry[1]] Unable to resolve the profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_AllergyIntolerance'
22:32:21.100 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.section[2].section[4].entry[1]] The type 'AllergyIntolerance' is not a valid Target for this element (must be one of [CanonicalType[http://jpfhir.jp/fhir/core/StructureDefinition/JP_AllergyIntolerance]])
22:32:21.100 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.section[2].section[4].entry[1]] Invalid Resource target type. Found AllergyIntolerance, but expected one of ([])
22:32:21.100 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.section[2].section[6].entry[0]] Unable to resolve the profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Observation_Common'
22:32:21.100 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.section[2].section[6].entry[0]] The type 'Observation' is not a valid Target for this element (must be one of [CanonicalType[http://jpfhir.jp/fhir/core/StructureDefinition/JP_Observation_Common]])
22:32:21.101 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.section[2].section[6].entry[0]] Invalid Resource target type. Found Observation, but expected one of ([])
22:32:21.101 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[1].resource.ofType(Patient).meta.profile[0]] Profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Patient' has not been checked because it is unknown
22:32:21.101 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[2].resource.ofType(Encounter).meta.profile[0]] Profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Encounter' has not been checked because it is unknown
22:32:21.101 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[0].resource.entry[2].resource.ofType(Encounter)] Encounter.reasonCode: minimum required = 1, but only found 0 (from http://jpfhir.jp/fhir/eClinicalSummary/StructureDefinition/JP_Encounter_eClinicalSummary|1.1.5)
22:32:21.101 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[3].resource.ofType(Practitioner).meta.profile[0]] Profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Practitioner' has not been checked because it is unknown
22:32:21.101 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[3].resource.name[0]] Unknown profile http://jpfhir.jp/fhir/core/StructureDefinition/JP_HumanName
22:32:21.101 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[3].resource.name[1]] Unknown profile http://jpfhir.jp/fhir/core/StructureDefinition/JP_HumanName
22:32:21.101 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[4].resource.ofType(Practitioner).meta.profile[0]] Profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Practitioner' has not been checked because it is unknown
22:32:21.101 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[4].resource.name[0]] Unknown profile http://jpfhir.jp/fhir/core/StructureDefinition/JP_HumanName
22:32:21.102 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[4].resource.name[1]] Unknown profile http://jpfhir.jp/fhir/core/StructureDefinition/JP_HumanName
22:32:21.102 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle.entry[0].resource.entry[5].resource.ofType(Organization).extension[0]] Unknown extension http://jpfhir.jp/fhir/core/Extension/StructureDefinition/JP_Organization_PrefectureNo
22:32:21.102 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle.entry[0].resource.entry[5].resource.ofType(Organization).extension[1]] Unknown extension http://jpfhir.jp/fhir/core/Extension/StructureDefinition/JP_Organization_InsuranceOrganizationCategory
22:32:21.102 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle.entry[0].resource.entry[5].resource.ofType(Organization).extension[2]] Unknown extension http://jpfhir.jp/fhir/core/Extension/StructureDefinition/JP_Organization_InsuranceOrganizationNo
22:32:21.102 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[5].resource.ofType(Organization).meta.profile[0]] Profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Organization' has not been checked because it is unknown
22:32:21.102 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[5].resource.extension[0]] Slicing cannot be evaluated: Unable to resolve profile CanonicalType[http://jpfhir.jp/fhir/core/Extension/StructureDefinition/JP_Organization_PrefectureNo]
22:32:21.102 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[5].resource.extension[1]] Slicing cannot be evaluated: Unable to resolve profile CanonicalType[http://jpfhir.jp/fhir/core/Extension/StructureDefinition/JP_Organization_PrefectureNo]
22:32:21.103 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[5].resource.extension[2]] Slicing cannot be evaluated: Unable to resolve profile CanonicalType[http://jpfhir.jp/fhir/core/Extension/StructureDefinition/JP_Organization_PrefectureNo]
22:32:21.103 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[5].resource.extension[0]] Slicing cannot be evaluated: Unable to resolve profile CanonicalType[http://jpfhir.jp/fhir/core/Extension/StructureDefinition/JP_Organization_InsuranceOrganizationCategory]
22:32:21.103 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[5].resource.extension[1]] Slicing cannot be evaluated: Unable to resolve profile CanonicalType[http://jpfhir.jp/fhir/core/Extension/StructureDefinition/JP_Organization_InsuranceOrganizationCategory]
22:32:21.103 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[5].resource.extension[2]] Slicing cannot be evaluated: Unable to resolve profile CanonicalType[http://jpfhir.jp/fhir/core/Extension/StructureDefinition/JP_Organization_InsuranceOrganizationCategory]
22:32:21.103 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[5].resource.extension[0]] Slicing cannot be evaluated: Unable to resolve profile CanonicalType[http://jpfhir.jp/fhir/core/Extension/StructureDefinition/JP_Organization_InsuranceOrganizationNo]
22:32:21.103 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[5].resource.extension[1]] Slicing cannot be evaluated: Unable to resolve profile CanonicalType[http://jpfhir.jp/fhir/core/Extension/StructureDefinition/JP_Organization_InsuranceOrganizationNo]
22:32:21.103 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[5].resource.extension[2]] Slicing cannot be evaluated: Unable to resolve profile CanonicalType[http://jpfhir.jp/fhir/core/Extension/StructureDefinition/JP_Organization_InsuranceOrganizationNo]
22:32:21.104 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle.entry[5].resource.extension[0]] Unknown extension http://jpfhir.jp/fhir/core/Extension/StructureDefinition/JP_Organization_PrefectureNo
22:32:21.104 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle.entry[5].resource.extension[1]] Unknown extension http://jpfhir.jp/fhir/core/Extension/StructureDefinition/JP_Organization_InsuranceOrganizationCategory
22:32:21.104 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle.entry[5].resource.extension[2]] Unknown extension http://jpfhir.jp/fhir/core/Extension/StructureDefinition/JP_Organization_InsuranceOrganizationNo
22:32:21.104 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle.entry[6].resource.ofType(Organization).extension[0]] Unknown extension http://jpfhir.jp/fhir/core/Extension/StructureDefinition/JP_Organization_PrefectureNo
22:32:21.104 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle.entry[6].resource.ofType(Organization).extension[1]] Unknown extension http://jpfhir.jp/fhir/core/Extension/StructureDefinition/JP_Organization_InsuranceOrganizationCategory
22:32:21.104 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle.entry[6].resource.ofType(Organization).extension[2]] Unknown extension http://jpfhir.jp/fhir/core/Extension/StructureDefinition/JP_Organization_InsuranceOrganizationNo
22:32:21.104 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[6].resource.ofType(Organization).meta.profile[0]] Profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Organization' has not been checked because it is unknown
22:32:21.104 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[6].resource.extension[0]] Slicing cannot be evaluated: Unable to resolve profile CanonicalType[http://jpfhir.jp/fhir/core/Extension/StructureDefinition/JP_Organization_PrefectureNo]
22:32:21.105 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[6].resource.extension[1]] Slicing cannot be evaluated: Unable to resolve profile CanonicalType[http://jpfhir.jp/fhir/core/Extension/StructureDefinition/JP_Organization_PrefectureNo]
22:32:21.105 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[6].resource.extension[2]] Slicing cannot be evaluated: Unable to resolve profile CanonicalType[http://jpfhir.jp/fhir/core/Extension/StructureDefinition/JP_Organization_PrefectureNo]
22:32:21.105 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[6].resource.extension[0]] Slicing cannot be evaluated: Unable to resolve profile CanonicalType[http://jpfhir.jp/fhir/core/Extension/StructureDefinition/JP_Organization_InsuranceOrganizationCategory]
22:32:21.105 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[6].resource.extension[1]] Slicing cannot be evaluated: Unable to resolve profile CanonicalType[http://jpfhir.jp/fhir/core/Extension/StructureDefinition/JP_Organization_InsuranceOrganizationCategory]
22:32:21.105 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[6].resource.extension[2]] Slicing cannot be evaluated: Unable to resolve profile CanonicalType[http://jpfhir.jp/fhir/core/Extension/StructureDefinition/JP_Organization_InsuranceOrganizationCategory]
22:32:21.105 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[6].resource.extension[0]] Slicing cannot be evaluated: Unable to resolve profile CanonicalType[http://jpfhir.jp/fhir/core/Extension/StructureDefinition/JP_Organization_InsuranceOrganizationNo]
22:32:21.106 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[6].resource.extension[1]] Slicing cannot be evaluated: Unable to resolve profile CanonicalType[http://jpfhir.jp/fhir/core/Extension/StructureDefinition/JP_Organization_InsuranceOrganizationNo]
22:32:21.106 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[6].resource.extension[2]] Slicing cannot be evaluated: Unable to resolve profile CanonicalType[http://jpfhir.jp/fhir/core/Extension/StructureDefinition/JP_Organization_InsuranceOrganizationNo]
22:32:21.106 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle.entry[6].resource.extension[0]] Unknown extension http://jpfhir.jp/fhir/core/Extension/StructureDefinition/JP_Organization_PrefectureNo
22:32:21.106 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle.entry[6].resource.extension[1]] Unknown extension http://jpfhir.jp/fhir/core/Extension/StructureDefinition/JP_Organization_InsuranceOrganizationCategory
22:32:21.106 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle.entry[6].resource.extension[2]] Unknown extension http://jpfhir.jp/fhir/core/Extension/StructureDefinition/JP_Organization_InsuranceOrganizationNo
22:32:21.106 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[7].resource.ofType(Organization).meta.profile[0]] Profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Organization' has not been checked because it is unknown
22:32:21.106 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[8].resource.ofType(Organization).meta.profile[0]] Profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Organization' has not been checked because it is unknown
22:32:21.107 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[9].resource.ofType(Encounter).meta.profile[0]] Profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Encounter' has not been checked because it is unknown
22:32:21.107 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[10].resource.ofType(Condition).meta.profile[0]] Profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Condition' has not been checked because it is unknown
22:32:21.107 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[10].resource.subject] Unable to resolve the profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Patient'
22:32:21.107 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[10].resource.subject] Invalid Resource target type. Found Patient, but expected one of ([])
22:32:21.107 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[11].resource.ofType(Condition).meta.profile[0]] Profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Condition' has not been checked because it is unknown
22:32:21.107 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[11].resource.subject] Unable to resolve the profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Patient'
22:32:21.107 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[11].resource.subject] Invalid Resource target type. Found Patient, but expected one of ([])
22:32:21.107 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[12].resource.ofType(Condition).meta.profile[0]] Profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Condition' has not been checked because it is unknown
22:32:21.107 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[12].resource.subject] Unable to resolve the profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Patient'
22:32:21.107 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[12].resource.subject] Invalid Resource target type. Found Patient, but expected one of ([])
22:32:21.108 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[13].resource.ofType(Condition).meta.profile[0]] Profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Condition' has not been checked because it is unknown
22:32:21.108 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[13].resource.subject] Unable to resolve the profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Patient'
22:32:21.108 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[13].resource.subject] Invalid Resource target type. Found Patient, but expected one of ([])
22:32:21.108 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[14].resource.ofType(Condition).meta.profile[0]] Profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Condition' has not been checked because it is unknown
22:32:21.108 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[14].resource.subject] Unable to resolve the profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Patient'
22:32:21.108 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[14].resource.subject] Invalid Resource target type. Found Patient, but expected one of ([])
22:32:21.108 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle.entry[15].resource.ofType(AllergyIntolerance).encounter] Details for urn:uuid:7cad1f19-3435-451d-9a71-a81b61f3358e matching against profile http://hl7.org/fhir/StructureDefinition/Encounter|4.0.1
22:32:21.108 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[15].resource.ofType(AllergyIntolerance).meta.profile[0]] Profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_AllergyIntolerance' has not been checked because it is unknown
22:32:21.108 [main] WARN  hapisample.Main - [WARNING]:[Bundle.entry[15].resource.code] ValueSet http://jpfhir.jp/fhir/core/ValueSet/JP_AllergyIntolerance_VS not found by validator
22:32:21.108 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[15].resource.patient] Unable to resolve the profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Patient'
22:32:21.109 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[15].resource.patient] Invalid Resource target type. Found Patient, but expected one of ([])
22:32:21.109 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[15].resource.encounter] Unable to resolve the profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Encounter'
22:32:21.109 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[15].resource.encounter] Invalid Resource target type. Found Encounter, but expected one of ([])
22:32:21.110 [main] WARN  hapisample.Main - [INFORMATION]:[Bundle.entry[16].resource.ofType(AllergyIntolerance).encounter] Details for urn:uuid:7cad1f19-3435-451d-9a71-a81b61f3358e matching against profile http://hl7.org/fhir/StructureDefinition/Encounter|4.0.1
22:32:21.110 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[16].resource.ofType(AllergyIntolerance).meta.profile[0]] Profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_AllergyIntolerance' has not been checked because it is unknown
22:32:21.110 [main] WARN  hapisample.Main - [WARNING]:[Bundle.entry[16].resource.code] ValueSet http://jpfhir.jp/fhir/core/ValueSet/JP_AllergyIntolerance_VS not found by validator
22:32:21.110 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[16].resource.patient] Unable to resolve the profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Patient'
22:32:21.110 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[16].resource.patient] Invalid Resource target type. Found Patient, but expected one of ([])
22:32:21.110 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[16].resource.encounter] Unable to resolve the profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Encounter'
22:32:21.111 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[16].resource.encounter] Invalid Resource target type. Found Encounter, but expected one of ([])
22:32:21.111 [main] WARN  hapisample.Main - [ERROR]:[Bundle.entry[17].resource.ofType(Observation).meta.profile[0]] Profile reference 'http://jpfhir.jp/fhir/core/StructureDefinition/JP_Observation_Common' has not been checked because it is unknown

# ここから、パース処理の結果。うまくいっている。
22:32:21.112 [main] INFO  hapisample.Main - Bundle type:Document
22:32:21.115 [main] INFO  hapisample.Main - Resource Type: Composition
22:32:21.115 [main] INFO  hapisample.Main - 文書名: 診療情報提供書
22:32:21.116 [main] INFO  hapisample.Main - subject display: 患者リソースPatient
22:32:21.116 [main] INFO  hapisample.Main - subject reference Id: urn:uuid:0a48a4bf-0d87-4efb-aafd-d45e0842a4dd
22:32:21.116 [main] INFO  hapisample.Main - Resource Type: Patient
22:32:21.116 [main] INFO  hapisample.Main - Composition.subjectの参照先のPatient:urn:uuid:0a48a4bf-0d87-4efb-aafd-d45e0842a4dd
22:32:21.116 [main] INFO  hapisample.Main - 患者番号:12345
22:32:21.117 [main] INFO  hapisample.Main - 患者氏名:田中 太郎
22:32:21.118 [main] INFO  hapisample.Main - 患者カナ氏名:タナカ タロウ
22:32:21.118 [main] INFO  hapisample.Main - Resource Type: Encounter
22:32:21.118 [main] INFO  hapisample.Main - Resource Type: Practitioner
22:32:21.119 [main] INFO  hapisample.Main - Resource Type: Practitioner
22:32:21.119 [main] INFO  hapisample.Main - Resource Type: Organization
22:32:21.119 [main] INFO  hapisample.Main - Resource Type: Organization
22:32:21.119 [main] INFO  hapisample.Main - Resource Type: Organization
22:32:21.119 [main] INFO  hapisample.Main - Resource Type: Organization
22:32:21.119 [main] INFO  hapisample.Main - Resource Type: Encounter
22:32:21.119 [main] INFO  hapisample.Main - Resource Type: Condition
22:32:21.120 [main] INFO  hapisample.Main - Resource Type: Condition
22:32:21.120 [main] INFO  hapisample.Main - Resource Type: Condition
22:32:21.120 [main] INFO  hapisample.Main - Resource Type: Condition
22:32:21.120 [main] INFO  hapisample.Main - Resource Type: Condition
22:32:21.120 [main] INFO  hapisample.Main - Resource Type: AllergyIntolerance
22:32:21.120 [main] INFO  hapisample.Main - Resource Type: AllergyIntolerance
22:32:21.120 [main] INFO  hapisample.Main - Resource Type: Observation
22:32:21.121 [main] INFO  hapisample.Main - Resource Type: DocumentReference
```

## JSONシリアライズ実行結果の例
- [処方情報のFHIR記述仕様書](https://jpfhir.jp/fhir/ePrescriptionData/igv1/)に従い、JSON文字列のほんの一部分が生成出来てるのが分かります。

```sh
22:30:19.763 [main] INFO  ca.uhn.fhir.util.VersionUtil - HAPI FHIR version 6.4.4 - Rev 107a1bd073
22:30:19.771 [main] INFO  ca.uhn.fhir.context.FhirContext - Creating new FHIR context for FHIR version [R4]
22:30:21.194 [main] INFO  hapisample.SerializingSampleMain - 実行結果:
# 処方情報のFHIRのJSON文字列の一部を生成
{
  "resourceType": "Bundle",
  "meta": {
    "lastUpdated": "2023-06-17T22:30:19.732+09:00",
    "profile": [ "http://jpfhir.jp/fhir/ePrescription/StructureDefinition/JP_Bundle_ePrescriptionData" ]
  },
  "type": "document",
  "timestamp": "2023-06-17T22:30:19.732+09:00",
  "entry": [ {
    "fullUrl": "urn:uuid:6b9e2bec-dd22-4ca6-8392-835d4b5172dd",
    "resource": {
      "resourceType": "Composition",
      "id": "compositionReferralExample01Inline",
      "meta": {
        "lastUpdated": "2023-06-17T22:30:19.732+09:00",
        "profile": [ "http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Composition_ePrescriptionData" ]
      },
      "text": {
        "status": "generated",
        "div": "<div xmlns=\"http://www.w3.org/1999/xhtml\">xxx</div>"
      },
      "extension": [ {
        "url": "http://hl7.org/fhir/StructureDefinition/composition-clinicaldocument-versionNumber",
        "valueString": "1.0"
      } ],
      "identifier": {
        "system": "http://jpfhir.jp/fhir/Common/IdSystem/resourceInstance-identifier",
        "value": "1311234567-2020-00123456"
      },
      "status": "final",
      "type": {
        "coding": [ {
          "system": "http://jpfhir.jp/fhir/Common/CodeSystem/doc-typecodes",
          "code": "57833-6",
          "display": "処方箋"
        } ]
      },
      "category": [ {
        "coding": [ {
          "system": "http://jpfhir.jp/fhir/ePrescription/CodeSystem/prescription-category",
          "code": "01",
          "display": "処方箋"
        } ]
      } ],
      "subject": {
        "reference": "urn:uuid:224a5d95-2a22-433b-889d-5b087568a1c3"
      },
      "date": "2023-06-17T22:30:19+09:00",
      "title": "処方箋"
    }
  }, {
    "fullUrl": "urn:uuid:224a5d95-2a22-433b-889d-5b087568a1c3",
    "resource": {
      "resourceType": "Patient",
      "meta": {
        "lastUpdated": "2023-06-17T22:30:19.732+09:00",
        "profile": [ "http://jpfhir.jp/fhir/ePrescription/StructureDefinition/JP_Patient_ePrescriptionData" ]
      },
      "text": {
        "status": "generated",
        "div": "<div xmlns=\"http://www.w3.org/1999/xhtml\">xxx</div>"
      },
      "identifier": [ {
        "system": "urn:oid:1.2.392.100495.20.3.51.11311234567",
        "value": "00000010"
      }, {
        "system": "http:/jpfhir.jp/fhir/ccs/Idsysmem/JP_Insurance_member/00012345",
        "value": "00012345:あいう:１８７:05"
      } ],
      "name": [ {
        "extension": [ {
          "url": "http:// hl7.org/fhir/StructureDefinition/iso21090-EN-representation",
          "valueString": "IDE"
        } ],
        "use": "official",
        "text": "東京　太郎",
        "family": "東京",
        "given": [ "太郎" ]
      }, {
        "extension": [ {
          "url": "http:// hl7.org/fhir/StructureDefinition/iso21090-EN-representation",
          "valueString": "SYL"
        } ],
        "use": "official",
        "text": "トウキョウ　タロウ",
        "family": "トウキョウ",
        "given": [ "タロウ" ]
      } ],
      "gender": "male",
      "birthDate": "1920-01-11",
      "address": [ {
        "text": "神奈川県横浜市港区１－２－３",
        "postalCode": "123-4567",
        "country": "JP"
      } ]
    }
  } ]
}
```
