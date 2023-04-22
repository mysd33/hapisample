package hapisample;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

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

public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws FileNotFoundException {
        InputStream is = new FileInputStream("file/input/Bundle-BundleReferralExample01.json");

        FhirContext ctx = FhirContext.forR4();
        IParser parser = ctx.newJsonParser();
        Bundle bundle = parser.parseResource(Bundle.class, is);

        logger.info("Bundle type:{}", bundle.getType().getDisplay());

        List<BundleEntryComponent> entities = bundle.getEntry();

        entities.forEach(e -> {
            Resource resource = e.getResource();
            ResourceType resourceType = resource.getResourceType();
            logger.info("Resource Type: {}", resourceType.name());

            switch (resourceType) {
            case Composition:
                Composition composition = (Composition) resource;
                String title = composition.getTitle();
                logger.info("文書名: {}", title);

                Reference subjectRef = composition.getSubject();
                logger.info("subject reference: {}", subjectRef.getReference());
                logger.info("subject display: {}", subjectRef.getDisplay());

                break;

            default:
                break;
            }

        });

    }
}
