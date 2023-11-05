package com.gouda.edyou.validator;

import com.gouda.edyou.entity.Feedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;

@Component
public class FeedbackValidator implements Validator {
    private static final String COHERE_URL = "https://api.cohere.ai/v1/classify";

    @Value("${cohere.apiKey}")
    private String apiKey;
    @Value("${cohere.toxicityModel}")
    private String toxicityModelID;
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public FeedbackValidator() {

    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Feedback.class.equals(clazz);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Feedback feedback = (Feedback) o;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        String requestJson = "{" +
                "\"inputs\": [ \"" + feedback.getComment() + "\" ], " +
                "\"model\": \"" + toxicityModelID + "\"" +
                "}";
        HttpEntity<String> request = new HttpEntity<>(requestJson, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(COHERE_URL, request, String.class);
        int predIndex = response.getBody().indexOf("\"prediction\":") + "\"prediction\":".length() + 1;
        String pred = response.getBody().substring(predIndex, predIndex + response.getBody().substring(predIndex).indexOf("\""));
        if (pred.equals("Toxic")) {
            errors.rejectValue("comment", "error", "This comment was detected as toxic. Please reword and resubmit!");
        }
    }
}
