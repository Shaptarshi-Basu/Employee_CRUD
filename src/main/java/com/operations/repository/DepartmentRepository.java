package com.operations.repository;

import com.operations.model.Department;
import com.operations.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Department findByName(String departmentName);
}