package com.operations.controller;

import com.operations.DTO.EmployeeDTO;
import com.operations.DTO.ProjectDTO;
import com.operations.model.Employee;
import com.operations.model.Project;
import com.operations.repository.EmployeeRepository;
import com.operations.repository.ProjectRepository;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public ProjectController(ProjectRepository projectRepository, EmployeeRepository employeeRepository) {
        this.projectRepository = projectRepository;
        this.employeeRepository = employeeRepository;
    }

    // Create a project
    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO projectDTO) {
        Project project = new Project(projectDTO.getName(), projectDTO.getStartDate(), projectDTO.getEndDate());
        Project savedProject = projectRepository.save(project);
        ProjectDTO savedProjectDTO = mapProjectToDTO(savedProject);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProjectDTO);
    }

    // Get a project by ID
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));
        ProjectDTO projectDTO = mapProjectToDTO(project);
        return ResponseEntity.ok(projectDTO);
    }

    // Update a project by ID
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable Long projectId, @RequestBody ProjectDTO projectDTO) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        project.setName(projectDTO.getName());
        project.setStartDate(projectDTO.getStartDate());
        project.setEndDate(projectDTO.getEndDate());

        Project updatedProject = projectRepository.save(project);
        ProjectDTO updatedProjectDTO = mapProjectToDTO(updatedProject);
        return ResponseEntity.ok(updatedProjectDTO);
    }

    // Delete a project by ID
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId) {
        projectRepository.deleteById(projectId);
        return ResponseEntity.noContent().build();
    }

    // Add an employee to a project
    @PostMapping("/{projectId}/employees/{employeeId}")
    public ResponseEntity<Void> addEmployeeToProject(@PathVariable Long projectId, @PathVariable Long employeeId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        project.getEmployees().add(employee);
        projectRepository.save(project);

        employee.getProjects().add(project);
        employeeRepository.save(employee);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Remove an employee from a project
    @DeleteMapping("/{projectId}/employees/{employeeId}")
    public ResponseEntity<Void> removeEmployeeFromProject(@PathVariable Long projectId, @PathVariable Long employeeId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        project.getEmployees().remove(employee);
        projectRepository.save(project);

        employee.getProjects().remove(project);
        employeeRepository.save(employee);

        return ResponseEntity.noContent().build();
    }

    // Get all employees in a project
    @GetMapping("/{projectId}/employees")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesInProject(@PathVariable Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        List<EmployeeDTO> employeeDTOs = project.getEmployees().stream()
                .map(this::mapEmployeeToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(employeeDTOs);
    }

    // List all projects
    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        List<ProjectDTO> projectDTOs = projects.stream()
                .map(this::mapProjectToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(projectDTOs);
    }

    // Utility method to map Project to ProjectDTO
    private ProjectDTO mapProjectToDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(project.getProjectId());
        projectDTO.setName(project.getName());
        projectDTO.setStartDate(project.getStartDate());
        projectDTO.setEndDate(project.getEndDate());
        return projectDTO;
    }

    // Utility method to map Employee to EmployeeDTO
    private EmployeeDTO mapEmployeeToDTO(Employee employee) {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setEmployeeId(employee.getEmployeeId());
        employeeDTO.setFirstName(employee.getFirstName());
        employeeDTO.setLastName(employee.getLastName());
        employeeDTO.setEmail(employee.getEmail());
        return employeeDTO;
    }
}
