# HAPI FHIRのサンプル

- [HAPI FHIR](https://hapifhir.io/)を使って、[診療情報提供書HL7FHIR記述仕様](https://std.jpfhir.jp/)に基づくサンプルデータ（Bundle-BundleReferralExample01.json）に対して検証（FHIRバリデーション）し、Bundleリソースとしてパースするサンプルプログラムです。

- HAPI FHIRのバージョンは、7.0.2を使用しています。

- 各フォルダに、以下の2つのサンプルAPプロジェクトを作成しています。
    - simplehapiフォルダ
        - HAPIを理解するためのMain関数ですぐ実行できる簡単なJavaプログラム
        - 実行には[simplehapiフォルダのサンプルAP実行方法](#4-simplehapiフォルダのサンプルap実行結果)を参照してください。
    - springboot-hapiフォルダ
        - REST APIでFHIRバリデーションを実施する応用編のSpringBootアプリケーション
        - 実行には[springboot-hapiフォルダのSpringBootサンプルAP実行方法](#5-springboot-hapiフォルダのspringbootサンプルap実行方法)を参照してください。

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
        - npmパッケージには、diff形式とsnapshot形式の2つがありますが、通常は、FHIRが親のプロファイルを継承して定義される思想からdiff形式のパッケージを使いたいのですが、以下の2点の理由によりsnapshot形式のパッケージを使って実行しています。
            1. JPCoreのnpmパッケージは、diff形式のパッケージが提供されているが、[SnapshotGeneratingValidationSupport](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html#snapshotgeneratingvalidationsupport)による処理でjava.lang.OutOfMemoryErrorが発生する。
            1. HAPIのValidatorは、R5以前のバージョンも動作するように下位互換性が担保されている作りとなっているが、実装上、内部ではFHIRのR5のデータ構造に変換して処理する。このため、R4のプロファイルを利用する場合に、バリデーション実行時に、StructureDefinitionやValueSet、CodeSystem等の定義情報を参照する際、都度R4からR5のデータ構造へ変換するための処理が発生し、オーバヘッドになることがある。SnapshotGeneratingValidationSupportを使った場合この処理が多く発生するため、処理が遅くなることがある。SnapshotGeneratingValidationSupportは、diff形式のパッケージに対してValidation実行時にSnapshot形式の定義情報を自動生成するクラスであるため、全てsnapshot形式のパッケージを使う場合は、SnapshotGeneratingValidationSupportを使わなくて済む。
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

## 3. simplehapiフォルダのサンプルAP実行方法
- 検証・パースするサンプルAPの使い方
    - 「simplehapi」フォルダの「ParsingSampleMain」クラスを実行してください。
    - 「simplehapi」フォルダがMavenプロジェクトになっていますので、通常は、Eclipse等のIDEを使ってインポートし実行するのが簡単です。    

- シリアライズするサンプルAPの使い方
    - 「simplehapiフォルダ」の「SerializingSampleMain」クラスを実行してください。
    - 「simplehapi」フォルダがMavenプロジェクトになっていますので、通常は、Eclipse等のIDEを使ってインポートし実行するのが簡単です。

## 4. simplehapiフォルダのサンプルAP実行結果
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

#### 4.1.2 処理時間
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

## 5. springboot-hapiフォルダのSpringBootサンプルAP実行方法
- サンプルAPの使い方
    - 「springboot-hapiフォルダ」はMavenプロジェクトになっていますので、 通常は、Eclipse(STS)等のIDEを使ってインポートし、「SpringBootHapiApplication」クラスをSpringBootアプリケーションとして実行するのが簡単です。  

    - もしMavenで実行する際は、以下のコマンドを実行してください。
        - Mavenでビルド
        ```sh
        cd springboot-hapi
        mvnw clean package
        
        # Windowsの場合
        mvnw.cmd clean package
        ```
        - SpringBootアプリケーションの実行
        ```sh
        mvnw spring-boot:run

        # Windowsの場合
        mvnw.cmd spring-boot:run
        ```

## 6. springboot-hapiフォルダのSpringBootサンプルAP実行結果

- REST APIの呼び出し
    - curlコマンド等で、以下のコマンドを呼び出します。テスト用FHIRデータを送信し、バリデーション結果を取得します。

    ```sh
    # curlコマンド実行
    cd springboot-hapi
    curl -H "Content-Type: application/json" -d @src\main\resources\file\Bundle-BundleReferralExample01.json http://localhost:8080/api/v1/fhir

    # 正常応答
    OK

    # Bundle-BundleReferralExample01.jsonを書き換えてエラーが出るようにした場合の応答例

    [ERROR]:[Bundle] Rule bdl-3: 'Entry.Requestバッチ/トランザクション/履歴に必須、それ以外の場合は禁止されています / entry.request mandatory for batch/transaction/history, otherwise prohibited' Failed
    [ERROR]:[Bundle] Rule bdl-4: 'Batch-Response/Transaction-Response/historyに必須であり、それ以外の場合は禁止されています / entry.response mandatory for batch-response/transaction-response/history, otherwise prohibited' Failed
    [ERROR]:[Bundle] Rule bdl-12: 'メッセージには最初のリソースとしてメッセージヘッダーが必要です / A message must have a MessageHeader as the first resource' Failed
    [ERROR]:[Bundle] Bundle.type: minimum required = 1, but only found 0 (from http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Bundle_eReferral)

    ```

    - APログ
    ```
    2024-04-18T23:49:09.321+09:00  INFO 29428 --- [demo] [restartedMain] c.e.h.SpringBootHapiApplication          : Starting SpringBootHapiApplication using Java 21.0.2 with PID 29428 …
    2024-04-18T23:49:09.323+09:00 DEBUG 29428 --- [demo] [restartedMain] c.e.h.SpringBootHapiApplication          : Running with Spring Boot v3.2.4, Spring v6.1.5
    2024-04-18T23:49:09.324+09:00  INFO 29428 --- [demo] [restartedMain] c.e.h.SpringBootHapiApplication          : The following 2 profiles are active: "dev", "log_default"
    …    
    2024-04-18T23:49:10.534+09:00  INFO 29428 --- [demo] [restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
    2024-04-18T23:49:10.548+09:00  INFO 29428 --- [demo] [restartedMain] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
    2024-04-18T23:49:10.548+09:00  INFO 29428 --- [demo] [restartedMain] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.19]
    2024-04-18T23:49:10.626+09:00  INFO 29428 --- [demo] [restartedMain] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
    2024-04-18T23:49:10.626+09:00  INFO 29428 --- [demo] [restartedMain] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 1245 ms

    # Bean定義によるHAPIのFHIRContextの作成
    2024-04-18T23:49:10.704+09:00  INFO 29428 --- [demo] [restartedMain] ca.uhn.fhir.util.VersionUtil             : HAPI FHIR version 7.0.2 - Rev 95beaec894
    2024-04-18T23:49:10.710+09:00  INFO 29428 --- [demo] [restartedMain] ca.uhn.fhir.context.FhirContext          : Creating new FHIR context for FHIR version [R4]
    2024-04-18T23:49:10.711+09:00 DEBUG 29428 --- [demo] [restartedMain] com.example.hapisample.FhirConfig        : FHIRContext作成：26ms
    
    # Bean定義によるHAPIのFHIRValidatorの作成（20秒と、かなり時間がかかっている）
    2024-04-18T23:49:13.965+09:00  INFO 29428 --- [demo] [restartedMain] ca.uhn.fhir.util.XmlUtil                 : Unable to determine StAX implementation: java.xml/META-INF/MANIFEST.MF not found
    2024-04-18T23:49:31.455+09:00  INFO 29428 --- [demo] [restartedMain] ca.uhn.fhir.validation.FhirValidator     : Ph-schematron library not found on classpath, will not attempt to perform schematron validation
    2024-04-18T23:49:31.457+09:00 DEBUG 29428 --- [demo] [restartedMain] com.example.hapisample.FhirConfig        : FHIRValidator作成：20738ms

    # バリデーション処理は初回が時間がかかるためダミーデータで暖機処理実行しておく（11秒と、かなり時間がかかっている）
    2024-04-18T23:49:31.469+09:00 DEBUG 29428 --- [demo] [restartedMain] c.e.h.domain.FhirValidationServiceImpl   : バリデーション暖機処理実行開始
    2024-04-18T23:49:31.542+09:00  INFO 29428 --- [demo] [restartedMain] .u.f.c.s.DefaultProfileValidationSupport : Loading structure definitions from classpath: /org/hl7/fhir/r4/model/profile/profiles-resources.xml
    2024-04-18T23:49:34.659+09:00  INFO 29428 --- [demo] [restartedMain] .u.f.c.s.DefaultProfileValidationSupport : Loading structure definitions from classpath: /org/hl7/fhir/r4/model/profile/profiles-types.xml
    2024-04-18T23:49:34.914+09:00  INFO 29428 --- [demo] [restartedMain] .u.f.c.s.DefaultProfileValidationSupport : Loading structure definitions from classpath: /org/hl7/fhir/r4/model/profile/profiles-others.xml
    2024-04-18T23:49:35.827+09:00  INFO 29428 --- [demo] [restartedMain] .u.f.c.s.DefaultProfileValidationSupport : Loading structure definitions from classpath: /org/hl7/fhir/r4/model/extension/extension-definitions.xml
    2024-04-18T23:49:38.900+09:00  INFO 29428 --- [demo] [restartedMain] .u.f.c.s.DefaultProfileValidationSupport : Loading CodeSystem/ValueSet from classpath: /org/hl7/fhir/r4/model/valueset/valuesets.xml
    2024-04-18T23:49:39.995+09:00  WARN 29428 --- [demo] [restartedMain] ca.uhn.fhir.parser.LenientErrorHandler   : Unknown element 'author' found while parsing
    2024-04-18T23:49:39.996+09:00  INFO 29428 --- [demo] [restartedMain] .u.f.c.s.DefaultProfileValidationSupport : Loading CodeSystem/ValueSet from classpath: /org/hl7/fhir/r4/model/valueset/v2-tables.xml
    2024-04-18T23:49:41.067+09:00  WARN 29428 --- [demo] [restartedMain] ca.uhn.fhir.parser.LenientErrorHandler   : Unknown element 'author' found while parsing
    2024-04-18T23:49:41.068+09:00  INFO 29428 --- [demo] [restartedMain] .u.f.c.s.DefaultProfileValidationSupport : Loading CodeSystem/ValueSet from classpath: /org/hl7/fhir/r4/model/valueset/v3-codesystems.xml
    2024-04-18T23:49:41.495+09:00  WARN 29428 --- [demo] [restartedMain] ca.uhn.fhir.parser.LenientErrorHandler   : Unknown element 'author' found while parsing
    2024-04-18T23:49:42.543+09:00 DEBUG 29428 --- [demo] [restartedMain] c.e.h.domain.FhirValidationServiceImpl   : バリデーション暖機処理実行完了：11074ms

    # Spring Boot起動までに34秒程度かかっている
    2024-04-18T23:49:42.923+09:00  INFO 29428 --- [demo] [restartedMain] o.s.b.d.a.OptionalLiveReloadServer       : LiveReload server is running on port 35729
    2024-04-18T23:49:42.970+09:00  INFO 29428 --- [demo] [restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path ''
    2024-04-18T23:49:42.984+09:00  INFO 29428 --- [demo] [restartedMain] c.e.h.SpringBootHapiApplication          : Started SpringBootHapiApplication in 34.266 seconds (process running for 35.195)
    2024-04-18T23:50:33.634+09:00  INFO 29428 --- [demo] [http-nio-8080-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
    2024-04-18T23:50:33.634+09:00  INFO 29428 --- [demo] [http-nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
    2024-04-18T23:50:33.635+09:00  INFO 29428 --- [demo] [http-nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 0 ms

    # バリデーション処理の実行（正常終了）　785msと、比較的高速に処理されている
    2024-04-18T23:50:33.684+09:00 DEBUG 29428 --- [demo] [http-nio-8080-exec-1] c.e.h.domain.FhirValidationServiceImpl   : FHIRバリデーション開始[FHIRバージョン 4.0.1]
    2024-04-18T23:50:34.469+09:00 DEBUG 29428 --- [demo] [http-nio-8080-exec-1] c.e.h.domain.FhirValidationServiceImpl   : バリデーション実行完了：785ms
    2024-04-18T23:50:34.470+09:00  INFO 29428 --- [demo] [http-nio-8080-exec-1] c.e.h.domain.FhirValidationServiceImpl   : ドキュメントは有効です
    ```    

```
