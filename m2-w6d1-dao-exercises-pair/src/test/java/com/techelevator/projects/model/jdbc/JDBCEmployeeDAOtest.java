package com.techelevator.projects.model.jdbc;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
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

public class JDBCEmployeeDAOtest {
	
	private static SingleConnectionDataSource dataSource;
	private JDBCEmployeeDAO dao;
	private JDBCDepartmentDAO daoD;
	private JDBCProjectDAO daoProject;
	private JdbcTemplate jdbcTemplate;
	private Employee susan;
	private Employee mike;
	private Department departmentOne;
	private Department departmentTwo;
	private Project ourProject;


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
		jdbcTemplate.update("DELETE FROM employee");
		jdbcTemplate.update("DELETE FROM department");
		jdbcTemplate.update("DELETE FROM project");

		dao = new JDBCEmployeeDAO(dataSource);
				
		susan = dao.makeNewEmployee("Susan", "Dolf", LocalDate.of(1990, 02, 02), "F", LocalDate.of(1998, 03, 03));
		//mike = dao.makeNewEmployee("Michael", "Dolfton", LocalDate.of(1990, 02, 02), "M", LocalDate.of(1998, 03, 03));
		daoProject = new JDBCProjectDAO(dataSource);
		ourProject = daoProject.makeNewProject("theProject");
		daoD = new JDBCDepartmentDAO(dataSource);
		departmentOne = daoD.createDepartment("THE Dept. 1");
		departmentTwo = daoD.createDepartment("THE Dept. 2");
	
	}

	/* After each test, we rollback any changes that were made to the database so that
	 * everything is clean for the next test */
	@After
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}

	@Test
	public void testGetAllEmployees() {

			List<Employee> listOfEmp = dao.getAllEmployees();
			
			assertEquals(1, listOfEmp.size());
			assertNotNull(listOfEmp);
			
	}

	@Test
	public void testSearchEmployeesByName() {
	
		List<Employee> empName = dao.searchEmployeesByName("Susan", "Dolf");
		List<Employee> notThere = dao.searchEmployeesByName("Susie", "Q");
		
		assertEquals("Dolf, Susan", empName.get(0).toString());
		assertFalse(notThere.size() > 0);
	}

	@Test
	public void testGetEmployeesByDepartmentId() {
		String updateEmpId = "UPDATE employee SET department_id = (SELECT department_id FROM department WHERE name = 'THE Dept. 1') "
				+ "WHERE first_name = 'Susan'";
		jdbcTemplate.update(updateEmpId);
		dao.changeEmployeeDepartment(susan.getId(), departmentOne.getId());
		List<Employee> empList = new ArrayList<>();
		empList.add(susan);
		assertEquals( "Susan", dao.getEmployeesByDepartmentId(departmentOne.getId()).get(0).getFirstName());
	}

	@Test
	public void testGetEmployeesWithoutProjects() {
		assertNotNull(dao.getEmployeesWithoutProjects());
		assertEquals("Susan", dao.getEmployeesWithoutProjects().get(0).getFirstName());
	}
	
	@Test
	public void testGetEmployeesWithoutProjectsWhenNone() {
		String updateEmpId = "INSERT INTO project_employee (project_id, employee_id) VALUES"
				+ "((SELECT project_id FROM project WHERE name = 'theProject'), "
				+ "(SELECT employee_id FROM employee WHERE first_name = 'Susan'))";
		jdbcTemplate.update(updateEmpId);
		assertTrue(dao.getEmployeesWithoutProjects().size() < 1);
	}

	@Test
	public void testGetEmployeesByProjectId() {
		String updateEmpId = "INSERT INTO project_employee (project_id, employee_id) VALUES"
				+ "((SELECT project_id FROM project WHERE name = 'theProject'), "
				+ "(SELECT employee_id FROM employee WHERE first_name = 'Susan'))";
		jdbcTemplate.update(updateEmpId);
		assertEquals("Susan", dao.getEmployeesByProjectId(ourProject.getId()).get(0).getFirstName());
	}

	@Test
	public void testChangeEmployeeDepartment() {
		String updateEmpId = "UPDATE employee SET department_id = (SELECT department_id FROM department WHERE name = 'THE Dept. 1') "
				+ "WHERE first_name = 'Susan'";
		jdbcTemplate.update(updateEmpId);
		
		dao.changeEmployeeDepartment(susan.getId(), departmentTwo.getId());
		
		Long SusanDeptId = jdbcTemplate.queryForObject("SELECT department_id FROM employee WHERE employee_id = ?", Long.class, susan.getId());

		Long newer = departmentTwo.getId();
		assertEquals(newer, SusanDeptId);
	}

}
