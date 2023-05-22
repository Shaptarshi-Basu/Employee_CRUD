package com.operations.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.operations.DTO.EmployeeDTO;
import com.operations.model.Department;
import com.operations.model.Employee;
import com.operations.repository.DepartmentRepository;
import com.operations.repository.EmployeeRepository;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Autowired
    public EmployeeController(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    // Add an employee
    @PostMapping
    public ResponseEntity<EmployeeDTO> addEmployee(@RequestParam("department_name") String departmentName, @RequestBody Employee employee) {
        Department department = departmentRepository.findByName(departmentName);
        if (department == null) {
            // Handle department not found scenario (e.g., throw an exception)
            throw new ResourceNotFoundException("Department not found with name: " + departmentName);
        }

        employee.setDepartment(department);
        Employee savedEmployee = employeeRepository.save(employee);
        EmployeeDTO employeeDTO = mapEmployeeToDTO(savedEmployee);
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeDTO);

    }

    private EmployeeDTO mapEmployeeToDTO(Employee employee) {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setEmployeeId(employee.getEmployeeId());
        employeeDTO.setFirstName(employee.getFirstName());
        employeeDTO.setLastName(employee.getLastName());
        employeeDTO.setEmail(employee.getEmail());
        employeeDTO.setDepartmentName(employee.getDepartment().getName());
        return employeeDTO;
    }

    // List all employees
    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        List<EmployeeDTO> employeeDTOs = employees.stream()
                .map(this::mapEmployeeToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(employeeDTOs);
    }


    // Get employee by ID
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        EmployeeDTO employeeDTO = mapEmployeeToDTO(employee);

        return ResponseEntity.ok(employeeDTO);
    }

    // Update employee by ID
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable Long id, @RequestParam("department_name") String departmentName, @RequestBody Employee updatedEmployee) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        // Check if the department name has changed
        if (!departmentName.equals(employee.getDepartment().getName())) {
            Department oldDepartment = employee.getDepartment();
            Department newDepartment = departmentRepository.findByName(departmentName);

            if (newDepartment == null) {
                throw new ResourceNotFoundException("Department not found with name: " + departmentName);
            }

            // Remove the employee from the old department
            oldDepartment.getEmployees().remove(employee);

            // Add the employee to the new department
            newDepartment.getEmployees().add(employee);

            // Update the employee's department
            employee.setDepartment(newDepartment);

            // Save the updated departments
            departmentRepository.save(oldDepartment);
            departmentRepository.save(newDepartment);
        }

        employee.setFirstName(updatedEmployee.getFirstName());
        employee.setLastName(updatedEmployee.getLastName());
        employee.setEmail(updatedEmployee.getEmail());

        Employee updatedEmployeeObj = employeeRepository.save(employee);
        EmployeeDTO employeeDTO = mapEmployeeToDTO(updatedEmployeeObj);
        return ResponseEntity.ok(employeeDTO);
    }
}