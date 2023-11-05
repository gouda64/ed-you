package com.gouda.edyou.service;

import com.gouda.edyou.entity.School;
import com.gouda.edyou.repository.SchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SchoolService {
    private final SchoolRepository schoolRepository;

    @Autowired
    public SchoolService(SchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    public School findById(long id) {
        return schoolRepository.findById(id);
    }

    public void save(School school) {
        schoolRepository.save(school);
    }
}
