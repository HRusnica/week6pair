package com.techelevator.projects.model.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.DepartmentDAO;

public class JDBCDepartmentDAO implements DepartmentDAO {
	
	private JdbcTemplate jdbcTemplate;

	public JDBCDepartmentDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Department> getAllDepartments() {
		String departmentNameAndId = "SELECT * FROM department ORDER BY name";
		SqlRowSet deptNameRowSet = jdbcTemplate.queryForRowSet(departmentNameAndId);
		List<Department> allDepts = new ArrayList<>();
		while(deptNameRowSet.next()){
			Department ourDept = new Department();
			ourDept = mapRowtoDept(deptNameRowSet);
			allDepts.add(ourDept);
		}
		return allDepts;
	}

	@Override
	public List<Department> searchDepartmentsByName(String nameSearch) {
		String sqlFindDeptByName = "SELECT * FROM department WHERE name ILIKE ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindDeptByName, "%" + nameSearch + "%");
		List<Department> allDepts = new ArrayList<>(); 
		while(results.next()){
			Department ourDept = mapRowtoDept(results);
			allDepts.add(ourDept);
		}
		return allDepts;
	}

	@Override
	public void updateDepartmentName(Long departmentId, String departmentName) {
		String sqlUpdateName = "UPDATE department SET name = ? WHERE department_id = ?";
		jdbcTemplate.update(sqlUpdateName, departmentName, departmentId);
	}

	@Override
	public Department createDepartment(String departmentName) {
		String sqlCreateNewDept = "INSERT INTO department (name) VALUES (?) RETURNING department_id";
		Long deptId = jdbcTemplate.queryForObject(sqlCreateNewDept, Long.class, departmentName);
		return this.getDepartmentById(deptId);
	}

	@Override
	public Department getDepartmentById(Long id) {
		String sqlFindDeptByID = "SELECT * FROM department WHERE department_id=?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindDeptByID, id);
		if(results.next()) {
			Department ourDept = new Department();
			ourDept.setId(results.getLong("department_id"));
			ourDept.setName(results.getString("name"));
			return ourDept;
		}
		return null;
	}

	
	public Department mapRowtoDept(SqlRowSet deptNameRowSet){
		Department deptNew = new Department();
		deptNew.setId(deptNameRowSet.getLong("department_id"));
		deptNew.setName(deptNameRowSet.getString("name"));
		return deptNew;
	}
}
