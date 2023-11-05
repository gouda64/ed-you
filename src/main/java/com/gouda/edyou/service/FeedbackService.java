package com.gouda.edyou.service;

import com.gouda.edyou.entity.Feedback;
import com.gouda.edyou.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class FeedbackService {
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
        FeedbackRepository.save(feedback);
    }
}
