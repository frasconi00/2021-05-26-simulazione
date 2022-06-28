package it.polito.tdp.yelp.model;

public class TestModel {

	public static void main(String[] args) {
		Model m = new Model();
//		m.creaGrafo("Wickenburg", 2009);
//		m.creaGrafo("Phoenix", 2005);
		m.creaGrafo("Tempe", 2013);
		
		Business migliore = m.doLocaleMigliore();
		System.out.println(migliore);
	}

}
