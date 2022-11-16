package com.github.hexanome4114.pldagile.algorithme.tsp;

import java.util.Collection;
import java.util.Iterator;

public final class TSP1 extends TemplateTSP {

	@Override
	protected int bound(
			final Integer currentVertex,
			final Collection<Integer> unvisited
	) {
		return 0;
	}

	@Override
	protected Iterator<Integer> iterator(
			final Integer currentVertex,
			final Collection<Integer> unvisited,
			final Graph g
	) {
		return new SeqIter(unvisited, currentVertex, g);
	}

}
