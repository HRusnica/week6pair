package com.techelevator.projects.model.jdbc;

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


public class JDBCDepartmentDAOtest {

	private static SingleConnectionDataSource dataSource;
	private JDBCDepartmentDAO dao;
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
		jdbcTemplate.update("DELETE FROM employee");
		jdbcTemplate.update("DELETE FROM department");

		dao = new JDBCDepartmentDAO(dataSource);
	}

	/* After each test, we rollback any changes that were made to the database so that
	 * everything is clean for the next test */
	@After
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}
	@Test
	public void testGetAllDepartments() {
		int numOfExistingDept = dao.getAllDepartments().size();
		String deptName = "MY NEW TEST DEPT";
		Department newDept = dao.createDepartment(deptName);
		String deptName2 = "THE SECOND ONE";
		Department newDept2 = dao.createDepartment(deptName2);
		
		List<Department> deptList = dao.getAllDepartments();

		assertNotNull(deptList);
		assertEquals(deptList.size(), 2 + numOfExistingDept);
		
	}

	@Test
	public void testSearchDepartmentsByName() {
		//Arrange
		String departmentName = "testDeptName";
		Department myDepartment = dao.createDepartment(departmentName);
		//Act
		List<Department> departments = dao.searchDepartmentsByName(departmentName);
		//Assert
		assertNotNull(departments);
		assertEquals(1, departments.size());
		assertEquals(myDepartment.getId(), departments.get(0).getId());
	}

	@Test
	public void testUpdateDepartmentName() {
		//Arrange
		Department myDepartment = dao.createDepartment("departmentName");
		//Act
		dao.updateDepartmentName(myDepartment.getId(), "newName");
		myDepartment = dao.getDepartmentById(myDepartment.getId());
		//Assert
		assertEquals("newName", myDepartment.getName());
	}

	@Test
	public void testCreateDepartment() {
		String deptName = "MY NEW TEST DEPT";
		Department newDept = dao.createDepartment(deptName);
		
		assertNotNull(newDept);
		SqlRowSet results =jdbcTemplate.queryForRowSet("SELECT * FROM department");
		assertTrue("There were no departments in the database", results.next());
		assertEquals(deptName, results.getString("name"));
		assertEquals(newDept.getId(), (Long)results.getLong("department_id"));
		assertFalse("Too many rows", results.next());
	}

	@Test
	public void testGetDepartmentById() {
		//Arrange
		Department myDept = dao.createDepartment("deptName");
		//Act
		Department dudeSweet = dao.getDepartmentById(myDept.getId());
		//Assert
		assertNotNull(myDept.getId());
		assertEquals("deptName", dudeSweet.getName());
		
		
				
	}

}
