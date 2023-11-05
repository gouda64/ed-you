package com.gouda.edyou.repository;

import com.gouda.edyou.entity.Staff;
import com.gouda.edyou.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRepository extends JpaRepository<Staff,Long> {
    Staff findById(long id);
}
