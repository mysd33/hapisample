package hapisample;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Composition.CompositionStatus;
import org.hl7.fhir.r4.model.HumanName.NameUse;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Narrative;
import org.hl7.fhir.r4.model.Narrative.NarrativeStatus;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

/**
 * 処方情報のFHIRリソースのオブジェクトを、FHIR（JSON）形式にシリアライズする
 *
 */
public class SerializingSampleMain {
    private static Logger logger = LoggerFactory.getLogger(SerializingSampleMain.class);

    // FHIR形式に変換対象の入力データを疑似したデータクラス
    static record PrescriptionData(//
            String prescriptionNo, // 処方箋番号
            String patientNo, // 患者番号
            String hokenjaNo, // 保険者番号
            String hihokennshaKigo, // 被保険者記号
            String hihokennshaBango, // 被保険者番号
            String hihokennshaEdaNo, // 被保険者枝番
            String kanjiLastName, // 漢字姓
            String kanjiFirstName, // 漢字名
            String kanaLastName, // かな姓
            String kanaFirstName, // かな名
            int genderCode, // 性別コード(0:男性、1:女性、2:その他、3:不明）
            Date birthday, // 誕生日
            String zip, // 郵便番号
            String address, // 住所
            Date lastUpdated// 最終更新日
    ) {

    }

    // （参考）
    // https://hapifhir.io/hapi-fhir/docs/model/parsers.html#encoding-aka-serializing
    // あえて分かりやすくするため１つのメソッドに手続き的に書いてあるので、本当に実装したい場合は保守性の高いモジュール化されたコード書くこと
    public static void main(String[] args) {
        try {
            // FHIR形式に変換対象の入力データ
            PrescriptionData input = new PrescriptionData(//
                    "1311234567-2020-00123456", //
                    "00000010", //
                    "00012345", "あいう", "１８７", "05", //
                    "東京", "太郎", "トウキョウ", "タロウ", //
                    0, //
                    new SimpleDateFormat("yyyy-MM-DD").parse("1920-02-11"), //
                    "123-4567", "神奈川県横浜市港区１－２－３"//
                    , new Date());

            // FHIRコンテキスト作成
            FhirContext ctx = FhirContext.forR4();

            // 処方情報のFHIR記述仕様書ドラフト（https://jpfhir.jp/fhir/ePrescriptionData/igv1/）を参考に
            // 一部分のJSONを作成

            // PatientのEntryを作成
            BundleEntryComponent patientEntry = createPatientEntry(input);

            // CompositionのEntryを作成
            BundleEntryComponent compositionEntry = createCompositionEntry(input, patientEntry);

            // TODO: Bundleに、以下のリソースを作成
            // Encounter（受診時状況情報）
            // Coverage（保険情報）
            // Coverage（公費情報）
            // Organization（費用負担者（保険者等）情報）
            // Organization（処方医療機関情報）
            // Organization（処方診療科情報）
            // PractitionerRole（処方医役割情報）
            // Practitioner（処方医情報）
            // MedicationRequest（処方指示情報）
            // Communication（処方箋全体の指示、明細単位での備考記述）

            // Bundleを作成
            Bundle bundle = createBundle(input)
                    // BundleにComposition（文書構成情報）を追加
                    .addEntry(compositionEntry)
                    // BundleにPatient（患者情報）を追加
                    .addEntry(patientEntry);
            // TODO:その他のリソースの追加

            // パーサを作成
            IParser parser = ctx.newJsonParser();
            // BundleをJSONにシリアライズ
            // 改行等なしで出力する場合
            // String serialized = parser.encodeResourceToString(bundle);
            // Pretty Printingで改行等含む文字列に指定
            String serialized = parser.setPrettyPrint(true).encodeResourceToString(bundle);
            logger.info("実行結果:\n{}", serialized);
        } catch (Exception e) {
            logger.error("予期せぬエラーが発生しました", e);
        }

    }

    /**
     * Bundleリソースを作成
     */
    private static Bundle createBundle(PrescriptionData prescriptionData) {
        // Bundleを作成
        Bundle bundle = new Bundle();
        // meta
        bundle.setMeta(new Meta()//
                .addProfile("http://jpfhir.jp/fhir/ePrescription/StructureDefinition/JP_Bundle_ePrescriptionData")//
                .setLastUpdated(prescriptionData.lastUpdated()));
        // type
        bundle.setType(BundleType.DOCUMENT)//
                .setTimestamp(prescriptionData.lastUpdated());
        // timestamp
        bundle.setTimestamp(prescriptionData.lastUpdated());
        return bundle;
    }

