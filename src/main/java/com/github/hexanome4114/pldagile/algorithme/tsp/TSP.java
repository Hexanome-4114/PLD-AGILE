package com.github.hexanome4114.pldagile.algorithme.tsp;


public interface TSP {

    void searchSolution(int timeLimit, Graph g);

    Integer getSolution(int i);

    int getSolutionCost();
}
