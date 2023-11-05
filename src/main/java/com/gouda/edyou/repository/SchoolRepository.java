package com.gouda.edyou.repository;

import com.gouda.edyou.entity.School;
import com.gouda.edyou.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolRepository extends JpaRepository<School,Long> {
    School findById(long id);
}
