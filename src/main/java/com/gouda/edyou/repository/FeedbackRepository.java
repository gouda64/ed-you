package com.gouda.edyou.repository;

import com.gouda.edyou.entity.Feedback;
import com.gouda.edyou.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback,Long> {
    Feedback findById(long id);
}
