package application;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class Program2 {

	public static void main(String[] args) {
		
		
		DepartmentDao departmentDao = DaoFactory.createDepartmentDao();
		
		System.out.println("=== TEST 1: department insert ===");
		
		Department department = new Department(null, "Payments");
		departmentDao.insert(department); //insere o objeto department criado acima no banco de dados
		System.out.println("Inserted new Id = " + department.getId());
		
		
	}

}
