package hapisample;

import org.hl7.fhir.r4.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

/**
 * FHIRリソースのオブジェクトをJSONシリアライズする
 *
 */
public class SerializingSampleMain {
    private static Logger logger = LoggerFactory.getLogger(SerializingSampleMain.class);

    
    public static void main(String[] args) {
        // FHIRコンテキスト作成
        FhirContext ctx = FhirContext.forR4();

        // シリアライズ対象のPatient（患者）情報を作成
        Patient patient = new Patient();
        patient.addName().setFamily("山田").addGiven("太郎");

        // パーサを作成
        IParser parser = ctx.newJsonParser();

        // JSONにシリアライズ
        // 改行等なしで出力する場合
        //String serialized = parser.encodeResourceToString(patient);

        // Pretty Printingで改行等含む文字列に指定
        String serialized = parser.setPrettyPrint(true).encodeResourceToString(patient);
        logger.info("実行結果:\n{}", serialized);

    }
}
