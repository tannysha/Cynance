package com.dbIntegration.exp3.info;

import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@RestController
public class EmployeeController {
    private LinkedList<Employee> employees = new LinkedList<>();

    // Add a new employee
    @PostMapping("/addEmployee/{id}/{name}/{dob}/{address}")
    public Employee addEmployee(@PathVariable int id, @PathVariable String name, @PathVariable String dob, @PathVariable String address) {
        Employee employee = new Employee(id, name, dob, address);
        employees.add(employee);
        return employee;
    }

    // Update an existing employee
    @PutMapping("/updateEmployee/{id}/{name}/{dob}/{address}")
    public Employee updateEmployee(@PathVariable int id, @PathVariable String name, @PathVariable String dob, @PathVariable String address) {
        for (Employee employee : employees) {
            if (employee.getId() == id) {
                employee.setName(name);
                employee.setDob(dob);
                employee.setAddress(address);
                return employee;
            }
        }
        return null;
    }

    // Get all employees
    @GetMapping("/employees")
    public List<Employee> getAllEmployees() {
        return employees;
    }

    // Get employee by ID
    @GetMapping("/employee/{id}")
    public Employee getEmployeeById(@PathVariable int id) {
        for (Employee employee : employees) {
            if (employee.getId() == id) {
                return employee;
            }
        }
        return null;
    }
//not sure how else to relate the two these based on the entity relation model. However, eventually it will become easier to update the same
@PutMapping("/assignRelation/{id}/pid")
    public AssignRelation assignEmployee(@PathVariable int id, @PathVariable String pid) {
        Employee employee = employees.get(id);
        return new AssignRelation(employee,new Project(pid));
}
}