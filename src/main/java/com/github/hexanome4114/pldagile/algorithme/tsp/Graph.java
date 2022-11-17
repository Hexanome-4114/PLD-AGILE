package com.github.hexanome4114.pldagile.algorithme.tsp;


public interface Graph {
    /**
     * @return the number of vertices in <code>this</code>
     */
    int getNbVertices();

    /**
     * @param i
     * @param j
     * @return the cost of arc (i,j) if (i,j) is an arc; -1 otherwise
     */
    int getCost(int i, int j);

    /**
     *
     * @return minimum cost of graph arcs
     */
    int getMinCost();

    /**
     * @param i
     * @param j
     * @return true if <code>(i,j)</code> is an arc of <code>this</code>
     */
    boolean isArc(int i, int j);
}
