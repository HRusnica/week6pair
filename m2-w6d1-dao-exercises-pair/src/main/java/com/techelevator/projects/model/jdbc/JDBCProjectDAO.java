package com.techelevator.projects.model.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.Project;
import com.techelevator.projects.model.ProjectDAO;

public class JDBCProjectDAO implements ProjectDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCProjectDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<Project> getAllActiveProjects() {
		String activeProject = "SELECT * FROM project WHERE (to_date > now() AND from_date < now()) "
				+ "OR (from_date < now() AND to_date IS NULL) "
				+ "OR (to_date > now() AND from_date IS NULL)"
				+ "OR (to_date IS NULL AND from_date IS NULL)";
		SqlRowSet projectRowSet = jdbcTemplate.queryForRowSet(activeProject);
		List<Project> projectList = new ArrayList<>();
		while(projectRowSet.next()){
			Project myProject = new Project();
			myProject = mapRowToProject(projectRowSet);
			projectList.add(myProject);
		}
		return projectList;
	}

	@Override
	public void removeEmployeeFromProject(Long projectId, Long employeeId) {
		String myDeletion = ("DELETE * FROM project_employee WHERE project_id = ? AND employee_id =?");
		jdbcTemplate.update(myDeletion, projectId, employeeId);
	}

	@Override
	public void addEmployeeToProject(Long projectId, Long employeeId) {
		String myAddition = "INSERT INTO project_employee(project_id, employee_id) " +
							"VALUES(?, ?)"; 
		jdbcTemplate.update(myAddition, projectId, employeeId);
	}

	public Project makeNewProject(String projectName){
		Project newProject = new Project();
		newProject.setName(projectName);
		
		String myAddition = "INSERT INTO project (name) VALUES (?) RETURNING project_id";
		newProject.setId((long)jdbcTemplate.queryForObject(myAddition, Long.class, projectName));
		return newProject;
	}
	
	public Project mapRowToProject(SqlRowSet projectRowSet){
		Project myProject = new Project();
		myProject.setId(projectRowSet.getLong("project_id"));
		myProject.setName(projectRowSet.getString("name"));
		if(projectRowSet.getDate("from_date") != null){
		myProject.setStartDate(projectRowSet.getDate("from_date").toLocalDate());
		}
		if(projectRowSet.getDate("to_date") != null){
		myProject.setEndDate(projectRowSet.getDate("to_date").toLocalDate());
		}
		return myProject;
	}
	
}
