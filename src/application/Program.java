package application;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class Program {

	public static void main(String[] args) {
		/*
		Connection conn = DB.getConnection();
		DB.closeConnection();
		 
		
		Department department = new Department(1, "Books");
		
		Seller seller = new Seller(21, "Bob", "Bob@gmail.com", new Date(), 3000.0, department);
		
		SellerDao sellerDao = DaoFactory.createSellerDao();
		*/
		
		SellerDao sellerDao = DaoFactory.createSellerDao();
		
		System.out.println("=== TEST 1: seller findById ===");
		Seller seller = sellerDao.findById(3);
		
		System.out.println(seller);
		
		
		System.out.println("\n=== TEST 2: seller findByDepartment ===");
		
		Department department = new Department(2, null);
		
		List<Seller> listSeller = sellerDao.findByDepartment(department);
		
		for (Seller seller2 : listSeller) {
			System.out.println(seller2);
		}
		
System.out.println("\n=== TEST 3: seller findAll ===");
				
		listSeller = sellerDao.findAll();
		
		for (Seller seller2 : listSeller) {
			System.out.println(seller2);
		}
		
	}

}
