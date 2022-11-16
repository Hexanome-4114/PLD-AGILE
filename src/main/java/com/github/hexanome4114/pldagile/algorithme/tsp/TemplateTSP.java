package com.github.hexanome4114.pldagile.algorithme.tsp;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public abstract class TemplateTSP implements TSP {
	private Integer[] bestSol;
	private Graph g;
	private int bestSolCost;
	private int timeLimit;
	private long startTime;

	public final void searchSolution(final int timeLimit, final Graph g) {
		if (timeLimit <= 0) {
			return;
		}
		startTime = System.currentTimeMillis();
		this.timeLimit = timeLimit;
		this.g = g;
		bestSol = new Integer[g.getNbVertices()];
		Collection<Integer> unvisited
				= new ArrayList<Integer>(g.getNbVertices() - 1);
		for (int i = 1; i < g.getNbVertices(); i++) {
			unvisited.add(i);
		}
		Collection<Integer> visited
				= new ArrayList<Integer>(g.getNbVertices());
		visited.add(0); // The first visited vertex is 0
		bestSolCost = Integer.MAX_VALUE;
		branchAndBound(0, unvisited, visited, 0);
	}

	public final Integer getSolution(final int i) {
		if (g != null && i >= 0 && i < g.getNbVertices()) {
			return bestSol[i];
		}
		return -1;
	}

	public final int getSolutionCost() {
		if (g != null) {
			return bestSolCost;
		}
		return -1;
	}

	protected abstract int bound(
			Integer currentVertex,
			Collection<Integer> unvisited
	);

	protected abstract Iterator<Integer> iterator(
			Integer currentVertex,
			Collection<Integer> unvisited,
			Graph g
	);

	private void branchAndBound(
			final int currentVertex,
			final Collection<Integer> unvisited,
			final Collection<Integer> visited,
			final int currentCost
	) {
		if (System.currentTimeMillis() - startTime > timeLimit) {
			return;
		}
	    if (unvisited.size() == 0) {
	    	if (g.isArc(currentVertex, 0)) {
				if (currentCost + g.getCost(currentVertex, 0)
						< bestSolCost
				) {
	    			visited.toArray(bestSol);
	    			bestSolCost = currentCost + g.getCost(
							currentVertex, 0
					);
	    		}
	    	}
	    } else if (
				currentCost + bound(currentVertex, unvisited)
						< bestSolCost
		) {
	        Iterator<Integer> it = iterator(currentVertex, unvisited, g);
	        while (it.hasNext()) {
				Integer nextVertex = it.next();
				visited.add(nextVertex);
				unvisited.remove(nextVertex);
				branchAndBound(
						nextVertex,
						unvisited,
						visited,
						currentCost + g.getCost(
								currentVertex,
								nextVertex
						)
				);
				visited.remove(nextVertex);
				unvisited.add(nextVertex);
			}
	    }
	}
}