    /**
     * CompositionリソースのEntryを作成
     */
    private static BundleEntryComponent createCompositionEntry(PrescriptionData prescriptionData,
            BundleEntryComponent patientEntry
    // TODO: 引数にEntry追加
    ) {
        // Composition（文書構成情報）を作成
        Composition composition = new Composition();
        composition.setId("compositionReferralExample01Inline");
        // meta
        Meta meta = new Meta()
                .addProfile("http://jpfhir.jp/fhir/eReferral/StructureDefinition/JP_Composition_ePrescriptionData")
                .setLastUpdated(prescriptionData.lastUpdated());
        composition.setMeta(meta);

        // text
        Narrative narrative = new Narrative().setStatus(NarrativeStatus.GENERATED);
        narrative.setDivAsString("<div xmlns=\"http://www.w3.org/1999/xhtml\">xxx</div>");
        composition.setText(narrative);

        // extention
        composition.addExtension()//
                .setUrl("http://hl7.org/fhir/StructureDefinition/composition-clinicaldocument-versionNumber")
                .setValue(new StringType("1.0"));

        // identifier
        Identifier identifier = new Identifier()//
                .setSystem("http://jpfhir.jp/fhir/Common/IdSystem/resourceInstance-identifier")
                .setValue(prescriptionData.prescriptionNo());
        composition.setIdentifier(identifier);

        // status
        composition.setStatus(CompositionStatus.FINAL);

        // type
        CodeableConcept typeCodeableConcept = new CodeableConcept();
        Coding typeCoding = new Coding().setCode("57833-6")
                .setSystem("http://jpfhir.jp/fhir/Common/CodeSystem/doc-typecodes")//
                .setDisplay("処方箋");
        typeCodeableConcept.addCoding(typeCoding);
        composition.setType(typeCodeableConcept);

        // category
        CodeableConcept categoryCodeableConcept = new CodeableConcept();
        Coding categoryCoding = new Coding()//
                .setSystem("http://jpfhir.jp/fhir/ePrescription/CodeSystem/prescription-category")//
                .setCode("01")//
                .setDisplay("処方箋");
        categoryCodeableConcept.addCoding(categoryCoding);
        composition.setCategory(List.of(categoryCodeableConcept));

        // subject
        Reference subjectReference = new Reference().setReference(patientEntry.getFullUrl());
        composition.setSubject(subjectReference);

        // TODO: 項目追加
        // encounter

        // date
        composition.setDate(prescriptionData.lastUpdated());
        // author

        // title
        composition.setTitle("処方箋");

        // TODO: 項目追加
        // custodian
        // event
        // section

        return new BundleEntryComponent().setFullUrl(String.format("urn:uuid:%s", UUID.randomUUID().toString()))
                .setResource(composition);
    }

    /**
     * PatientリソースのEntryを作成する
     * 
     * @throws ParseException
     */
    private static BundleEntryComponent createPatientEntry(PrescriptionData prescriptionData) {
        // Patient（患者情報）を作成
        Patient patient = new Patient();
        // meta
        patient.setMeta(new Meta()//
                .addProfile("http://jpfhir.jp/fhir/ePrescription/StructureDefinition/JP_Patient_ePrescriptionData")//
                .setLastUpdated(prescriptionData.lastUpdated()));
        // text
        Narrative narrative = new Narrative().setStatus(NarrativeStatus.GENERATED);
        narrative.setDivAsString("<div xmlns=\"http://www.w3.org/1999/xhtml\">xxx</div>");
        patient.setText(narrative);

        // identifier（患者番号）
        patient.addIdentifier()//
                .setSystem("urn:oid:1.2.392.100495.20.3.51.11311234567")//
                .setValue(prescriptionData.patientNo());

        // identifier（保険個人識別子）
        // https://jpfhir.jp/fhir/ccs/output/#%E4%BF%9D%E9%99%BA%E5%80%8B%E4%BA%BA%E8%AD%98%E5%88%A5%E5%AD%90%E3%81%AE%E6%A0%BC%E7%B4%8D%E6%96%B9%E6%B3%95
        patient.addIdentifier()//
                .setSystem(String.format("http://jpfhir.jp/fhir/ccs/Idsysmem/JP_Insurance_member/%s",
                        prescriptionData.hokenjaNo))//
                .setValue(String.format("%s:%s:%s:%s", prescriptionData.hokenjaNo, prescriptionData.hihokennshaKigo(),
                        prescriptionData.hihokennshaBango(), prescriptionData.hihokennshaEdaNo));

        // name（漢字表記）
        patient.addName()//
                .setUse(NameUse.OFFICIAL)//
                .setText(String.format("%s　%s", prescriptionData.kanjiLastName(), prescriptionData.kanjiFirstName()))//
                .setFamily(prescriptionData.kanjiLastName())//
                .addGiven(prescriptionData.kanjiFirstName())//
                .addExtension()//
                .setUrl("http://hl7.org/fhir/StructureDefinition/iso21090-EN-representation")//
                .setValue(new StringType("IDE"));
        // name（よみ）
        patient.addName().setUse(NameUse.OFFICIAL)//
                .setText(String.format("%s　%s", prescriptionData.kanaLastName(), prescriptionData.kanaFirstName()))//
                .setFamily(prescriptionData.kanaLastName())//
                .addGiven(prescriptionData.kanaFirstName())//
                .addExtension()//
                .setUrl("http://hl7.org/fhir/StructureDefinition/iso21090-EN-representation")//
                .setValue(new StringType("SYL"));
        // gender
        switch (prescriptionData.genderCode) {
        case 0: {
            patient.setGender(AdministrativeGender.MALE);
            break;
        }
        case 1: {
            patient.setGender(AdministrativeGender.FEMALE);
            break;
        }
        case 2: {
            patient.setGender(AdministrativeGender.OTHER);
            break;
        }
        default:
            patient.setGender(AdministrativeGender.UNKNOWN);
        }
        // birthdate
        patient.setBirthDate(prescriptionData.birthday());
        // address
        patient.addAddress()//
                .setText(prescriptionData.address())//
                .setPostalCode(prescriptionData.zip())//
                .setCountry("JP");

        return new BundleEntryComponent()//
                .setFullUrl(String.format("urn:uuid:%s", UUID.randomUUID().toString()))//
                .setResource(patient);
    }

}
