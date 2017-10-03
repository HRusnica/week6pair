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
		String sqlInsertProject = "INSERT INTO project (name) VALUES ('test')";
		jdbcTemplate.update(sqlInsertProject);
		//Act
		dao.getAllActiveProjects();
		String testProjectName = dao.getAllActiveProjects().;
		//Assert
		assertNotNull(dao.getAllActiveProjects());
		assertEquals()
	}

	@Test
	public void testRemoveEmployeeFromProject() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddEmployeeToProject() {
		fail("Not yet implemented");
	}

}
