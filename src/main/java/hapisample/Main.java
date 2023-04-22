package hapisample;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.IValidatorModule;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;

public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);
    
    // （参考）
    // https://hapifhir.io/hapi-fhir/docs/model/parsers.html
    // https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html
    public static void main(String[] args) throws FileNotFoundException {
        // あえて分かりやすくするため１つのメソッドに手続き的に書いてあるので、本当に実装したい場合は保守性の高いモジュール化されたコード書くこと
        
        // 診療情報提供書のHL7 FHIRのサンプルデータを読み込み
        InputStream is = new BufferedInputStream(new FileInputStream("file/input/Bundle-BundleReferralExample01.json"));
        // FHIRコンテキスト作成
        FhirContext ctx = FhirContext.forR4();
        // パーサを作成
        IParser parser = ctx.newJsonParser();
        // サンプルデータをパースしBundleリソースを取得
        Bundle bundle = parser.parseResource(Bundle.class, is);

        // FHIR公式の検証ルールに基づいているか検証    
        // Validatorの作成
        FhirValidator validator = ctx.newValidator();
        IValidatorModule module = new FhirInstanceValidator(ctx);
        validator.registerValidatorModule(module);
        // 検証
        ValidationResult validationResult = validator.validateWithResult(bundle);
        if (!validationResult.isSuccessful()) {
            // 検証結果の出力
            for (SingleValidationMessage validationMessage : validationResult.getMessages()) {                
                logger.warn("[{}] {}", validationMessage.getLocationString(), validationMessage.getMessage());
            }
        }
        
        // Bundleリソースを解析
        logger.info("Bundle type:{}", bundle.getType().getDisplay());
        // BundleからEntryを取得
        List<BundleEntryComponent> entries = bundle.getEntry();
        String subjectRefId = null;
        // Entry内のResourceを取得
        for (BundleEntryComponent entry : entries) {
            Resource resource = entry.getResource();
            ResourceType resourceType = resource.getResourceType();
            logger.info("Resource Type: {}", resourceType.name());
            switch (resourceType) {
            case Composition:
                // Compositionリソースを解析する例
                Composition composition = (Composition) resource;
                String title = composition.getTitle();
                logger.info("文書名: {}", title);
                // subjectの参照先のUUIDを取得
                Reference subjectRef = composition.getSubject();
                subjectRefId = subjectRef.getReference();
                logger.info("subject display: {}", subjectRef.getDisplay());
                logger.info("subject reference Id: {}", subjectRefId);
                // TODO: 各参照先のUUIDを取得する処理の追加
                break;
            case Patient:
                // Patientリソースを解析する例
                if (!entry.getFullUrl().equals(subjectRefId)) {
                    break;
                }
                logger.info("Composition.subjectの参照先のPatient:{}", subjectRefId);
                Patient patient = (Patient) resource;
                // 患者番号の取得
                logger.info("患者番号:{}", patient.getIdentifier().get(0).getValue());
                // 患者氏名の取得
                List<HumanName> humanNames = patient.getName();                
                humanNames.forEach(humanName -> {
                   String valueCode = humanName.getExtensionString("http://hl7.org/fhir/StructureDefinition/iso21090-EN-representation");
                   if ("IDE".equals(valueCode)) {
                       logger.info("患者氏名:{}",humanName.getText());
                   } else {
                       logger.info("患者カナ氏名:{}",humanName.getText());
                   }
                });                
                break;
            // TODO: リソース毎に処理の追加
            default:
                break;
            }
        }

    }
}
