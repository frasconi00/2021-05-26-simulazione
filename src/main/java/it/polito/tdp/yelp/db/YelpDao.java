package it.polito.tdp.yelp.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.yelp.model.Adiacenza;
import it.polito.tdp.yelp.model.Business;
import it.polito.tdp.yelp.model.Review;
import it.polito.tdp.yelp.model.User;

public class YelpDao {

	public List<Business> getAllBusiness(){
		String sql = "SELECT * FROM Business";
		List<Business> result = new ArrayList<Business>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Business business = new Business(res.getString("business_id"), 
						res.getString("full_address"),
						res.getString("active"),
						res.getString("categories"),
						res.getString("city"),
						res.getInt("review_count"),
						res.getString("business_name"),
						res.getString("neighborhoods"),
						res.getDouble("latitude"),
						res.getDouble("longitude"),
						res.getString("state"),
						res.getDouble("stars"));
				result.add(business);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Review> getAllReviews(){
		String sql = "SELECT * FROM Reviews";
		List<Review> result = new ArrayList<Review>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Review review = new Review(res.getString("review_id"), 
						res.getString("business_id"),
						res.getString("user_id"),
						res.getDouble("stars"),
						res.getDate("review_date").toLocalDate(),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("review_text"));
				result.add(review);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<User> getAllUsers(){
		String sql = "SELECT * FROM Users";
		List<User> result = new ArrayList<User>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				User user = new User(res.getString("user_id"),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("name"),
						res.getDouble("average_stars"),
						res.getInt("review_count"));
				
				result.add(user);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> getCities() {
		String sql="SELECT DISTINCT city "
				+ "FROM business";
		List<String> result = new ArrayList<String>();
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while(res.next()) {
				result.add(res.getString("city"));
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	public void getVertici(String citta, Integer anno, Map<String, Business> idMap) {
		String sql="SELECT b.* "
				+ "FROM reviews r, business b "
				+ "WHERE r.business_id = b.business_id "
				+ "AND YEAR(r.review_date) = ? "
				+ "AND b.city = ? "
				+ "GROUP BY b.business_id";
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			st.setString(2, citta);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Business business = new Business(res.getString("business_id"), 
						res.getString("full_address"),
						res.getString("active"),
						res.getString("categories"),
						res.getString("city"),
						res.getInt("review_count"),
						res.getString("business_name"),
						res.getString("neighborhoods"),
						res.getDouble("latitude"),
						res.getDouble("longitude"),
						res.getString("state"),
						res.getDouble("stars"));
				
				if(!idMap.containsKey(business.getBusinessId())) {
					idMap.put(business.getBusinessId(), business);
				}
				
			}
			res.close();
			st.close();
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
//	public Double getDifferenza(Business b1, Business b2, Integer anno) {
//		String sql="SELECT (AVG(r1.stars) - AVG(r2.stars)) AS differenza "
//				+ "FROM reviews r1, reviews r2 "
//				+ "WHERE r1.business_id=? "
//				+ "AND YEAR(r1.review_date)=? "
//				+ "AND r2.business_id=? "
//				+ "AND YEAR(r2.review_date)=?";
//		Double diff = null;
//		Connection conn = DBConnect.getConnection();
//		try {
//			PreparedStatement st = conn.prepareStatement(sql);
//			st.setString(1, b1.getBusinessId());
//			st.setInt(2, anno);
//			st.setString(3, b2.getBusinessId());
//			st.setInt(4, anno);
//			ResultSet res = st.executeQuery();
//			while(res.next()) {
//				diff = res.getDouble("differenza");
//			}
//			conn.close();
//			return diff;
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return null;
//		}
//	}
	
	public List<Adiacenza> getAdiacenze(String citta, Integer anno, Map<String, Business> idMap) {
		String sql="SELECT r1.business_id, r2.business_id, (AVG(r1.stars)-AVG(r2.stars)) AS differenza "
				+ "FROM reviews r1, reviews r2, business b1, business b2 "
				+ "WHERE r1.business_id = b1.business_id AND r2.business_id = b2.business_id "
				+ "AND r1.business_id <> r2.business_id "
				+ "AND b1.city=? AND b2.city=b1.city "
				+ "AND YEAR(r1.review_date)=? AND YEAR(r2.review_date) = YEAR(r1.review_date) "
				+ "GROUP BY r1.business_id, r2.business_id "
				+ "HAVING differenza > 0";
		List<Adiacenza> result = new ArrayList<Adiacenza>();
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, citta);
			st.setInt(2, anno);
			ResultSet res = st.executeQuery();
			while(res.next()) {
				Business partenza = idMap.get(res.getString("r2.business_id"));
				Business arrivo = idMap.get(res.getString("r1.business_id"));
				Double peso = res.getDouble("differenza");
				
				result.add(new Adiacenza(partenza, arrivo, peso));
			}
			
			conn.close();
			return result;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	
}
