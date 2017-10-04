package com.techelevator.projects.model.jdbc;

import static org.junit.Assert.*;

import org.junit.Test;
import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.Project;


public class JDBCProjectDAOtest {

	private static SingleConnectionDataSource dataSource;
	private JDBCProjectDAO dao;
	private JdbcTemplate jdbcTemplate;
	


	@BeforeClass
	public static void setupDataSource() {
		dataSource = new SingleConnectionDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/projects");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");
		/* The following line disables autocommit for connections 
		 * returned by this DataSource. This allows us to rollback
		 * any changes after each test */
		dataSource.setAutoCommit(false);
	}
	
	/* After all tests have finished running, this method will close the DataSource */
	@AfterClass
	public static void closeDataSource() throws SQLException {
		dataSource.destroy();
	}
	
	@Before
	public void setup() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update("DELETE FROM project_employee");
		jdbcTemplate.update("DELETE FROM project");

		dao = new JDBCProjectDAO(dataSource);
	}

	/* After each test, we rollback any changes that were made to the database so that
	 * everything is clean for the next test */
	@After
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}


	@Test
	public void testGetAllActiveProjects() {
		//Arrange
		String sqlInsertProject = "INSERT INTO project (name) VALUES ('testProject')";
		jdbcTemplate.update(sqlInsertProject);
		//Act
		dao.getAllActiveProjects();
		List<Project> activeProjects = new ArrayList<>();
		activeProjects.addAll(dao.getAllActiveProjects());
		//Assert
		assertNotNull(dao.getAllActiveProjects());
		assertEquals("testProject", activeProjects.get(0).getName());
	}
	
	@Test
	public void testGetAllActiveProjectsWhereEndDateOutOfRange() {
		//Arrange
		String sqlInsertProject = "INSERT INTO project (name, to_date) VALUES ('testProject', '2010-05-05')";
		jdbcTemplate.update(sqlInsertProject);
		//Act
		dao.getAllActiveProjects();
		List<Project> activeProjects = new ArrayList<>();
		activeProjects.addAll(dao.getAllActiveProjects());
		//Assert
		assertTrue(activeProjects.size() < 1);
	}
	
	@Test
	public void testGetAllActiveProjectsWhereStartDateOutOfRange() {
		//Arrange
		String sqlInsertProject = "INSERT INTO project (name, from_date) VALUES ('testProject', '2020-05-05')";
		jdbcTemplate.update(sqlInsertProject);
		//Act
		dao.getAllActiveProjects();
		List<Project> activeProjects = new ArrayList<>();
		activeProjects.addAll(dao.getAllActiveProjects());
		//Assert
		assertTrue(activeProjects.size() < 1);
	}

	@Test
	public void testRemoveEmployeeFromProject() {
		//Arrange
		
		//Act
		
		//Assert
		fail("Not yet implemented");
	}

	@Test
	public void testAddEmployeeToProject() {
		//Arrange
		Project myProject = dao.makeNewProject("testProject");
		String sqlInsertEmployee = "INSERT INTO employee (first_name, last_name, birth_date, gender, hire_date)"
				+ "VALUES(?, ?, ?, ?, ?)";
		jdbcTemplate.update(sqlInsertEmployee, "Michael", "Smith", 1990-01-01, 'M', 2004-01-01);
		String myEmployeeQuery = "SELECT * FROM employee WHERE first_name = ?, last_name = ?";
		SqlRowSet employeeRowSet = jdbcTemplate.queryForRowSet(myEmployeeQuery, "Michael", "Smith");
		Employee myEmployee = new Employee();
		myEmployee.setId(employeeRowSet.getLong("employee_id"));
		myEmployee.setDepartmentId(employeeRowSet.getLong("department_id"));
		myEmployee.setFirstName(employeeRowSet.getString("first_name"));
		myEmployee.setLastName(employeeRowSet.getString("last_name"));
		myEmployee.setBirthDay(employeeRowSet.getDate("birth_date").toLocalDate());
		myEmployee.setGender(employeeRowSet.getString("gender").charAt(0));
		myEmployee.setHireDate(employeeRowSet.getDate("hire_date").toLocalDate());

		//Act
		dao.addEmployeeToProject(myProject.getId() , myEmployee.getId());
		//Assert
		assertNotNull()
	}

}
