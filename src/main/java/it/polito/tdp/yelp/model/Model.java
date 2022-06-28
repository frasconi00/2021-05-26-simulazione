package it.polito.tdp.yelp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	
	private YelpDao dao;
	
	private Map<String, Business> idMap;
	
	private Graph<Business, DefaultWeightedEdge> grafo;
	
	public Model() {
		this.dao = new YelpDao();
	}
	
	public List<String> getCities() {
		
		List<String> cities = new ArrayList<String>(dao.getCities());
		
		Collections.sort(cities);
		
		return cities;
	}
	
	public void creaGrafo(String citta, Integer anno) {
		this.grafo = new SimpleDirectedWeightedGraph<Business, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		//aggiungo vertici
		this.idMap = new HashMap<String, Business>();
		this.dao.getVertici(citta, anno, idMap);
		Graphs.addAllVertices(grafo, idMap.values());
		
		System.out.println("Vertici: "+grafo.vertexSet().size());
		
		//aggiungo archi
		for(Business b1 : grafo.vertexSet()) {
			for(Business b2 : grafo.vertexSet()) {
				
				if(b1.getBusinessId().compareTo(b2.getBusinessId())<0) {
					
					Double diff = dao.getDifferenza(b1, b2, anno);
					
					if(diff>0) {
						Graphs.addEdgeWithVertices(grafo, b2, b1, diff);
					} else if(diff<0) {
						Graphs.addEdgeWithVertices(grafo, b1, b2, (-diff));
					}
					
				}
				
			}
		}
		
		System.out.println("archi: "+grafo.edgeSet().size());
		
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public boolean grafoCreato() {
		if(this.grafo == null)
			return false;
		else
			return true;
	}
	
	public Business doLocaleMigliore() {
		Business migliore = null;
		
		Double max = Double.MIN_VALUE;
		for(Business b : grafo.vertexSet()) {
			Double somma = this.getValutazione(b);
			if(somma>max) {
				max = somma;
				migliore = b;
			}
		}
		
		return migliore;
	}
	
	public Double getValutazione(Business business) {
		
		Double somma=0.0;
		
		for(DefaultWeightedEdge e : grafo.incomingEdgesOf(business)) {
			somma += grafo.getEdgeWeight(e);
		}
		
		for(DefaultWeightedEdge e : grafo.outgoingEdgesOf(business)) {
			somma -= grafo.getEdgeWeight(e);
		}
		
		return somma;
		
	}
	
}
