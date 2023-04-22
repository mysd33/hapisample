package hapisample;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Composition;
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

    public static void main(String[] args) throws FileNotFoundException {
        // 診療情報提供書のHL7 FHIRのサンプルデータを読み込み
        InputStream is = new FileInputStream("file/input/Bundle-BundleReferralExample01.json");

        // FHIRコンテキスト作成
        FhirContext ctx = FhirContext.forR4();

        // HAPI FHIRを使ってパース
        // https://hapifhir.io/hapi-fhir/docs/model/parsers.html
        // パーサを作成
        IParser parser = ctx.newJsonParser();
        // サンプルデータをパースしBundleリソースを取得
        Bundle bundle = parser.parseResource(Bundle.class, is);

        // HAPI FHIRを使ってFHIR公式の検証ルールに基づいているか検証
        // https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html
        // Validatorの作成
        FhirValidator validator = ctx.newValidator();
        IValidatorModule module = new FhirInstanceValidator(ctx);
        validator.registerValidatorModule(module);
        // 検証
        ValidationResult validationResult = validator.validateWithResult(bundle);
        if (!validationResult.isSuccessful()) {
            for (SingleValidationMessage validationMessage : validationResult.getMessages()) {
                logger.warn("[{}] {}", validationMessage.getLocationString(), validationMessage.getMessage());
            }
        }

        // Bundleリソースを解析
        logger.info("Bundle type:{}", bundle.getType().getDisplay());

        // BundleからEntityを取得
        List<BundleEntryComponent> entities = bundle.getEntry();

        // Entity内のResourceを取得
        entities.forEach(e -> {
            Resource resource = e.getResource();
            ResourceType resourceType = resource.getResourceType();
            logger.info("Resource Type: {}", resourceType.name());

            switch (resourceType) {
            case Composition:
                // Compositionリソースを解析する例
                Composition composition = (Composition) resource;
                String title = composition.getTitle();
                logger.info("文書名: {}", title);

                Reference subjectRef = composition.getSubject();
                logger.info("subject reference: {}", subjectRef.getReference());
                logger.info("subject display: {}", subjectRef.getDisplay());
                // TODO: 処理の追加
                break;
            // TODO: リソース毎に処理の追加
            default:
                break;
            }

        });

    }
}
