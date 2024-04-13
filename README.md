# HAPI FHIRのサンプル

- [HAPI FHIR](https://hapifhir.io/)を使って、[診療情報提供書HL7FHIR記述仕様](https://std.jpfhir.jp/)に基づくサンプルデータ（Bundle-BundleReferralExample01.json）に対して検証（FHIRバリデーション）し、Bundleリソースとしてパースするサンプルプログラムです。

- [FHIR IGポータル](https://std.jpfhir.jp/)のサイトから、公式バリデータを使った[バリデーションガイド](https://jpfhir.jp/fhir/eReferral/igv1/validationGuide.html)が公開されていますが、ここでは、[HAPI FHIR](https://hapifhir.io)を使って、同様のバリデーションを行うサンプルプログラムを作成しています。

- また、FHIRリソース(Bundle)として作成したオブジェクトを、FHIRのJSON文字列で出力（シリアライズ）するサンプルプログラムもあります。
    - [処方情報のFHIR記述仕様書](https://jpfhir.jp/fhir/ePrescriptionData/igv1/)に従い、JSONのほんの一部分を生成しています。

## 1. プロファイルの検証（FHIRバリデーション）とパース
### 1.1. FHIRバリデーション
- [HAPI FHIRのバリデータの機能](https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html)を使用して検証しています。
    - FHIRベースプロファイルでの検証
        - デフォルトの組み込みのバリデータである[DefaultProfileValidationSupport](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html#defaultprofilevalidationsupport)クラスにより、検証できます。

    - JPCoreプロファイル、文書情報プロファイルでの検証
        - JPCore、診療情報提供書等の文書情報のプロファイル（実装ガイド）は、[FHIR package仕様](https://registry.fhir.org/learn)に従ったnpmパッケージ形式で提供されています。
        - HAPIのバリデータでは、[NpmPackageValidationSupport](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html#npmpackagevalidationsupport)クラスにより、npmパッケージを読み込み、検証することができます。
            - NpmPackageValidationSupportクラスによる、パッケージを使ったバリデーションの実装方法については、[HAPI FHIRのドキュメントの「Validating Using Packages」](https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html#packages)を参考に実装しています。    
        - npmパッケージには、diff形式とsnapshot形式の2つがありますが、通常は、FHIRが親のプロファイルを継承して定義される思想からdiff形式のパッケージを使いたいのですが、diff形式を使うとエラーになってしまったので、現状はsnapshot形式のパッケージを使って検証しています。
        - JPCoreのプロファイル
            - [JPCore実装ガイド](https://jpfhir.jp/fhir/core/)のサイトにJPCoreの実装ガイドとTerminologyのnpmパッケージがあります。
                - JPCoreのnpmパッケージ(ver1.1.2)
                    - [snapshot形式](https://jpfhir.jp/fhir/core/1.1.2/jp-core.r4-1.1.2-snap.tgz)
                    - [diff形式](https://jpfhir.jp/fhir/core/1.1.2/jp-core.r4-1.1.2.tgz) 
                - [Terminologyのnpmパッケージ(ver1.1.1)](https://jpfhir.jp/fhir/core/terminology/jpfhir-terminology.r4-1.1.1.tgz)
        - 診療情報提供書の文書情報プロファイル
            - [診療情報提供書FHIR記述仕様実装ガイド](https://jpfhir.jp/fhir/eReferral/igv1/)にnpmパッケージがあります。
                - 診療情報提供のnpmパッケージ(ver1.1.6) 
                    - [snapshot形式](https://jpfhir.jp/fhir/eReferral/jp-eReferral.r4-1.1.6-snap.tgz)
                    - [diff形式](https://jpfhir.jp/fhir/eReferral/jp-eReferral.r4-1.1.6.tgz)

        - 今回は、診療情報提供書ですが、退院時サマリー、健康診断結果報告書といった文書や、臨床情報（6情報、JP-CLINS）なども同様にnpmパッケージで提供されていますので、それらのnpmパッケージを使って検証することも可能です。

### 1.2. FHIRデータのパース
- [HAPI FHIRのパーサ](https://hapifhir.io/hapi-fhir/docs/model/parsers.html)を使って、パースをしています。    

## 2. シリアライズ
- FHIRデータからJSONへのシリアライズ
    - パース同様、[HAPI FHIRのパーサ](https://hapifhir.io/hapi-fhir/docs/model/parsers.html)を使って、シリアライズをしています。  

## 3. 実行方法
- 検証・パースするサンプルAPの使い方
    - Java、Mavenでビルドし、「ParsingSampleMain」クラスを実行してください。
    - 通常は、Eclipse等のIDEを使って実行するのが簡単です。

- シリアライズするサンプルAPの使い方
    - Java、Mavenでビルドし、「SerializingSampleMain」クラスを実行してください。
    - 通常は、Eclipse等のIDEを使って実行するのが簡単です。

## 4. 実行結果
### 4.1 FHIRバリデーション・パース

#### 4.1.1 処理結果
```
09:24:45.679 [main] INFO  ca.uhn.fhir.util.VersionUtil - HAPI FHIR version 7.0.2 - Rev 95beaec894
09:24:45.689 [main] INFO  ca.uhn.fhir.context.FhirContext - Creating new FHIR context for FHIR version [R4]
09:24:48.514 [main] INFO  ca.uhn.fhir.util.XmlUtil - Unable to determine StAX implementation: java.xml/META-INF/MANIFEST.MF not found
09:24:55.059 [main] INFO  c.uhn.fhir.validation.FhirValidator - Ph-schematron library not found on classpath, will not attempt to perform schematron validation
09:24:55.062 [main] INFO  hapisample.ParsingSampleMain - バリデーション初回
09:24:55.154 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading structure definitions from classpath: /org/hl7/fhir/r4/model/profile/profiles-resources.xml
09:24:56.238 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading structure definitions from classpath: /org/hl7/fhir/r4/model/profile/profiles-types.xml
09:24:56.357 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading structure definitions from classpath: /org/hl7/fhir/r4/model/profile/profiles-others.xml
09:24:56.668 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading structure definitions from classpath: /org/hl7/fhir/r4/model/extension/extension-definitions.xml
09:24:58.670 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading CodeSystem/ValueSet from classpath: /org/hl7/fhir/r4/model/valueset/valuesets.xml
09:24:59.000 [main] WARN  c.u.fhir.parser.LenientErrorHandler - Unknown element 'author' found while parsing
09:24:59.001 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading CodeSystem/ValueSet from classpath: /org/hl7/fhir/r4/model/valueset/v2-tables.xml
09:24:59.252 [main] WARN  c.u.fhir.parser.LenientErrorHandler - Unknown element 'author' found while parsing
09:24:59.253 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading CodeSystem/ValueSet from classpath: /org/hl7/fhir/r4/model/valueset/v3-codesystems.xml
09:2a4:59.379 [main] WARN  c.u.fhir.parser.LenientErrorHandler - Unknown element 'author' found while parsing
09:25:00.425 [main] INFO  hapisample.ParsingSampleMain - バリデーション2回目
09:25:00.897 [main] INFO  hapisample.ParsingSampleMain - ドキュメントは有効です
09:25:00.908 [main] INFO  hapisample.ParsingSampleMain - Bundle type:Document
09:25:00.910 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Composition
09:25:00.911 [main] INFO  hapisample.ParsingSampleMain - 文書名: 診療情報提供書
09:25:00.911 [main] INFO  hapisample.ParsingSampleMain - subject display: 患者リソースPatient
09:25:00.911 [main] INFO  hapisample.ParsingSampleMain - subject reference Id: urn:uuid:0a48a4bf-0d87-4efb-aafd-d45e0842a4dd
09:25:00.911 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Patient
09:25:00.912 [main] INFO  hapisample.ParsingSampleMain - Composition.subjectの参照先のPatient:urn:uuid:0a48a4bf-0d87-4efb-aafd-d45e0842a4dd
09:25:00.912 [main] INFO  hapisample.ParsingSampleMain - 患者番号:12345
09:25:00.913 [main] INFO  hapisample.ParsingSampleMain - 患者氏名:田中 太郎
09:25:00.913 [main] INFO  hapisample.ParsingSampleMain - 患者カナ氏名:タナカ タロウ
09:25:00.913 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Encounter
09:25:00.913 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Practitioner
09:25:00.913 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Practitioner
09:25:00.913 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Organization
09:25:00.913 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Organization
09:25:00.914 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Encounter
09:25:00.914 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Condition
09:25:00.914 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Condition
09:25:00.914 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Condition
09:25:00.914 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Condition
09:25:00.914 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Condition
09:25:00.914 [main] INFO  hapisample.ParsingSampleMain - Resource Type: AllergyIntolerance
09:25:00.915 [main] INFO  hapisample.ParsingSampleMain - Resource Type: AllergyIntolerance
09:25:00.915 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Observation
09:25:00.915 [main] INFO  hapisample.ParsingSampleMain - Resource Type: DocumentReference
```

#### 4.1.2 処理結果
- Validator作成などの初期化、Validationの初回実行に時間がかかるのが分かる
- 2回目以降のValidation実行は、高速にできているのが分かる
    - HAPI FHIRのバージョン(7.0.2)の場合

    ```
    09:25:00.915 [main] INFO  hapisample.ParsingSampleMain - Context作成時間：40ms
    09:25:00.915 [main] INFO  hapisample.ParsingSampleMain - Validator作成時間：9371ms
    09:25:00.915 [main] INFO  hapisample.ParsingSampleMain - Validation処理時間（初回）：5364ms
    09:25:00.915 [main] INFO  hapisample.ParsingSampleMain - Validation処理時間（2回目）：471ms
    09:25:00.916 [main] INFO  hapisample.ParsingSampleMain - Parser作成時間：0ms
    09:25:00.916 [main] INFO  hapisample.ParsingSampleMain - Parse処理時間：10ms
    09:25:00.916 [main] INFO  hapisample.ParsingSampleMain - モデル処理時間：8ms
    ```

    - （参考）以前使っていた古いHAPI FHIRのバージョン(6.4.4)の処理時間
        - 6.x→7.xで処理時間はほぼ変わっていなさそう。

    ```
    09:26:56.212 [main] INFO  hapisample.ParsingSampleMain - Context作成時間：62ms
    09:26:56.213 [main] INFO  hapisample.ParsingSampleMain - Validator作成時間：9536ms
    09:26:56.213 [main] INFO  hapisample.ParsingSampleMain - Validation処理時間（初回）：5373ms
    09:26:56.213 [main] INFO  hapisample.ParsingSampleMain - Validation処理時間（2回目）：541ms
    09:26:56.213 [main] INFO  hapisample.ParsingSampleMain - Parser作成時間：0ms
    09:26:56.213 [main] INFO  hapisample.ParsingSampleMain - Parse処理時間：12ms
    09:26:56.214 [main] INFO  hapisample.ParsingSampleMain - モデル処理時間：7ms
    ```

### 4.2 シリアライズ
- [処方情報のFHIR記述仕様書](https://jpfhir.jp/fhir/ePrescriptionData/igv1/)に従い、処方情報のFHIR文書のJSON文字列のほんの一部分が生成出来てるのが分かります。

```sh
09:01:06.520 [main] INFO  ca.uhn.fhir.util.VersionUtil - HAPI FHIR version 7.0.2 - Rev 95beaec894
09:01:06.529 [main] INFO  ca.uhn.fhir.context.FhirContext - Creating new FHIR context for FHIR version [R4]
09:01:08.058 [main] INFO  hapisample.SerializingSampleMain - 実行結果:
{
  "resourceType": "Bundle",
  "meta": {
    "lastUpdated": "2024-04-13T09:01:06.489+09:00",
    "profile": [ "http://jpfhir.jp/fhir/ePrescription/StructureDefinition/JP_Bundle_ePrescriptionData" ]
  },
  "type": "document",
  "timestamp": "2024-04-13T09:01:06.489+09:00",
  "entry": [ {
    "fullUrl": "urn:uuid:a7f274ff-a212-4084-945f-ac87a329a8a2",
    "resource": {
      "resourceType": "Composition",
      "id": "compositionReferralExample01Inline",
      "meta": {
        "lastUpdated": "2024-04-13T09:01:06.489+09:00",
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
        "reference": "urn:uuid:1d830fe6-1b0f-43c7-a256-62988ea75539"
      },
      "date": "2024-04-13T09:01:06+09:00",
      "title": "処方箋"
    }
  }, {
    "fullUrl": "urn:uuid:1d830fe6-1b0f-43c7-a256-62988ea75539",
    "resource": {
      "resourceType": "Patient",
      "meta": {
        "lastUpdated": "2024-04-13T09:01:06.489+09:00",
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
