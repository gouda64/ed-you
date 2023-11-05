package com.gouda.edyou.service;

import com.gouda.edyou.entity.Feedback;
import com.gouda.edyou.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Service
@Transactional
public class FeedbackService {
    private static final String COHERE_URL = "https://api.cohere.ai/v1/classify";
//
    @Value("${cohere.apiKey}")
    private String apiKey;
    @Value("${cohere.rankingModel}")
    private String rankingModelID;
    private final RestTemplate restTemplate = new RestTemplate();

    private final FeedbackRepository FeedbackRepository;

    @Autowired
    public FeedbackService(FeedbackRepository FeedbackRepository) {
        this.FeedbackRepository = FeedbackRepository;
    }

    public Feedback findById(long id) {
        return FeedbackRepository.findById(id);
    }

    public void save(Feedback feedback) {
        feedback.setDate(new Date());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        try {
            String requestJson = "{" +
                    "\"inputs\": [ \"" + feedback.getComment() + "\" ], " +
                    "\"model\": \"" + rankingModelID + "\"" +
                    "}";
            HttpEntity<String> request = new HttpEntity<>(requestJson, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(COHERE_URL, request, String.class);
            int index = response.getBody().indexOf("\"prediction\":\"") + "\"prediction\":".length() + 1;
            int rating = Integer.parseInt(response.getBody().substring(index, index + response.getBody().substring(index).indexOf("\"")));
            feedback.setRating(rating);
        }
        catch (Exception e) {
            feedback.setRating(-1);
        }

        FeedbackRepository.save(feedback);
    }
    public void save(Feedback feedback, int rating) {
        feedback.setDate(new Date());
        feedback.setRating(rating);

        FeedbackRepository.save(feedback);
    }
}
