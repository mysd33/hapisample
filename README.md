# HAPI FHIRのサンプル

[!WARNING]
当時、検証に利用したプロファイルの定義から、新しいバージョンが出ています。
現在、[https://std.jpfhir.jp/](https://std.jpfhir.jp/)で提供される、最新のものでは試していません。

- [HAPI FHIR](https://hapifhir.io/)を使って、[診療情報提供書HL7FHIR記述仕様](https://std.jpfhir.jp/)に基づくサンプルデータ（Bundle-BundleReferralExample01.json）に対して検証し、Bundleリソースとしてパースするサンプルプログラムです。

- また、FHIRリソース(Bundle)として作成したオブジェクトを、FHIRのJSON文字列で出力（シリアライズ）するサンプルプログラムもあります。
    - [処方情報のFHIR記述仕様書](https://jpfhir.jp/fhir/ePrescriptionData/igv1/)に従い、JSONのほんの一部分を生成しています。

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
- 公開されているプロファイル情報およびサンプルデータをそのまま利用すると、パース処理はうまくいっていますが、文書情報プロファイルおよびJPCoreプロファイルに対する検証（バリデーション）で大量のエラーメッセージが出てしまいます。

```sh
06:57:45.668 [main] INFO  ca.uhn.fhir.util.VersionUtil - HAPI FHIR version 6.4.4 - Rev 107a1bd073
06:57:45.678 [main] INFO  ca.uhn.fhir.context.FhirContext - Creating new FHIR context for FHIR version [R4]
06:57:47.094 [main] INFO  ca.uhn.fhir.util.XmlUtil - Unable to determine StAX implementation: java.xml/META-INF/MANIFEST.MF not found
06:57:50.246 [main] INFO  c.uhn.fhir.validation.FhirValidator - Ph-schematron library not found on classpath, will not attempt to perform schematron validation
06:57:50.324 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading structure definitions from classpath: /org/hl7/fhir/r4/model/profile/profiles-resources.xml
06:57:51.558 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading structure definitions from classpath: /org/hl7/fhir/r4/model/profile/profiles-types.xml
06:57:51.643 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading structure definitions from classpath: /org/hl7/fhir/r4/model/profile/profiles-others.xml
06:57:51.877 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading structure definitions from classpath: /org/hl7/fhir/r4/model/extension/extension-definitions.xml
06:57:53.740 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading CodeSystem/ValueSet from classpath: /org/hl7/fhir/r4/model/valueset/valuesets.xml
06:57:54.144 [main] WARN  c.u.fhir.parser.LenientErrorHandler - Unknown element 'author' found while parsing
06:57:54.147 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading CodeSystem/ValueSet from classpath: /org/hl7/fhir/r4/model/valueset/v2-tables.xml
06:57:54.492 [main] WARN  c.u.fhir.parser.LenientErrorHandler - Unknown element 'author' found while parsing
06:57:54.492 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading CodeSystem/ValueSet from classpath: /org/hl7/fhir/r4/model/valueset/v3-codesystems.xml
06:57:54.634 [main] WARN  c.u.fhir.parser.LenientErrorHandler - Unknown element 'author' found while parsing

# ここから、バリデーションの結果。うまくエラーが大量に出ている。
06:57:56.014 [main] WARN  hapisample.ParsingSampleMain - ドキュメントに不備があります
06:57:56.015 [main] WARN  hapisample.ParsingSampleMain - [ERROR]:[Bundle.entry[0]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
06:57:56.015 [main] WARN  hapisample.ParsingSampleMain - [ERROR]:[Bundle.entry[1]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
06:57:56.015 [main] WARN  hapisample.ParsingSampleMain - [ERROR]:[Bundle.entry[2]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
06:57:56.015 [main] WARN  hapisample.ParsingSampleMain - [ERROR]:[Bundle.entry[3]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
06:57:56.015 [main] WARN  hapisample.ParsingSampleMain - [ERROR]:[Bundle.entry[4]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
06:57:56.016 [main] WARN  hapisample.ParsingSampleMain - [ERROR]:[Bundle.entry[5]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
06:57:56.016 [main] WARN  hapisample.ParsingSampleMain - [ERROR]:[Bundle.entry[6]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
06:57:56.016 [main] WARN  hapisample.ParsingSampleMain - [ERROR]:[Bundle.entry[7]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
06:57:56.016 [main] WARN  hapisample.ParsingSampleMain - [ERROR]:[Bundle.entry[8]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
06:57:56.016 [main] WARN  hapisample.ParsingSampleMain - [ERROR]:[Bundle.entry[9]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
06:57:56.016 [main] WARN  hapisample.ParsingSampleMain - [ERROR]:[Bundle.entry[10]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
06:57:56.016 [main] WARN  hapisample.ParsingSampleMain - [ERROR]:[Bundle.entry[11]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
06:57:56.017 [main] WARN  hapisample.ParsingSampleMain - [ERROR]:[Bundle.entry[12]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
06:57:56.017 [main] WARN  hapisample.ParsingSampleMain - [ERROR]:[Bundle.entry[13]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
06:57:56.017 [main] WARN  hapisample.ParsingSampleMain - [ERROR]:[Bundle.entry[14]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
06:57:56.018 [main] WARN  hapisample.ParsingSampleMain - [ERROR]:[Bundle.entry[15]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
06:57:56.018 [main] WARN  hapisample.ParsingSampleMain - [ERROR]:[Bundle.entry[16]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
06:57:56.018 [main] WARN  hapisample.ParsingSampleMain - [ERROR]:[Bundle.entry[17]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
06:57:56.018 [main] WARN  hapisample.ParsingSampleMain - [ERROR]:[Bundle.entry[18]] Slicing cannot be evaluated: Profile based discriminators must have a type with a profile (Bundle.entry:bundleData.resource in profile http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
06:57:56.018 [main] WARN  hapisample.ParsingSampleMain - [INFORMATION]:[Bundle] Bundle.entry:composition: Found 0 matches, but unable to check minimum required (Bundle.entry) due to lack of slicing validation (from http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral|1.1.5)
06:57:56.018 [main] WARN  hapisample.ParsingSampleMain - [INFORMATION]:[Bundle.entry[0].resource.ofType(Composition).encounter] Details for urn:uuid:7cad1f19-3435-451d-9a71-a81b61f3358e matching against profile http://hl7.org/fhir/StructureDefinition/Encounter|4.0.1
06:57:56.018 [main] WARN  hapisample.ParsingSampleMain - [INFORMATION]:[Bundle.entry[0].resource.encounter] Details for urn:uuid:7cad1f19-3435-451d-9a71-a81b61f3358e matching against profile http://hl7.org/fhir/StructureDefinition/Encounter|4.0.1
06:57:56.019 [main] WARN  hapisample.ParsingSampleMain - [ERROR]:[Bundle.entry[0].resource.section[2].section[4].entry[0]] Unable to find a match for profile urn:uuid:711b07ae-d20b-40b0-9aa9-c7f1981409e6 among choices: http://jpfhir.jp/fhir/core/StructureDefinition/JP_AllergyIntolerance
06:57:56.019 [main] WARN  hapisample.ParsingSampleMain - [INFORMATION]:[Bundle.entry[0].resource.section[2].section[4].entry[0]] Details for urn:uuid:711b07ae-d20b-40b0-9aa9-c7f1981409e6 matching against profile http://jpfhir.jp/fhir/core/StructureDefinition/JP_AllergyIntolerance|1.1.1
06:57:56.019 [main] WARN  hapisample.ParsingSampleMain - [ERROR]:[Bundle.entry[0].resource.section[2].section[4].entry[1]] Unable to find a match for profile urn:uuid:5a1aae74-f720-415a-ba15-a6c8b5d4c5a1 among choices: http://jpfhir.jp/fhir/core/StructureDefinition/JP_AllergyIntolerance
06:57:56.019 [main] WARN  hapisample.ParsingSampleMain - [INFORMATION]:[Bundle.entry[0].resource.section[2].section[4].entry[1]] Details for urn:uuid:5a1aae74-f720-415a-ba15-a6c8b5d4c5a1 matching against profile http://jpfhir.jp/fhir/core/StructureDefinition/JP_AllergyIntolerance|1.1.1
06:57:56.019 [main] WARN  hapisample.ParsingSampleMain - [ERROR]:[Bundle.entry[0].resource.entry[2].resource.ofType(Encounter)] Encounter.reasonCode: minimum required = 1, but only found 0 (from http://jpfhir.jp/fhir/eClinicalSummary/StructureDefinition/JP_Encounter_eClinicalSummary|1.1.5)
06:57:56.020 [main] WARN  hapisample.ParsingSampleMain - [INFORMATION]:[Bundle.entry[15].resource.ofType(AllergyIntolerance).encounter] Details for urn:uuid:7cad1f19-3435-451d-9a71-a81b61f3358e matching against profile http://hl7.org/fhir/StructureDefinition/Encounter|4.0.1
06:57:56.020 [main] WARN  hapisample.ParsingSampleMain - [ERROR]:[Bundle.entry[0].resource.entry[15].resource.ofType(AllergyIntolerance).encounter] Unable to find a match for profile urn:uuid:7cad1f19-3435-451d-9a71-a81b61f3358e among choices: http://jpfhir.jp/fhir/core/StructureDefinition/JP_Encounter
06:57:56.020 [main] WARN  hapisample.ParsingSampleMain - [INFORMATION]:[Bundle.entry[0].resource.entry[15].resource.ofType(AllergyIntolerance).encounter] Details for urn:uuid:7cad1f19-3435-451d-9a71-a81b61f3358e matching against profile http://jpfhir.jp/fhir/core/StructureDefinition/JP_Encounter|1.1.1
06:57:56.020 [main] WARN  hapisample.ParsingSampleMain - [INFORMATION]:[Bundle.entry[16].resource.ofType(AllergyIntolerance).encounter] Details for urn:uuid:7cad1f19-3435-451d-9a71-a81b61f3358e matching against profile http://hl7.org/fhir/StructureDefinition/Encounter|4.0.1
06:57:56.020 [main] WARN  hapisample.ParsingSampleMain - [ERROR]:[Bundle.entry[0].resource.entry[16].resource.ofType(AllergyIntolerance).encounter] Unable to find a match for profile urn:uuid:7cad1f19-3435-451d-9a71-a81b61f3358e among choices: http://jpfhir.jp/fhir/core/StructureDefinition/JP_Encounter
06:57:56.020 [main] WARN  hapisample.ParsingSampleMain - [INFORMATION]:[Bundle.entry[0].resource.entry[16].resource.ofType(AllergyIntolerance).encounter] Details for urn:uuid:7cad1f19-3435-451d-9a71-a81b61f3358e matching against profile http://jpfhir.jp/fhir/core/StructureDefinition/JP_Encounter|1.1.1

# ここから、パース処理の結果。うまくいっている。
06:57:56.022 [main] INFO  hapisample.ParsingSampleMain - Bundle type:Document
06:57:56.024 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Composition
06:57:56.024 [main] INFO  hapisample.ParsingSampleMain - 文書名: 診療情報提供書
06:57:56.025 [main] INFO  hapisample.ParsingSampleMain - subject display: 患者リソースPatient
06:57:56.025 [main] INFO  hapisample.ParsingSampleMain - subject reference Id: urn:uuid:0a48a4bf-0d87-4efb-aafd-d45e0842a4dd
06:57:56.025 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Patient
06:57:56.025 [main] INFO  hapisample.ParsingSampleMain - Composition.subjectの参照先のPatient:urn:uuid:0a48a4bf-0d87-4efb-aafd-d45e0842a4dd
06:57:56.025 [main] INFO  hapisample.ParsingSampleMain - 患者番号:12345
06:57:56.026 [main] INFO  hapisample.ParsingSampleMain - 患者氏名:田中 太郎
06:57:56.027 [main] INFO  hapisample.ParsingSampleMain - 患者カナ氏名:タナカ タロウ
06:57:56.027 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Encounter
06:57:56.027 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Practitioner
06:57:56.027 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Practitioner
06:57:56.027 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Organization
06:57:56.027 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Organization
06:57:56.028 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Organization
06:57:56.028 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Organization
06:57:56.028 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Encounter
06:57:56.028 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Condition
06:57:56.028 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Condition
06:57:56.028 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Condition
06:57:56.029 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Condition
06:57:56.029 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Condition
06:57:56.029 [main] INFO  hapisample.ParsingSampleMain - Resource Type: AllergyIntolerance
06:57:56.029 [main] INFO  hapisample.ParsingSampleMain - Resource Type: AllergyIntolerance
06:57:56.029 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Observation
06:57:56.029 [main] INFO  hapisample.ParsingSampleMain - Resource Type: DocumentReference

```

そこで、エラー箇所を修正したサンプルデータおよび、修正したStructureDefinitionによる診療情報提供書のnpmパッケージを使用すると、バリデーションも正常終了します。
```sh
23:09:47.510 [main] INFO  ca.uhn.fhir.util.VersionUtil - HAPI FHIR version 6.4.4 - Rev 107a1bd073
23:09:47.520 [main] INFO  ca.uhn.fhir.context.FhirContext - Creating new FHIR context for FHIR version [R4]
23:09:48.915 [main] INFO  ca.uhn.fhir.util.XmlUtil - Unable to determine StAX implementation: java.xml/META-INF/MANIFEST.MF not found
23:09:51.862 [main] INFO  c.uhn.fhir.validation.FhirValidator - Ph-schematron library not found on classpath, will not attempt to perform schematron validation
23:09:51.974 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading structure definitions from classpath: /org/hl7/fhir/r4/model/profile/profiles-resources.xml
23:09:53.062 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading structure definitions from classpath: /org/hl7/fhir/r4/model/profile/profiles-types.xml
23:09:53.143 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading structure definitions from classpath: /org/hl7/fhir/r4/model/profile/profiles-others.xml
23:09:53.387 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading structure definitions from classpath: /org/hl7/fhir/r4/model/extension/extension-definitions.xml
23:09:55.228 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading CodeSystem/ValueSet from classpath: /org/hl7/fhir/r4/model/valueset/valuesets.xml
23:09:55.572 [main] WARN  c.u.fhir.parser.LenientErrorHandler - Unknown element 'author' found while parsing
23:09:55.574 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading CodeSystem/ValueSet from classpath: /org/hl7/fhir/r4/model/valueset/v2-tables.xml
23:09:55.913 [main] WARN  c.u.fhir.parser.LenientErrorHandler - Unknown element 'author' found while parsing
23:09:55.913 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading CodeSystem/ValueSet from classpath: /org/hl7/fhir/r4/model/valueset/v3-codesystems.xml
23:09:56.010 [main] WARN  c.u.fhir.parser.LenientErrorHandler - Unknown element 'author' found while parsing

# バリデーションの結果が正常に終了している
23:09:57.196 [main] INFO  hapisample.ParsingSampleMain - ドキュメントは有効です

# 以下、パース処理の結果は同様に正常終了
23:09:57.197 [main] INFO  hapisample.ParsingSampleMain - Bundle type:Document
23:09:57.199 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Composition
23:09:57.199 [main] INFO  hapisample.ParsingSampleMain - 文書名: 診療情報提供書
23:09:57.200 [main] INFO  hapisample.ParsingSampleMain - subject display: 患者リソースPatient
23:09:57.200 [main] INFO  hapisample.ParsingSampleMain - subject reference Id: urn:uuid:0a48a4bf-0d87-4efb-aafd-d45e0842a4dd
23:09:57.200 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Patient
23:09:57.200 [main] INFO  hapisample.ParsingSampleMain - Composition.subjectの参照先のPatient:urn:uuid:0a48a4bf-0d87-4efb-aafd-d45e0842a4dd
23:09:57.200 [main] INFO  hapisample.ParsingSampleMain - 患者番号:12345
23:09:57.201 [main] INFO  hapisample.ParsingSampleMain - 患者氏名:田中 太郎
23:09:57.202 [main] INFO  hapisample.ParsingSampleMain - 患者カナ氏名:タナカ タロウ
23:09:57.202 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Encounter
23:09:57.202 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Practitioner
23:09:57.202 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Practitioner
23:09:57.202 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Organization
23:09:57.202 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Organization
23:09:57.202 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Organization
23:09:57.203 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Organization
23:09:57.203 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Encounter
23:09:57.203 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Condition
23:09:57.203 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Condition
23:09:57.203 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Condition
23:09:57.203 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Condition
23:09:57.203 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Condition
23:09:57.203 [main] INFO  hapisample.ParsingSampleMain - Resource Type: AllergyIntolerance
23:09:57.204 [main] INFO  hapisample.ParsingSampleMain - Resource Type: AllergyIntolerance
23:09:57.204 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Observation
23:09:57.204 [main] INFO  hapisample.ParsingSampleMain - Resource Type: DocumentReference
```



## JSONシリアライズ実行結果の例
- [処方情報のFHIR記述仕様書](https://jpfhir.jp/fhir/ePrescriptionData/igv1/)に従い、JSON文字列のほんの一部分が生成出来てるのが分かります。

```sh
17:22:57.193 [main] INFO  ca.uhn.fhir.util.VersionUtil - HAPI FHIR version 6.4.4 - Rev 107a1bd073
17:22:57.201 [main] INFO  ca.uhn.fhir.context.FhirContext - Creating new FHIR context for FHIR version [R4]
17:22:58.771 [main] INFO  hapisample.SerializingSampleMain - 実行結果:
# 処方情報のFHIRのJSON文字列の一部を生成
{
  "resourceType": "Bundle",
  "meta": {
    "lastUpdated": "2023-06-18T17:22:57.163+09:00",
    "profile": [ "http://jpfhir.jp/fhir/ePrescription/StructureDefinition/JP_Bundle_ePrescriptionData" ]
  },
  "type": "document",
  "timestamp": "2023-06-18T17:22:57.163+09:00",
  "entry": [ {
    "fullUrl": "urn:uuid:f9b2e4a8-8ec1-441a-9cfc-a65d360fa18c",
    "resource": {
      "resourceType": "Composition",
      "id": "compositionReferralExample01Inline",
      "meta": {
        "lastUpdated": "2023-06-18T17:22:57.163+09:00",
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
        "reference": "urn:uuid:13ccb2b7-45ec-4e74-80e4-d61854a7a2f9"
      },
      "date": "2023-06-18T17:22:57+09:00",
      "title": "処方箋"
    }
  }, {
    "fullUrl": "urn:uuid:13ccb2b7-45ec-4e74-80e4-d61854a7a2f9",
    "resource": {
      "resourceType": "Patient",
      "meta": {
        "lastUpdated": "2023-06-18T17:22:57.163+09:00",
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
        "system": "http://jpfhir.jp/fhir/ccs/Idsysmem/JP_Insurance_member/00012345",
        "value": "00012345:あいう:１８７:05"
      } ],
      "name": [ {
        "extension": [ {
          "url": "http://hl7.org/fhir/StructureDefinition/iso21090-EN-representation",
          "valueString": "IDE"
        } ],
        "use": "official",
        "text": "東京　太郎",
        "family": "東京",
        "given": [ "太郎" ]
      }, {
        "extension": [ {
          "url": "http://hl7.org/fhir/StructureDefinition/iso21090-EN-representation",
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
