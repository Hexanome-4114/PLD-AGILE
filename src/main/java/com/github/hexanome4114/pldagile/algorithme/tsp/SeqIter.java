package com.github.hexanome4114.pldagile.algorithme.tsp;

import java.util.Collection;
import java.util.Iterator;

public final class SeqIter implements Iterator<Integer> {
    private final Integer[] candidates;
    private int nbCandidates;

    public SeqIter(
            final Collection<Integer> unvisited,
            final int currentVertex,
            final Graph g
    ) {
        this.candidates = new Integer[unvisited.size()];
        for (Integer s : unvisited) {
            if (g.isArc(currentVertex, s)) {
                candidates[nbCandidates++] = s;
            }
        }
    }

    @Override
    public boolean hasNext() {
        return nbCandidates > 0;
    }

    @Override
    public Integer next() {
        nbCandidates--;
        return candidates[nbCandidates];
    }

    @Override
    public void remove() { }
}
