package com.github.hexanome4114.pldagile.algorithme.tsp;

import java.util.Map;

public final class GrapheTSP implements Graph {
    private final int nbVertices;
    private final int[][] cost;

    private Map<String, Integer> mapNomSommetVersIndex;
    //map entre nom des sommets et index dans le tableau utilisé par le TSP

    private Map<Integer, String> mapIndexVersNomSommet;

    public GrapheTSP(int nbVertices, int[][] cost,
                     Map<String, Integer> mapNomSommetVersIndex, Map<Integer, String> mapIndexVersNomSommet) {
        this.nbVertices = nbVertices;
        this.cost = cost;
        this.mapNomSommetVersIndex = mapNomSommetVersIndex;
        this.mapIndexVersNomSommet = mapIndexVersNomSommet;
    }

    @Override
    public int getNbVertices() {
        return nbVertices;
    }

    @Override
    public int getCost(final int i, final int j) {
        if (i < 0 || i >= nbVertices || j < 0 || j >= nbVertices) {
            return -1;
        }
        return cost[i][j];
    }

    public Map<String, Integer> getMapNomSommetVersIndex() {
        return mapNomSommetVersIndex;
    }

    public Map<Integer, String> getMapIndexVersNomSommet() {
        return mapIndexVersNomSommet;
    }

    @Override
    public boolean isArc(final int i, final int j) {
        if (i < 0 || i >= nbVertices || j < 0 || j >= nbVertices || cost[i][j] < 0) {
            return false;
        }
        return i != j;
    }

}
