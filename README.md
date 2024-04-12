# HAPI FHIRのサンプル

- [HAPI FHIR](https://hapifhir.io/)を使って、[診療情報提供書HL7FHIR記述仕様](https://std.jpfhir.jp/)に基づくサンプルデータ（Bundle-BundleReferralExample01.json）に対して検証（FHIRバリデーション）し、Bundleリソースとしてパースするサンプルプログラムです。

- [FHIR IGポータル](https://std.jpfhir.jp/)のサイトから、公式バリデータを使った[バリデーションガイド](https://jpfhir.jp/fhir/eReferral/igv1/validationGuide.html)が公開されていますが、ここでは、[HAPI FHIR](https://hapifhir.io)を使って、同様のバリデーションを行うサンプルプログラムを作成しています。

- また、FHIRリソース(Bundle)として作成したオブジェクトを、FHIRのJSON文字列で出力（シリアライズ）するサンプルプログラムもあります。
    - [処方情報のFHIR記述仕様書](https://jpfhir.jp/fhir/ePrescriptionData/igv1/)に従い、JSONのほんの一部分を生成しています。

## プロファイルの検証（FHIRバリデーション）とパース
- FHIRベースプロファイルでの検証
    - [HAPI FHIRのバリデータの機能](https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html)を使用して、検証しています。
    - デフォルトの組み込みのバリデータである[DefaultProfileValidationSupport](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html#defaultprofilevalidationsupport)クラスにより、検証できます。

- JPCoreプロファイル、文書情報プロファイルでの検証
    - JPCore、診療情報提供書等の文書情報のプロファイル（実装ガイド）は、[FHIR package仕様](https://registry.fhir.org/learn)に従ったnpmパッケージ形式で提供されています。
    - npmパッケージには、diff形式とsnapshot形式の2つがありますが、通常は、FHIRが親のプロファイルを継承して定義される思想からdiff形式のパッケージを使いたいのですが、    - ですが、現状、diff形式を使うとエラーになってしまったので、ここでは、snapshot形式のパッケージを使って検証しています。
    - JPCoreのプロファイル
        - [JPCore実装ガイド](https://jpfhir.jp/fhir/core/)のサイトにJPCoreの実装ガイドとTerminologyのnpmパッケージがあります。
            - [JPCoreのnpmパッケージ(ver1.1.2) snapshot形式](https://jpfhir.jp/fhir/core/1.1.2/jp-core.r4-1.1.2-snap.tgz)                - 
            - [JPCoreのnpmパッケージ(ver1.1.2) diff形式](https://jpfhir.jp/fhir/core/1.1.2/jp-core.r4-1.1.2.tgz)                - 
            - [Terminologyのnpmパッケージ(ver1.1.1)](https://jpfhir.jp/fhir/core/terminology/jpfhir-terminology.r4-1.1.1.tgz)
    - 診療情報提供書の文書情報プロファイル
        - [診療情報提供書FHIR記述仕様実装ガイド](https://jpfhir.jp/fhir/eReferral/igv1/)にnpmパッケージがあります。
            - [診療情報提供のnpmパッケージ(ver1.1.6) snapshot形式](https://jpfhir.jp/fhir/eReferral/jp-eReferral.r4-1.1.6-snap.tgz)
            - [診療情報提供のnpmパッケージ(ver1.1.6) diff形式](https://jpfhir.jp/fhir/eReferral/jp-eReferral.r4-1.1.6.tgz)

        - 今回は、診療情報提供書ですが、退院時サマリー、健康診断結果報告書といった文書や、臨床情報（6情報、JP-CLINS）なども同様にnpmパッケージで提供されていますので、それらのnpmパッケージを使って検証することも可能です。

    - HAPIのバリデータでは、[NpmPackageValidationSupport](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html#npmpackagevalidationsupport)クラスにより、パッケージを読み込み、検証することができます。

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

```
08:23:56.046 [main] INFO  ca.uhn.fhir.util.VersionUtil - HAPI FHIR version 6.4.4 - Rev 107a1bd073
08:23:56.055 [main] INFO  ca.uhn.fhir.context.FhirContext - Creating new FHIR context for FHIR version [R4]
08:23:58.795 [main] INFO  ca.uhn.fhir.util.XmlUtil - Unable to determine StAX implementation: java.xml/META-INF/MANIFEST.MF not found
08:23:59.256 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:23:59.257 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:23:59.269 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:23:59.270 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:23:59.275 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:23:59.275 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:23:59.337 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="short"] Invalid attribute value "": Attribute value must not be empty ("")
08:23:59.343 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="short"] Invalid attribute value "": Attribute value must not be empty ("")
08:23:59.408 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:23:59.427 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:23:59.427 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:23:59.451 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:23:59.471 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:23:59.472 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:23:59.475 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:23:59.511 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:23:59.549 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:23:59.550 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="requirements"] Invalid attribute value "": Attribute value must not be empty ("")
08:23:59.550 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:23:59.551 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:23:59.551 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="requirements"] Invalid attribute value "": Attribute value must not be empty ("")
08:23:59.552 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:24:00.462 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:24:00.464 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:24:00.472 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:24:00.476 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:24:00.518 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:24:00.518 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="definition"] Invalid attribute value "": Attribute value must not be empty ("")
08:24:00.521 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:24:00.524 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:24:00.532 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:24:00.533 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="definition"] Invalid attribute value "": Attribute value must not be empty ("")
08:24:00.540 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:24:00.541 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="definition"] Invalid attribute value "": Attribute value must not be empty ("")
08:24:00.543 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:24:00.543 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="definition"] Invalid attribute value "": Attribute value must not be empty ("")
08:24:00.549 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:24:00.549 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:24:00.555 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:24:00.556 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:24:00.561 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:24:00.562 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:24:00.563 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:24:00.563 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:24:00.610 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:24:00.613 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:24:00.614 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:24:00.614 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:24:00.649 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:24:00.650 [main] WARN  c.u.fhir.parser.LenientErrorHandler - [element="comment"] Invalid attribute value "": Attribute value must not be empty ("")
08:24:05.313 [main] INFO  c.uhn.fhir.validation.FhirValidator - Ph-schematron library not found on classpath, will not attempt to perform schematron validation
08:24:05.388 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading structure definitions from classpath: /org/hl7/fhir/r4/model/profile/profiles-resources.xml
08:24:06.674 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading structure definitions from classpath: /org/hl7/fhir/r4/model/profile/profiles-types.xml
08:24:06.799 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading structure definitions from classpath: /org/hl7/fhir/r4/model/profile/profiles-others.xml
08:24:07.103 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading structure definitions from classpath: /org/hl7/fhir/r4/model/extension/extension-definitions.xml
08:24:09.217 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading CodeSystem/ValueSet from classpath: /org/hl7/fhir/r4/model/valueset/valuesets.xml
08:24:09.570 [main] WARN  c.u.fhir.parser.LenientErrorHandler - Unknown element 'author' found while parsing
08:24:09.571 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading CodeSystem/ValueSet from classpath: /org/hl7/fhir/r4/model/valueset/v2-tables.xml
08:24:09.844 [main] WARN  c.u.fhir.parser.LenientErrorHandler - Unknown element 'author' found while parsing
08:24:09.845 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading CodeSystem/ValueSet from classpath: /org/hl7/fhir/r4/model/valueset/v3-codesystems.xml
08:24:09.966 [main] WARN  c.u.fhir.parser.LenientErrorHandler - Unknown element 'author' found while parsing


# バリデーションの結果が正常に終了している
08:24:11.110 [main] INFO  hapisample.ParsingSampleMain - ドキュメントは有効です

# パース処理の結果も正常終了し、リソースの情報を取得できている
08:24:11.125 [main] INFO  hapisample.ParsingSampleMain - Bundle type:Document
08:24:11.128 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Composition
08:24:11.129 [main] INFO  hapisample.ParsingSampleMain - 文書名: 診療情報提供書
08:24:11.129 [main] INFO  hapisample.ParsingSampleMain - subject display: 患者リソースPatient
08:24:11.129 [main] INFO  hapisample.ParsingSampleMain - subject reference Id: urn:uuid:0a48a4bf-0d87-4efb-aafd-d45e0842a4dd
08:24:11.129 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Patient
08:24:11.129 [main] INFO  hapisample.ParsingSampleMain - Composition.subjectの参照先のPatient:urn:uuid:0a48a4bf-0d87-4efb-aafd-d45e0842a4dd
08:24:11.129 [main] INFO  hapisample.ParsingSampleMain - 患者番号:12345
08:24:11.130 [main] INFO  hapisample.ParsingSampleMain - 患者氏名:田中 太郎
08:24:11.131 [main] INFO  hapisample.ParsingSampleMain - 患者カナ氏名:タナカ タロウ
08:24:11.131 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Encounter
08:24:11.131 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Practitioner
08:24:11.131 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Practitioner
08:24:11.131 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Organization
08:24:11.131 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Organization
08:24:11.131 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Encounter
08:24:11.131 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Condition
08:24:11.131 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Condition
08:24:11.132 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Condition
08:24:11.132 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Condition
08:24:11.132 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Condition
08:24:11.132 [main] INFO  hapisample.ParsingSampleMain - Resource Type: AllergyIntolerance
08:24:11.132 [main] INFO  hapisample.ParsingSampleMain - Resource Type: AllergyIntolerance
08:24:11.132 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Observation
08:24:11.132 [main] INFO  hapisample.ParsingSampleMain - Resource Type: DocumentReference
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
