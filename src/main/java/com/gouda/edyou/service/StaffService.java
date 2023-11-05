package com.gouda.edyou.service;

import com.gouda.edyou.entity.Staff;
import com.gouda.edyou.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StaffService {
    private final StaffRepository StaffRepository;

    @Autowired
    public StaffService(StaffRepository StaffRepository) {
        this.StaffRepository = StaffRepository;
    }

    public Staff findById(long id) {
        return StaffRepository.findById(id);
    }

    public void save(Staff Staff) {
        StaffRepository.save(Staff);
    }
}
