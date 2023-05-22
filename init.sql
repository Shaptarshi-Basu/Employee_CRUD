CREATE TABLE departments (
  department_id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  city VARCHAR(255) NOT NULL
);

CREATE TABLE employees (
  employee_id SERIAL PRIMARY KEY,
  first_name VARCHAR(255) NOT NULL,
  last_name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  department_id BIGINT REFERENCES departments(department_id)
);

-- Create projects table
CREATE TABLE projects (
  project_id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL
);

-- Create join table for the many-to-many relationship between employees and projects
CREATE TABLE employee_project (
  employee_id BIGINT REFERENCES employees(employee_id),
  project_id BIGINT REFERENCES projects(project_id),
  PRIMARY KEY (employee_id, project_id)
);