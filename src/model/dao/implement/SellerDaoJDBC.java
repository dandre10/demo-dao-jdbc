package model.dao.implement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {

	private Connection conn;
	
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	
	@Override
	public void insert(Seller obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO seller "
					  				 + "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
									 + "VALUES " 
					  				 + "(?, ?, ?, ?, ?) ", //placeHolders
					  				 Statement.RETURN_GENERATED_KEYS); //retorna o id do vendedor inserido
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			
			
			int rowsAffected = st.executeUpdate();
			
			if(rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys(); //st.getGenerateKeys -> pega o código da linha inserida. Retorna um objeto ResultSet com 1 ou mais valores
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			} else {
				throw new DbException("Unexpected error! No rows affected!");
			}
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public void update(Seller obj) {
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement("UPDATE seller "
									 + "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
									 + "WHERE Id = ? ");
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			
			st.setInt(6, obj.getId());//id do vendedor (seller)
			
			st.executeUpdate();
		}
		catch(SQLException e){
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement("DELETE FROM seller "
					  				 + "WHERE Id = ? ");
			
			st.setInt(1, id);
			//se rowsAffected = 0 -> id não existe
			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected == 0) {
				System.out.println("Id not exists");
			} else {
				System.out.println("Delete completed");
			}
			
		} catch(SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
		
	}


	private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
		Seller seller = new Seller();
		seller.setId(rs.getInt("Id"));
		seller.setName(rs.getString("Name"));
		seller.setEmail(rs.getString("Email"));
		seller.setBirthDate(rs.getDate("BirthDate"));
		seller.setBaseSalary(rs.getDouble("BaseSalary"));
		
		//associação da tabela vendedor com o departamento
		seller.setDepartment(dep);
		
		return seller;
	}


	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("DepartmentId")); //pega o id do campo DepartmentId no resultset
		dep.setName(rs.getString("DepName"));
		return dep;
	}


	

	@Override
	public Seller findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT seller.*,department.Name as DepName " 
									 + "FROM seller INNER JOIN department "
									 + "ON seller.DepartmentId = department.Id "
									 + "WHERE seller.Id =? ");
			
			st.setInt(1, id);
			rs = st.executeQuery();
			//verificar se o rs tem algum retorno
			if (rs.next()) {
				//criando objetos a partir do resultset - IMPORTANTE
				Department dep = instantiateDepartment(rs);
				
				Seller seller = instantiateSeller(rs, dep);
				
				return seller;
				
			}
			return null;
		} 
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement("SELECT seller.*, department.Name as DepName "
									 + "FROM seller INNER JOIN department "
									 + "ON seller.DepartmentId = department.Id "
									 + "WHERE DepartmentId = ? "
									 + "ORDER BY Name");
			
			st.setInt(1, department.getId());
			
			rs = st.executeQuery();
			
			List<Seller> listSeller = new ArrayList<Seller>();
			
			//controle para não instanciar outro departamento
			//instancia varios vendedores sem repetir o departamento
			Map<Integer, Department> map = new HashMap<>();
			
			while (rs.next()) {
				
				Department dep = map.get(rs.getInt("DepartmentId"));
				
				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}
								
				Seller seller = instantiateSeller(rs, dep);
				listSeller.add(seller);
				
			}
			return listSeller;
			
		} catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}
	
	
	@Override
	public List<Seller> findAll() {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			
			st = conn.prepareStatement("SELECT seller.*, department.Name as DepName "
									 + "FROM seller INNER JOIN department "
									 + "ON seller.DepartmentId = department.Id "
									 + "ORDER BY Name");
			
			rs = st.executeQuery();
			
			List<Seller> listSeller = new ArrayList<Seller>();
			
			Map<Integer, Department> map = new HashMap<Integer, Department>();
			
			
			while(rs.next()) {
				Department dep = map.get(rs.getInt("DepartmentId"));
				
				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
					
				}
				Seller seller = instantiateSeller(rs, dep);
				listSeller.add(seller);
			}
			return listSeller;
			
		} catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally{
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

}
