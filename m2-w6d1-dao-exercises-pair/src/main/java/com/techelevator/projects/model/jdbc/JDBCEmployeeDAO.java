package com.techelevator.projects.model.jdbc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.EmployeeDAO;
import com.techelevator.projects.model.Project;

public class JDBCEmployeeDAO implements EmployeeDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCEmployeeDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<Employee> getAllEmployees() {
		String mySearch = "SELECT * FROM employee";
		SqlRowSet employeeRowSet = jdbcTemplate.queryForRowSet(mySearch);
		List<Employee> allEmployee = new ArrayList<>();
		while(employeeRowSet.next()){
			Employee ourEmployee = new Employee();
			ourEmployee = mapRowToEmployee(employeeRowSet);
			allEmployee.add(ourEmployee);
		}
		
		return allEmployee;
	}

	@Override
	public List<Employee> searchEmployeesByName(String firstNameSearch, String lastNameSearch) {
		String mySearch = "SELECT * FROM employee WHERE first_name ILIKE ? AND last_name ILIKE ? ORDER BY last_name, first_name";
		SqlRowSet employeesRowSet = jdbcTemplate.queryForRowSet(mySearch,"%" + firstNameSearch + "%", "%" + lastNameSearch + "%");
		List<Employee> allEmployee = new ArrayList<>();
		while(employeesRowSet.next()){
			Employee matchEmployee = new Employee();
			matchEmployee = mapRowToEmployee(employeesRowSet);
			allEmployee.add(matchEmployee);
		}
		
		return allEmployee;
	}

	@Override
	public List<Employee> getEmployeesByDepartmentId(Long id) {
		String mySearch = "SELECT * FROM employee WHERE department_id =? ORDER BY last_name, first_name";
		SqlRowSet employeesRowSet = jdbcTemplate.queryForRowSet(mySearch, id);
		List<Employee> allEmployee = new ArrayList<>();
		while(employeesRowSet.next()){
			Employee matchEmployee = new Employee();
			matchEmployee = mapRowToEmployee(employeesRowSet);
			allEmployee.add(matchEmployee);
		}
		return allEmployee;
	}

	@Override
	public List<Employee> getEmployeesWithoutProjects() {
		String mySearch = "SELECT * FROM project_employee pe RIGHT JOIN employee e ON e.employee_id = pe.employee_id WHERE pe.project_id IS NULL";
		SqlRowSet employeeRowSet = jdbcTemplate.queryForRowSet(mySearch);
		List<Employee> allEmployee = new ArrayList<>();
		while(employeeRowSet.next()){
			Employee matchEmployee = new Employee();
			matchEmployee = mapRowToEmployee(employeeRowSet);
			allEmployee.add(matchEmployee);
		}
		return allEmployee;
	}

	@Override
	public List<Employee> getEmployeesByProjectId(Long projectId) {
		String mySearch = "SELECT * FROM employee e JOIN project_employee pe ON e.employee_id = pe.employee_id JOIN project p ON pe.project_id = p.project_id  WHERE pe.project_id = ?";
		SqlRowSet employeeRowSet = jdbcTemplate.queryForRowSet(mySearch, projectId);
		List<Employee> allEmployee = new ArrayList<>();
		while(employeeRowSet.next()){
			Employee matchEmployee = new Employee();
			matchEmployee = mapRowToEmployee(employeeRowSet);
			allEmployee.add(matchEmployee);
		}
		return allEmployee;
	}

	@Override
	public void changeEmployeeDepartment(Long employeeId, Long departmentId) {
		String mySearch = "UPDATE employee SET department_id = ? WHERE employee_id = ?";
		jdbcTemplate.update(mySearch, departmentId, employeeId);
		
	}
	
	public Employee makeNewEmployee(String firstName, String lastName, LocalDate birthDate , String gender, LocalDate hireDate ){
		Employee myEmployee = new Employee();
		myEmployee.setFirstName(firstName);
		myEmployee.setLastName(lastName);
		myEmployee.setBirthDay(birthDate);
		myEmployee.setGender(gender);
		myEmployee.setHireDate(hireDate);
		String myAddition = "INSERT INTO employee (first_name, last_name, birth_date, gender, hire_date)"
				+ "VALUES(?, ?, ?, ?, ?) RETURNING employee_id";
		myEmployee.setId(jdbcTemplate.queryForObject(myAddition, Long.class, firstName, lastName, birthDate, gender, hireDate));
//		String myEmployeeQuery = "SELECT * FROM employee WHERE first_name = ?, last_name = ?";
//		SqlRowSet employeeRowSet = jdbcTemplate.queryForRowSet(myEmployeeQuery, firstName, lastName);
		
		
		return myEmployee;
	}
	
	public Employee mapRowToEmployee(SqlRowSet employeeRowSet){
		Employee employeeNew = new Employee();
		employeeNew.setId(employeeRowSet.getLong("employee_id"));
		employeeNew.setDepartmentId(employeeRowSet.getLong("department_id"));
		employeeNew.setFirstName(employeeRowSet.getString("first_name"));
		employeeNew.setLastName(employeeRowSet.getString("last_name"));
		employeeNew.setBirthDay(employeeRowSet.getDate("birth_date").toLocalDate());
		employeeNew.setGender(employeeRowSet.getString("gender"));
		employeeNew.setHireDate(employeeRowSet.getDate("hire_date").toLocalDate());
		return employeeNew;
	}

}
