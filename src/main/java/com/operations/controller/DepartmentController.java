package com.operations.controller;

import com.operations.model.Department;
import com.operations.model.Employee;
import com.operations.repository.DepartmentRepository;
import com.operations.repository.EmployeeRepository;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public DepartmentController(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    // Add a department
    @PostMapping
    public ResponseEntity<Department> addDepartment(@RequestBody Department department) {
        Department savedDepartment = departmentRepository.save(department);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDepartment);
    }

    // Get department by ID
    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartmentById(@PathVariable Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));

        return ResponseEntity.ok(department);
    }

    // Update department by ID
    @PutMapping("/{id}")
    public ResponseEntity<Department> updateDepartment(@PathVariable Long id, @RequestBody Department updatedDepartment) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));

        department.setName(updatedDepartment.getName());
        department.setCity(updatedDepartment.getCity());

        Department updatedDepartmentObj = departmentRepository.save(department);
        return ResponseEntity.ok(updatedDepartmentObj);
    }
    // Delete department by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDepartmentById(@PathVariable Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));

        List<Employee> employees = employeeRepository.findByDepartment(department);
        if (!employees.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Cannot delete department with employees. Remove employees first.");
        }

        departmentRepository.delete(department);
        return ResponseEntity.noContent().build();
    }
}
