package com.gouda.edyou.service;

import com.gouda.edyou.entity.Feedback;
import com.gouda.edyou.entity.Staff;
import com.gouda.edyou.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
public class StaffService {
    private static final String COHERE_URL = "https://api.cohere.ai/v1/summarize";
    @Value("${cohere.apiKey}")
    private String apiKey;
    private final RestTemplate restTemplate = new RestTemplate();

    private final StaffRepository StaffRepository;

    @Autowired
    public StaffService(StaffRepository StaffRepository) {
        this.StaffRepository = StaffRepository;
    }

    public Staff findById(long id) {
        return StaffRepository.findById(id);
    }

    public void save(Staff staff) {
        StaffRepository.save(staff);
    }

    public String getSummary(Staff staff) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        try {
            StringBuilder sb = new StringBuilder();
            for (Feedback fb : staff.getFeedback()) {
                sb.append(fb.getComment() + ". ");
            }

            String requestJson = "{" +
                    "\"text\":\"" + sb.toString() + "\"" +
                    "}";
            HttpEntity<String> request = new HttpEntity<>(requestJson, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(COHERE_URL, request, String.class);
            int ind = response.getBody().indexOf("\"summary\":") + "\"summary\":".length() + 1;
            return response.getBody().substring(ind, ind + response.getBody().substring(ind).indexOf("\""));

        }
        catch (Exception e) {
            return "Sorry, something went wrong with summary generation! More feedback may be needed before a summary can be compiled.";
        }
    }
}
