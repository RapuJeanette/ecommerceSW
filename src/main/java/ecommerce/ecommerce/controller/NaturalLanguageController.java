package ecommerce.ecommerce.controller;

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.LanguageServiceSettings;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;

import org.springframework.web.bind.annotation.GetMapping;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.language.v1.AnalyzeSentimentResponse;

@RestController
@RequestMapping("/analyze")
public class NaturalLanguageController {

    @Value("${spring.cloud.gcp.credentials.location}")
    private String credentialsLocation;

    @GetMapping
    public String analyzeText() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsLocation))
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(credentials);

        LanguageServiceSettings settings = LanguageServiceSettings.newBuilder()
                .setCredentialsProvider(credentialsProvider)
                .build();

        try (LanguageServiceClient languageServiceClient = LanguageServiceClient.create(settings)) {
            String text = "Texto que deseas analizar";
            Document document = Document.newBuilder()
                    .setContent(text)
                    .setType(Document.Type.PLAIN_TEXT)
                    .build();

                    AnalyzeSentimentResponse response = languageServiceClient.analyzeSentiment(document);

                    // Procesar el resultado del análisis de sentimiento
                    float score = response.getDocumentSentiment().getScore();
                    float magnitude = response.getDocumentSentiment().getMagnitude();
        
                    // Ejemplo de cómo podrías usar el resultado
                    String sentimentResult = "Análisis de Sentimiento - Score: " + score + ", Magnitud: " + magnitude;
        
                    return sentimentResult;
        }
    }
}

