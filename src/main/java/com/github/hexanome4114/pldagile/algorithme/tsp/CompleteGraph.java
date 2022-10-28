package com.github.hexanome4114.pldagile.algorithme.tsp;
import com.github.hexanome4114.pldagile.algorithme.dijkstra.*;

import java.util.HashMap;
import java.util.Map;

public class CompleteGraph implements Graph {
	private static final int MAX_COST = 40;
	private static final int MIN_COST = 10;
	int nbVertices;
	int[][] cost;

	public Map<String, Integer> getMapNomSommetVersIndex() {
		return mapNomSommetVersIndex;
	}

	private Map<String,Integer> mapNomSommetVersIndex;
	//map entre nom des sommets et index dans le tableau utilisé par le TSP

	public Map<Integer, String> getMapIndexVersSommet() {
		return mapIndexVersSommet;
	}

	private Map<Integer,String> mapIndexVersSommet;
	
	/**
	 * Create a complete directed graph such that each edge has a weight within [MIN_COST,MAX_COST]
	 * @param nbVertices
	 */

	public CompleteGraph(int nbVertices, int[][] cost) {
		this.nbVertices = nbVertices;
		this.cost = cost;
	}

	public CompleteGraph(Graphe completeGraph){
		this.nbVertices = completeGraph.getSommets().size();
		cost = new int[nbVertices][nbVertices];
		Sommet unSommet;
		int sommetOrigine;
		int sommetDest;
		mapNomSommetVersIndex = new HashMap<>();
		mapIndexVersSommet = new HashMap<>();
		int iter = 0;

		for (String nom : completeGraph.getSommets().keySet()){
			mapNomSommetVersIndex.put(nom,iter);
			mapIndexVersSommet.put(iter,nom);
			iter++;
		}
		//on itère sur les sommets du graphe
		for (Map.Entry entry : completeGraph.getSommets().entrySet()) {
			unSommet = (Sommet) entry.getValue();
			//Pour chaque sommet, on récupére les arc associés et les ajoute dans la matrice des arcs
			for (Map.Entry<Sommet, Integer> arc : unSommet.getSommetsAdjacents().entrySet()) {
				sommetOrigine = mapNomSommetVersIndex.get(unSommet.getNom());
				sommetDest = mapNomSommetVersIndex.get(arc.getKey().getNom());
				cost[sommetOrigine][sommetDest] = arc.getValue();
			}
			for(int i=0; i<nbVertices; i++)
				cost[i][i] = -1;
		}
	}

	@Override
	public int getNbVertices() {
		return nbVertices;
	}

	@Override
	public int getCost(int i, int j) {
		if (i<0 || i>=nbVertices || j<0 || j>=nbVertices)
			return -1;
		return cost[i][j];
	}

	@Override
	public boolean isArc(int i, int j) {
		if (i<0 || i>=nbVertices || j<0 || j>=nbVertices)
			return false;
		return i != j;
	}

}
