# HAPI FHIRのサンプル

- [HAPI FHIR](https://hapifhir.io/)を使って、[FHIR厚生労働省標準規格](https://std.jpfhir.jp/)に基づくサンプルデータに対して検証（FHIRバリデーション）し、Bundleリソースとしてパースするサンプルプログラムです。

- HAPI FHIRのバージョンは、7.6.0を使用しています。

- 各フォルダに、以下の2つのサンプルAPプロジェクトを作成しています。HAPIはJava11以上で動作すると書かれていますが、本サンプルは最新のLTSのJava21で作成しています。
    - simplehapiフォルダ
        - HAPIを理解するためのMain関数ですぐ実行できる簡単なJavaプログラム
        - 実行には[simplehapiフォルダのサンプルAP実行方法](#3-simplehapiフォルダのサンプルap実行方法)を参照してください。
    - springboot-hapiフォルダ
        - REST APIでFHIRバリデーションを実施する応用編のSpringBootアプリケーション
            - SpringBoot3.3で作成しており、SpringBoot3.xよりJava17以上が必要です。またSpringBoot3.2からはJava21を利用すると仮想スレッド機能が使用できることから、Java21を利用しています。
        - 実行には[springboot-hapiフォルダのSpringBootサンプルAP実行方法](#5-springboot-hapiフォルダのspringbootサンプルap実行方法)を参照してください。

- [FHIR IGポータル](https://std.jpfhir.jp/)のサイトから、公式バリデータを使った[バリデーションガイド](https://jpfhir.jp/fhir/eReferral/igv1/validationGuide.html)が公開されていますが、ここでは、[HAPI FHIR](https://hapifhir.io)を使って、同様のバリデーションを行うサンプルプログラムを作成しています。

    - ~~【注意】公式バリデータ（org.hl7.fhir.validation）の個別バージョンアップ~~
        - ~~HAPIのバリデーション機能（hapi-fhir-validaiton）は、内部で使用しているHL7が管理する公式バリデータ含む[HL7 FHIR Core Artifacts(org.hl7.fhir.core)](https://github.com/hapifhir/org.hl7.fhir.core)バージョンは、[hapi-fhir-validaitonのpom.xml](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-validation/pom.xml#L38)を見ると、${fhir_core_version}のプロパティで定義されており、[hapi-fhirの最上位プロジェクトのpom.xml](https://github.com/hapifhir/hapi-fhir/blob/v7.2.2/pom.xml#L928)でバージョン（現在は6.1.2.2）に統制されています。~~
            - ~~[開発中の最新版（7.5.0-SNAPSHOT）](https://github.com/hapifhir/hapi-fhir/blob/master/pom.xml#L941)は、6.3.18にバージョンアップされていますので、7.5.0系ではコアバージョンもアップされると思われます。~~

        - バリデーションガイドが使用している公式バリデータ（org.hl7.fhir.validation）のバージョンが6.1.8となっており、HAPIが利用するバージョンと齟齬があります。

        - ~~健診結果報告書のサンプルデータがうまく動作するよう、entry.resourceに複数のプロファイルがあるバンドルをバリデーションできなかった不具合に対応した[6.1.4](https://github.com/hapifhir/org.hl7.fhir.core/releases/tag/6.1.4)のバージョンで実行できるよう、このサンプルでは、HAPI FHIRが内部で使用するバージョンをバリデーションガイドでの公式バリデータのバージョンと合わせる個別対応を行っています。~~
            - ~~[pom.xml](simplehapi/pom.xml)~~
            - ~~[pom.xml](springboot-hapi/pom.xml)~~
        - ~~なお、同様の方法で、バリデーションガイドが使用している公式バリデータ（org.hl7.fhir.validation）のバージョン6.1.8にしてしまうと、APIの互換性がないため、実行時エラー（java.lang.NoSuchMethodError）が発生してしまいます。~~

> [!NOTE]
> HAPI ver7.4.0より、org.hl7.fhir.validationのバージョンは6.3.11に上がりました。  
> このため、上記のorg.hl7.fhir.validationの個別バージョンアップ対応は不要になりました。
> 現在、HAPI ver7.6.0では、org.hl7.fhir.validationのバージョンは6.4.0まで上がりました。  

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
        - npmパッケージには、diff形式とsnapshot形式の2つがありますが、通常は、FHIRが親のプロファイルを継承して定義される思想からdiff形式のパッケージを使います。ただし、下の問題が発生しています。
            1. JPCoreのnpmパッケージは、diff形式のパッケージの場合、[SnapshotGeneratingValidationSupport](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html#snapshotgeneratingvalidationsupport)による処理でjava.lang.OutOfMemoryErrorが発生するため、snapshot形式のパッケージを使っています。
            1. 新しいJP-CLINSのnpmパッケージは、snapshoto形式のパッケージを使用すると、バリデーション時にエラーが発生するのでdiff形式のパッケージを使用しています。
            
> [!NOTE]
> 診療情報提供書と退院時サマリーのプロファイルが、電子カルテ情報共有サービスのための実装ガイドJP-CLINSに統合されました。

- サンプルAPで利用する各種プロファイル
    - JPCoreのプロファイル
        - [JPCore実装ガイド](https://jpfhir.jp/fhir/core/)のサイトにJPCoreの実装ガイドがあります。
            - JPCoreのnpmパッケージ(ver1.1.2)
                - [snapshot形式](https://jpfhir.jp/fhir/core/1.1.2/jp-core.r4-1.1.2-snap.tgz)
                - [diff形式](https://jpfhir.jp/fhir/core/1.1.2/jp-core.r4-1.1.2.tgz)         
    - Terminologyのプロファイル
        - [JP-FHIR-Terminology](https://jpfhir.jp/fhir/core/terminology/ig/)のサイトにJP FHIR Terminologyの実装ガイドがあります。
            - [Terminologyのnpmパッケージ(ver1.2.0)](https://jpfhir.jp/fhir/core/terminology/jpfhir-terminology.r4-1.2.0.tgz)
    - JP-CLINS（電子カルテ情報共有サービス2文書5情報+患者サマリー　FHIR仕様）の文書情報プロファイル
        - [電子カルテ情報共有サービス2文書5情報+患者サマリー FHIR実装ガイド](https://jpfhir.jp/fhir/clins/igv1/)
            - JP-CLINSのnpmパッケージ(ver0.9.13)
                - [snapshot形式](https://jpfhir.jp/fhir/clins/jp-clins.r4-1.3.0-rc3.tgz)
                - [diff形式](https://jpfhir.jp/fhir/clins/jp-clins.r4-1.3.0-rc3-snap.tgz)
    - 健康診断診断結果報告書の文書情報プロファイル
        - [健康診断結果報告書FHIR記述仕様実装ガイド](https://jpfhir.jp/fhir/eCheckup/igv1/)
            - 健康診断結果報告書のnpmパッケージ(ver1.1.2)
                - [snapshot形式](https://jpfhir.jp/fhir/eCheckup/jp-eCheckupReport.r4-1.1.2-snap.tgz)
                - [diff形式](https://jpfhir.jp/fhir/eCheckup/jp-eCheckupReport.r4-1.1.2.tgz)

- サンプルデータ
  - [本実装ガイドに準拠したデータのサンプルの一覧](https://jpfhir.jp/fhir/clins/igv1.3-rc3/artifacts.html#example-example-instances)

### 1.2. FHIRデータのパース
- [HAPI FHIRのパーサ](https://hapifhir.io/hapi-fhir/docs/model/parsers.html)を使って、パースをしています。    

## 2. シリアライズ
- FHIRデータからJSONへのシリアライズ
    - パース同様、[HAPI FHIRのパーサ](https://hapifhir.io/hapi-fhir/docs/model/parsers.html)を使って、シリアライズをしています。  

## 3. simplehapiフォルダのサンプルAP実行方法
- 検証・パースするサンプルAPの使い方
    - 「simplehapi」フォルダの「[ParsingSampleMain](simplehapi/src/main/java/hapisample/ParsingSampleMain.java)」クラスを実行してください。
    - 「simplehapi」フォルダがMavenプロジェクトになっていますので、通常は、Eclipse等のIDEを使ってインポートし実行するのが簡単です。    

- シリアライズするサンプルAPの使い方
    - 「simplehapiフォルダ」の「[SerializingSampleMain](simplehapi/src/main/java/hapisample/SerializingSampleMain.java)」クラスを実行してください。
    - 「simplehapi」フォルダがMavenプロジェクトになっていますので、通常は、Eclipse等のIDEを使ってインポートし実行するのが簡単です。

## 4. simplehapiフォルダのサンプルAP実行結果
### 4.1 FHIRバリデーション・パース

#### 4.1.1 処理結果
```
21:15:19.187 [main] INFO  ca.uhn.fhir.util.VersionUtil - HAPI FHIR version 7.6.0 - Rev 90237f70ff
21:15:19.243 [main] INFO  ca.uhn.fhir.context.FhirContext - Creating new FHIR context for FHIR version [R4]
21:15:21.108 [main] INFO  ca.uhn.fhir.util.XmlUtil - Unable to determine StAX implementation: java.xml/META-INF/MANIFEST.MF not found
21:15:25.960 [main] INFO  c.uhn.fhir.validation.FhirValidator - Ph-schematron library not found on classpath, will not attempt to perform schematron validation
21:15:25.963 [main] INFO  hapisample.ParsingSampleMain - バリデーション初回
21:15:26.268 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading structure definitions from classpath: /org/hl7/fhir/r4/model/profile/profiles-resources.xml
21:15:27.528 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading structure definitions from classpath: /org/hl7/fhir/r4/model/profile/profiles-types.xml
21:15:27.616 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading structure definitions from classpath: /org/hl7/fhir/r4/model/profile/profiles-others.xml
21:15:27.900 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading structure definitions from classpath: /org/hl7/fhir/r4/model/extension/extension-definitions.xml
21:15:37.028 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading CodeSystem/ValueSet from classpath: /org/hl7/fhir/r4/model/valueset/valuesets.xml
21:15:37.410 [main] WARN  c.u.fhir.parser.LenientErrorHandler - Unknown element 'author' found while parsing
21:15:37.411 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading CodeSystem/ValueSet from classpath: /org/hl7/fhir/r4/model/valueset/v2-tables.xml
21:15:37.711 [main] WARN  c.u.fhir.parser.LenientErrorHandler - Unknown element 'author' found while parsing
21:15:37.712 [main] INFO  c.u.f.c.s.DefaultProfileValidationSupport - Loading CodeSystem/ValueSet from classpath: /org/hl7/fhir/r4/model/valueset/v3-codesystems.xml
21:15:37.842 [main] WARN  c.u.fhir.parser.LenientErrorHandler - Unknown element 'author' found while parsing
21:15:37.985 [main] INFO  o.h.f.c.h.v.s.CommonCodeSystemsTerminologyService - Loading BCP47 Language Registry
21:15:38.011 [main] INFO  o.h.f.c.h.v.s.CommonCodeSystemsTerminologyService - Have 8178 languages and 304 regions
21:15:39.731 [main] INFO  hapisample.ParsingSampleMain - バリデーション2回目
21:15:40.529 [main] INFO  hapisample.ParsingSampleMain - ドキュメントは有効です
21:15:40.580 [main] INFO  hapisample.ParsingSampleMain - Bundle type:Document
21:15:40.585 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Composition
21:15:40.587 [main] INFO  hapisample.ParsingSampleMain - 文書名: 診療情報提供書
21:15:40.587 [main] INFO  hapisample.ParsingSampleMain - subject display: 患者リソースPatient
21:15:40.587 [main] INFO  hapisample.ParsingSampleMain - subject reference Id: urn:uuid:0a48a4bf-0d87-4efb-aafd-d45e0842a4dd
21:15:40.588 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Patient
21:15:40.588 [main] INFO  hapisample.ParsingSampleMain - Composition.subjectの参照先のPatient:urn:uuid:0a48a4bf-0d87-4efb-aafd-d45e0842a4dd
21:15:40.589 [main] INFO  hapisample.ParsingSampleMain - 患者番号:000999739
21:15:40.590 [main] INFO  hapisample.ParsingSampleMain - 患者氏名:牧野 爛漫
21:15:40.590 [main] INFO  hapisample.ParsingSampleMain - 患者カナ氏名:マキノ ランマン
21:15:40.591 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Encounter
21:15:40.591 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Practitioner
21:15:40.591 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Practitioner
21:15:40.592 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Organization
21:15:40.592 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Organization
21:15:40.592 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Encounter
21:15:40.592 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Condition
21:15:40.592 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Condition
21:15:40.592 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Condition
21:15:40.593 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Condition
21:15:40.593 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Condition
21:15:40.593 [main] INFO  hapisample.ParsingSampleMain - Resource Type: AllergyIntolerance
21:15:40.593 [main] INFO  hapisample.ParsingSampleMain - Resource Type: AllergyIntolerance
21:15:40.593 [main] INFO  hapisample.ParsingSampleMain - Resource Type: Observation
21:15:40.593 [main] INFO  hapisample.ParsingSampleMain - Resource Type: DocumentReference
```

#### 4.1.2 処理時間
- Validator作成などの初期化、Validationの初回実行に時間がかかるのが分かります。
- 2回目以降のValidation実行は、高速にできているのが分かりますので、実際にアプリケーションを作成する際は一度ダミーデータでValidationを実行しておくと良いことが分かりました。
    - HAPI FHIRのバージョン(7.6.0)の場合
        - HAPI 7.4.0に比べて、Validation処理時間（2回目）が改善されているのが分かります。

    ```
    21:15:40.593 [main] INFO  hapisample.ParsingSampleMain - Context作成時間：125.595ms
    21:15:40.594 [main] INFO  hapisample.ParsingSampleMain - Validator作成時間：6718.957ms
    21:15:40.594 [main] INFO  hapisample.ParsingSampleMain - Validation処理時間（初回）：13768.648ms
    21:15:40.594 [main] INFO  hapisample.ParsingSampleMain - Validation処理時間（2回目）：797.877ms
    21:15:40.594 [main] INFO  hapisample.ParsingSampleMain - Parser作成時間：0.009ms
    21:15:40.595 [main] INFO  hapisample.ParsingSampleMain - Parse処理時間：49.198ms
    21:15:40.595 [main] INFO  hapisample.ParsingSampleMain - モデル処理時間：14.268ms
    ```

    - （比較参考）HAPI FHIRのバージョン(7.4.0)の場合
        - コアライブラリのバージョンアップ対応がされているためか、HAPI 7.2.2と比較して、Validatorの作成時間やParseの処理時間が短縮されているように見えます。
        - ただし、Validationの2回目実行時間は、かえって長くなっているように見えます。

    ```
    18:33:00.378 [main] INFO  hapisample.ParsingSampleMain - Context作成時間：53.866ms
    18:33:00.378 [main] INFO  hapisample.ParsingSampleMain - Validator作成時間：6415.631ms
    18:33:00.378 [main] INFO  hapisample.ParsingSampleMain - Validation処理時間（初回）：13955.223ms
    18:33:00.378 [main] INFO  hapisample.ParsingSampleMain - Validation処理時間（2回目）：1377.876ms
    18:33:00.379 [main] INFO  hapisample.ParsingSampleMain - Parser作成時間：0.008ms
    18:33:00.379 [main] INFO  hapisample.ParsingSampleMain - Parse処理時間：34.319ms
    18:33:00.379 [main] INFO  hapisample.ParsingSampleMain - モデル処理時間：9.392ms
    ```

    - （比較参考）HAPI FHIRのバージョン(7.2.2)の場合        

    ```
    21:51:18.277 [main] INFO  hapisample.ParsingSampleMain - Context作成時間：54.685ms
    21:51:18.277 [main] INFO  hapisample.ParsingSampleMain - Validator作成時間：9218.453ms
    21:51:18.277 [main] INFO  hapisample.ParsingSampleMain - Validation処理時間（初回）：13942.289ms
    21:51:18.277 [main] INFO  hapisample.ParsingSampleMain - Validation処理時間（2回目）：688.931ms
    21:51:18.278 [main] INFO  hapisample.ParsingSampleMain - Parser作成時間：0.007ms
    21:51:18.278 [main] INFO  hapisample.ParsingSampleMain - Parse処理時間：61.829ms
    21:51:18.278 [main] INFO  hapisample.ParsingSampleMain - モデル処理時間：12.737ms
    ```

### 4.2 シリアライズ
- [処方情報のFHIR記述仕様書](https://jpfhir.jp/fhir/ePrescriptionData/igv1/)に従い、処方情報のFHIR文書のJSON文字列のほんの一部分が生成出来てるのが分かります。
- このサンプルを応用すると、FHIRの形式変換や、FHIRデータの生成等ができることが分かります。

```sh
21:22:56.263 [main] INFO  ca.uhn.fhir.util.VersionUtil - HAPI FHIR version 7.6.0 - Rev 90237f70ff
21:22:56.272 [main] INFO  ca.uhn.fhir.context.FhirContext - Creating new FHIR context for FHIR version [R4]
21:22:57.821 [main] INFO  hapisample.SerializingSampleMain - 実行結果:
{
  "resourceType": "Bundle",
  "meta": {
    "lastUpdated": "2024-12-09T21:22:56.224+09:00",
    "profile": [ "http://jpfhir.jp/fhir/ePrescription/StructureDefinition/JP_Bundle_ePrescriptionData" ]
  },
  "type": "document",
  "timestamp": "2024-12-09T21:22:56.224+09:00",
  "entry": [ {
    "fullUrl": "urn:uuid:9a71d2e5-28aa-4bf5-abb6-4cf8c9575217",
    "resource": {
      "resourceType": "Composition",
      "id": "compositionReferralExample01Inline",
      "meta": {
        "lastUpdated": "2024-12-09T21:22:56.224+09:00",
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
        "reference": "urn:uuid:894de677-4f87-48f7-baee-7ebba19dbe2a"
      },
      "date": "2024-12-09T21:22:56+09:00",
      "title": "処方箋"
    }
  }, {
    "fullUrl": "urn:uuid:894de677-4f87-48f7-baee-7ebba19dbe2a",
    "resource": {
      "resourceType": "Patient",
      "meta": {
        "lastUpdated": "2024-12-09T21:22:56.224+09:00",
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

## 5. springboot-hapiフォルダのSpringBootサンプルAP実行方法
- サンプルAPの使い方
    - 「springboot-hapiフォルダ」はMavenプロジェクトになっています
    
    - 通常は、Eclipse(STS)等のIDEを使ってインポートし、「SpringBootHapiApplication」クラスをSpringBootアプリケーション（またはJavaアプリケーション）として実行するのが簡単です。  

    - Mavenで実行する際は、以下のコマンドを実行してください。        
    
    ```sh
    mvnw spring-boot:run
    ```

## 6. springboot-hapiフォルダのSpringBootサンプルAP実行結果

- REST APIの呼び出し
    - curlコマンド等で、以下のコマンドを呼び出します。テスト用FHIRデータを送信し、バリデーション結果を取得します。

    - 診療情報提供書のFHIRデータを送信する場合

    ```sh
    # curlコマンド実行
    cd springboot-hapi
    curl -H "Content-Type: application/json" -d @src\main\resources\file\Bundle-Bundle-CLINS-Referral-Example-01.json http://localhost:8080/api/v1/fhir/validate/clins

    # 正常応答
    {"result":"OK"}

    # エラー応答の例 （Bundle-BundleReferralExample01.jsonを書き換えてエラーが出るようにした場合）
    {
        "result": "NG",
        "details":[
            "[ERROR]:[Bundle] Rule bdl-3: 'Entry.Requestバッチ/トランザクション/履歴に必須、それ以外の場合は禁止されています / entry.request mandatory for batch/transaction/history, otherwise prohibited' Failed",
            "[ERROR]:[Bundle] Rule bdl-4: 'Batch-Response/Transaction-Response/historyに必須であり、それ以外の場合は禁止されています / entry.response mandatory for batch-response/transaction-response/history, otherwise prohibited' Failed",
            "[ERROR]:[Bundle] Rule bdl-12: 'メッセージには最初のリソースとしてメッセージヘッダーが必要です / A message must have a MessageHeader as the first resource' Failed",
            "[ERROR]:[Bundle] Bundle.type: minimum required = 1, but only found 0 (from http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral)"
        ]
    }

    ```

    - APログ
        - AP起動時に、HAPIのFHIRContext、FHIRValidatorの作成、バリデーション暖機処理実行を行い、高速にバリデーションを実行できるようになっていることが分かります。
            - SpringBootのBean定義によるHAPIのFHIRContext、FHIRValidatorの作成を行っているため、AP起動時に初期化処理が行われます。
                - [src/main/java/com/example/hapisample/FhirConfig.java](springboot-hapi/src/main/java/com/example/hapisample/FhirConfig.java)
            - バリデーション暖機処理実行は、AP起動時に@PostConstructに記載した処理でダミーデータで暖機処理を実行しています。
                - [springboot-hapi/src/main/java/com/example/hapisample/domain/service/FhirValidationServiceImpl.java](springboot-hapi/src/main/java/com/example/hapisample/domain/service/FhirValidationServiceImpl.java#L40-L48)


    ```
    2024-12-09T21:52:40.876+09:00  INFO 18124 --- [demo] [restartedMain] c.e.h.SpringBootHapiApplication          : Starting SpringBootHapiApplication using Java 21.0.3 with PID 18124 (D:\git\hapisample\springboot-hapi\target\classes started by dell in D:\git\hapisample\springboot-hapi)
    2024-12-09T21:52:40.878+09:00 DEBUG 18124 --- [demo] [restartedMain] c.e.h.SpringBootHapiApplication          : Running with Spring Boot v3.3.2, Spring v6.1.11
    2024-12-09T21:52:40.879+09:00  INFO 18124 --- [demo] [restartedMain] c.e.h.SpringBootHapiApplication          : The following 2 profiles are active: "dev", "log_default"    
    …
    2024-12-09T21:52:42.090+09:00  INFO 18124 --- [demo] [restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
    2024-12-09T21:52:42.103+09:00  INFO 18124 --- [demo] [restartedMain] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
    2024-12-09T21:52:42.104+09:00  INFO 18124 --- [demo] [restartedMain] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.26]
    2024-12-09T21:52:42.182+09:00  INFO 18124 --- [demo] [restartedMain] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
    2024-12-09T21:52:42.183+09:00  INFO 18124 --- [demo] [restartedMain] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 1247 ms
 
    # Bean定義によるHAPIのFHIRContextの作成
    2024-12-09T21:52:42.265+09:00  INFO 18124 --- [demo] [restartedMain] ca.uhn.fhir.util.VersionUtil             : HAPI FHIR version 7.6.0 - Rev 90237f70ff
    2024-12-09T21:52:42.272+09:00  INFO 18124 --- [demo] [restartedMain] ca.uhn.fhir.context.FhirContext          : Creating new FHIR context for FHIR version [R4]
    2024-12-09T21:52:42.273+09:00  INFO 18124 --- [demo] [restartedMain] com.example.hapisample.FhirConfig        : FHIRContext作成：29.669ms
    
    # Bean定義によるHAPIのFHIRValidatorの作成（プロファイルの読み込み等があるため、かなり時間がかかっているが、HAPI 7.2.2の時はJP-CLINSのValidator作成、健康診断結果報告書のValidator作成、それぞれに20秒程度かかっていたのが、10秒程度に短縮されている）
    2024-12-09T21:52:43.302+09:00  INFO 18124 --- [demo] [restartedMain] ca.uhn.fhir.util.XmlUtil                 : Unable to determine StAX implementation: java.xml/META-INF/MANIFEST.MF not found
    2024-12-09T21:52:54.374+09:00  INFO 18124 --- [demo] [restartedMain] ca.uhn.fhir.validation.FhirValidator     : Ph-schematron library not found on classpath, will not attempt to perform schematron validation
    2024-12-09T21:52:54.376+09:00  INFO 18124 --- [demo] [restartedMain] com.example.hapisample.FhirConfig        : JP-CLINS FHIRValidator作成：12085.468ms
    2024-12-09T21:53:04.487+09:00  INFO 18124 --- [demo] [restartedMain] com.example.hapisample.FhirConfig        : 健康診断結果報告書FHIRValidator作成：10108.402ms

    # バリデーション処理は初回実行時だけ時間がかかるため、AP起動時あらかじめ@PostConstructに記載した処理でダミーデータで暖機処理実行
    2024-12-09T21:53:04.495+09:00 DEBUG 18124 --- [demo] [restartedMain] c.e.h.d.s.FhirValidationServiceImpl      : バリデーション暖機処理実行開始
    2024-12-09T21:53:04.574+09:00  INFO 18124 --- [demo] [restartedMain] .u.f.c.s.DefaultProfileValidationSupport : Loading structure definitions from classpath: /org/hl7/fhir/r4/model/profile/profiles-resources.xml
    2024-12-09T21:53:07.848+09:00  INFO 18124 --- [demo] [restartedMain] .u.f.c.s.DefaultProfileValidationSupport : Loading structure definitions from classpath: /org/hl7/fhir/r4/model/profile/profiles-types.xml
    2024-12-09T21:53:08.089+09:00  INFO 18124 --- [demo] [restartedMain] .u.f.c.s.DefaultProfileValidationSupport : Loading structure definitions from classpath: /org/hl7/fhir/r4/model/profile/profiles-others.xml
    2024-12-09T21:53:09.066+09:00  INFO 18124 --- [demo] [restartedMain] .u.f.c.s.DefaultProfileValidationSupport : Loading structure definitions from classpath: /org/hl7/fhir/r4/model/extension/extension-definitions.xml
    2024-12-09T21:53:48.128+09:00  INFO 18124 --- [demo] [restartedMain] .u.f.c.s.DefaultProfileValidationSupport : Loading CodeSystem/ValueSet from classpath: /org/hl7/fhir/r4/model/valueset/valuesets.xml
    2024-12-09T21:53:49.309+09:00  WARN 18124 --- [demo] [restartedMain] ca.uhn.fhir.parser.LenientErrorHandler   : Unknown element 'author' found while parsing
    2024-12-09T21:53:49.309+09:00  INFO 18124 --- [demo] [restartedMain] .u.f.c.s.DefaultProfileValidationSupport : Loading CodeSystem/ValueSet from classpath: /org/hl7/fhir/r4/model/valueset/v2-tables.xml
    2024-12-09T21:53:50.204+09:00  WARN 18124 --- [demo] [restartedMain] ca.uhn.fhir.parser.LenientErrorHandler   : Unknown element 'author' found while parsing
    2024-12-09T21:53:50.205+09:00  INFO 18124 --- [demo] [restartedMain] .u.f.c.s.DefaultProfileValidationSupport : Loading CodeSystem/ValueSet from classpath: /org/hl7/fhir/r4/model/valueset/v3-codesystems.xml
    2024-12-09T21:53:50.675+09:00  WARN 18124 --- [demo] [restartedMain] ca.uhn.fhir.parser.LenientErrorHandler   : Unknown element 'author' found while parsing
    2024-12-09T21:53:50.728+09:00  INFO 18124 --- [demo] [restartedMain] .v.s.CommonCodeSystemsTerminologyService : Loading BCP47 Language Registry
    2024-12-09T21:53:50.789+09:00  INFO 18124 --- [demo] [restartedMain] .v.s.CommonCodeSystemsTerminologyService : Have 8178 languages and 304 regions

    # JP-CLINSの暖機処理（JP-CLINSの場合46秒と、かなり時間がかかっている）
    2024-12-09T21:53:54.445+09:00  INFO 18124 --- [demo] [restartedMain] c.e.h.d.s.FhirValidationServiceImpl      : バリデーション暖機処理実行完了：49948.38ms
    2024-12-09T21:53:54.446+09:00 DEBUG 18124 --- [demo] [restartedMain] c.e.h.d.s.FhirValidationServiceImpl      : バリデーション暖機処理実行開始
    2024-12-09T21:53:56.228+09:00  INFO 18124 --- [demo] [restartedMain] .v.s.CommonCodeSystemsTerminologyService : Loading BCP47 Language Registry
    2024-12-09T21:53:56.289+09:00  INFO 18124 --- [demo] [restartedMain] .v.s.CommonCodeSystemsTerminologyService : Have 8178 languages and 304 regions

    # 健康診断結果報告書の暖機処理     
    2024-12-09T21:53:57.722+09:00  INFO 18124 --- [demo] [restartedMain] c.e.h.d.s.FhirValidationServiceImpl      : バリデーション暖機処理実行完了：3275.399ms

    # 上のHAPIの初期化に時間がかかるので、Spring Boot起動までに78秒程度かかっている（HAPI 7.2.2の時は、92秒程度かかっていたので、短縮されている）
    2024-12-09T21:53:58.106+09:00  INFO 18124 --- [demo] [restartedMain] o.s.b.d.a.OptionalLiveReloadServer       : LiveReload server is running on port 35729
    2024-12-09T21:53:58.150+09:00  INFO 18124 --- [demo] [restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
    2024-12-09T21:53:58.159+09:00  INFO 18124 --- [demo] [restartedMain] c.e.h.SpringBootHapiApplication          : Started SpringBootHapiApplication in 77.925 seconds (process running for 78.862)

    …
    ```

    - バリデーション処理時間
        - HAPI7.6.0では、7.2.2→7.4.0での遅くなった問題が改善されて、バリデーション処理時間が短縮されていることが分かります。
    
    ```
    2024-12-09T21:43:36.957+09:00  INFO 18720 --- [demo] [tomcat-handler-1] c.e.h.d.s.FhirValidationServiceImpl      : バリデーション実行完了：1129.447ms
    2024-12-09T21:43:36.958+09:00  INFO 18720 --- [demo] [tomcat-handler-1] c.e.h.d.s.FhirValidationServiceImpl      : ドキュメントは有効です
    2024-12-09T21:43:37.922+09:00 DEBUG 18720 --- [demo] [tomcat-handler-3] c.e.h.d.s.FhirValidationServiceImpl      : FHIRバリデーション開始[FHIRバージョン 4.0.1]
    2024-12-09T21:43:38.431+09:00  INFO 18720 --- [demo] [tomcat-handler-3] c.e.h.d.s.FhirValidationServiceImpl      : バリデーション実行完了：508.701ms
    2024-12-09T21:43:38.431+09:00  INFO 18720 --- [demo] [tomcat-handler-3] c.e.h.d.s.FhirValidationServiceImpl      : ドキュメントは有効です
    2024-12-09T21:43:44.974+09:00 DEBUG 18720 --- [demo] [tomcat-handler-5] c.e.h.d.s.FhirValidationServiceImpl      : FHIRバリデーション開始[FHIRバージョン 4.0.1]
    2024-12-09T21:43:45.508+09:00  INFO 18720 --- [demo] [tomcat-handler-5] c.e.h.d.s.FhirValidationServiceImpl      : バリデーション実行完了：533.92ms
    2024-12-09T21:43:45.508+09:00  INFO 18720 --- [demo] [tomcat-handler-5] c.e.h.d.s.FhirValidationServiceImpl      : ドキュメントは有効です 
    ```    


    - （比較参考）HAPI FHIRのバージョン(7.4.0)の場合
        - HAPI7.4.0では、あまり高速に処理できず、1.5秒前後かかっている。
            - HAPI7.4.0では、コアライブラリのバージョンアップ等、内部の実装が大きく変わったせいか、データによっては遅くなるケースが出ているようです。
            - 実際、臨床情報は、数100ms程度で返却されたが、健診結果報告書は、4秒以上かかるようになってしまっていました。

    ```    
    2024-08-31T18:48:41.330+09:00 DEBUG 7740 --- [demo] [tomcat-handler-0] c.e.h.d.s.FhirValidationServiceImpl      : FHIRバリデーション開始[FHIRバージョン 4.0.1]
    2024-08-31T18:48:43.306+09:00  INFO 7740 --- [demo] [tomcat-handler-0] c.e.h.d.s.FhirValidationServiceImpl      : バリデーション実行完了：1974.671ms
    2024-08-31T18:48:43.306+09:00  INFO 7740 --- [demo] [tomcat-handler-0] c.e.h.d.s.FhirValidationServiceImpl      : ドキュメントは有効です
    2024-08-31T18:48:45.530+09:00 DEBUG 7740 --- [demo] [tomcat-handler-1] c.e.h.d.s.FhirValidationServiceImpl      : FHIRバリデーション開始[FHIRバージョン 4.0.1]
    2024-08-31T18:48:47.068+09:00  INFO 7740 --- [demo] [tomcat-handler-1] c.e.h.d.s.FhirValidationServiceImpl      : バリデーション実行完了：1537.248ms
    2024-08-31T18:48:47.069+09:00  INFO 7740 --- [demo] [tomcat-handler-1] c.e.h.d.s.FhirValidationServiceImpl      : ドキュメントは有効です
    2024-08-31T18:48:48.246+09:00 DEBUG 7740 --- [demo] [tomcat-handler-3] c.e.h.d.s.FhirValidationServiceImpl      : FHIRバリデーション開始[FHIRバージョン 4.0.1]
    2024-08-31T18:48:49.898+09:00  INFO 7740 --- [demo] [tomcat-handler-3] c.e.h.d.s.FhirValidationServiceImpl      : バリデーション実行完了：1652.509ms
    2024-08-31T18:48:49.899+09:00  INFO 7740 --- [demo] [tomcat-handler-3] c.e.h.d.s.FhirValidationServiceImpl      : ドキュメントは有効です  
    ```    

    - （比較参考）HAPI FHIRのバージョン(7.2.2)の場合
        - 診療情報提供書のFHIRデータは、バリデーション処理の実行（正常終了）500ms前後と、比較的高速に処理されていた。

    ```    
    2024-07-28T23:12:55.323+09:00 DEBUG 7820 --- [demo] [tomcat-handler-0] c.e.h.d.s.FhirValidationServiceImpl      : FHIRバリデーション開始[FHIRバージョン 4.0.1]
    2024-07-28T23:12:56.430+09:00  INFO 7820 --- [demo] [tomcat-handler-0] c.e.h.d.s.FhirValidationServiceImpl      : バリデーション実行完了：1106.683ms
    2024-07-28T23:12:56.431+09:00  INFO 7820 --- [demo] [tomcat-handler-0] c.e.h.d.s.FhirValidationServiceImpl      : ドキュメントは有効です
    2024-07-28T23:12:57.676+09:00 DEBUG 7820 --- [demo] [tomcat-handler-1] c.e.h.d.s.FhirValidationServiceImpl      : FHIRバリデーション開始[FHIRバージョン 4.0.1]
    2024-07-28T23:12:58.221+09:00  INFO 7820 --- [demo] [tomcat-handler-1] c.e.h.d.s.FhirValidationServiceImpl      : バリデーション実行完了：545.552ms
    2024-07-28T23:12:58.221+09:00  INFO 7820 --- [demo] [tomcat-handler-1] c.e.h.d.s.FhirValidationServiceImpl      : ドキュメントは有効です
    2024-07-28T23:12:59.375+09:00 DEBUG 7820 --- [demo] [tomcat-handler-3] c.e.h.d.s.FhirValidationServiceImpl      : FHIRバリデーション開始[FHIRバージョン 4.0.1]
    2024-07-28T23:12:59.953+09:00  INFO 7820 --- [demo] [tomcat-handler-3] c.e.h.d.s.FhirValidationServiceImpl      : バリデーション実行完了：578.144ms
    2024-07-28T23:12:59.953+09:00  INFO 7820 --- [demo] [tomcat-handler-3] c.e.h.d.s.FhirValidationServiceImpl      : ドキュメントは有効です
    ```        

    - 臨床情報（5情報）のFHIRデータを送信する場合

    ```sh
    # curlコマンド実行
    curl -H "Content-Type: application/json" -d @src\main\resources\file\Bundle-Bundle-CLINS-PCS-Example-01.json http://localhost:8080/api/v1/fhir/validate/clins

    # このサンプルデータは、HAPI 7.2.2だとバリデーションエラーが出ていただ、HAPI 7.4.0だとバリデーションが正常になるようになった
    curl -H "Content-Type: application/json" -d @src\main\resources\file\Bundle-Bundle-CLINS-Observations-Example-01.json http://localhost:8080/api/v1/fhir/validate/clins    

    # 実行結果は省略    
    ```
  
    - 健康診断結果報告書のFHIRデータを送信する場合

    ```sh
    # curlコマンド実行
    curl -H "Content-Type: application/json" -d @src\main\resources\file\Bundle-Bundle-eCheckupReport-Sample-01.json http://localhost:8080/api/v1/fhir/validate/checkup-report

    # 実行結果は省略    
    ``` 

## 7. SpringBootサンプルAPでのFHIRバリデーションの回帰テスト自動化の例
- FHIRプロファイルの改訂、HAPIのバージョンアップ等の際、FHIRバリデーションが以前と変わりなく同じように動作すること確認する回帰テストが自動でできる仕組みが必要になることが想像されます。
- 以下のテストコードは、SpringBootを起動せずに、JUnit5のパラメタライズドテストで、複数のテストケースに対して、繰り返しFHIRバリデーションのロジックだけを高速にテストする例です。CI/CDパイプラインに組み込めば、FHIRプロファイルの改訂、HAPIのバージョンアップ等の際に、バリデーションのロジックのみを高速に自動回帰テストできるようになります。
    - [FhirValidationRegressionTest.java](springboot-hapi/src/test/java/com/example/hapisample/FhirValidationRegressionTest.java)

## 8. FHIRバリデーションのマルチスレッドテスト

> [!WARNING]
> HAPI 7.4.0にバージョンアップして以降、本テストコードの実行に失敗することから、マルチスレッド下での実行結果がスレッドによって異なる結果が返却される不具合がある可能性があります。
> 最新のHAPI 7.6.0でも同様の事象があり、HAPIの利用には注意が必要です。

- 以下のテストコードを使うと、マルチスレッド化でのFHIRバリデーションの実行が正しいかのテストができる
    - SpringBootサンプルAPでのFHIRバリデーションのマルチスレッドテスト
        - [FhirValidationMultiThreadTest.java](springboot-hapi/src/test/java/com/example/hapisample/FhirValidationMultiThreadTest.java)    
    - 簡単なHAPIサンプルAPでのFHIRバリデーションのマルチスレッド実行のサンプルコード
        - [ValidationInMultiThreads.java](simplehapi/src/main/java/hapisample/ValidationInMultiThreads.java)        
    - 現状、HAPI 7.4.0以降にバージョンアップすると、テストに失敗、マルチスレッド化での実行結果が、スレッドによって異なる結果が返却されることがあり、不具合がある可能性がある。
        - HAPI 7.2.2の時には同じテストでの問題が発生しなかったため、HAPIのそれ以降のバージョンからの何らかの変更により、マルチスレッド化での実行に問題がある可能性がある。
        - マルチスレッド下でいきなりValidatorを実行すると、SnapshotGeneratingValidationSupportでjava.util.ConcurrentModificationExceptionのエラーが発生するので、これも関係している可能性がある。