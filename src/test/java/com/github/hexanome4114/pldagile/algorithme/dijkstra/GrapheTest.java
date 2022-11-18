package com.github.hexanome4114.pldagile.algorithme.dijkstra;

import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GrapheTest {

    @Test
    void calculerCheminplusCourtDepuisSource() {
        // Test 1 : Un seul sommet
        Graphe test1Graphe = new Graphe();
        Sommet test1Sommet1 = new Sommet("sommet1");
        test1Graphe.ajouterSommet(test1Sommet1);
        test1Graphe.calculerCheminplusCourtDepuisSource(test1Graphe,
                test1Sommet1);

        List<Sommet> test1Sommet1CheminsPlusCourt = new LinkedList();

        // Test 2 : Arbre simple
        Graphe test2Graphe = new Graphe();
        Sommet test2Sommet1 = new Sommet("sommet1");
        Sommet test2Sommet2 = new Sommet("sommet2");
        Sommet test2Sommet3 = new Sommet("sommet3");
        Sommet test2Sommet4 = new Sommet("sommet4");
        Sommet test2Sommet5 = new Sommet("sommet5");
        Sommet test2Sommet6 = new Sommet("sommet6");
        test2Sommet1.addDestination(test2Sommet2, 1);
        test2Sommet1.addDestination(test2Sommet4, 1);
        test2Sommet1.addDestination(test2Sommet5, 1);
        test2Sommet2.addDestination(test2Sommet1, 1);
        test2Sommet2.addDestination(test2Sommet3, 1);
        test2Sommet3.addDestination(test2Sommet2, 1);
        test2Sommet4.addDestination(test2Sommet1, 1);
        test2Sommet5.addDestination(test2Sommet1, 1);
        test2Sommet5.addDestination(test2Sommet6, 1);
        test2Sommet6.addDestination(test2Sommet5, 1);
        test2Graphe.ajouterSommet(test2Sommet1);
        test2Graphe.ajouterSommet(test2Sommet2);
        test2Graphe.ajouterSommet(test2Sommet3);
        test2Graphe.ajouterSommet(test2Sommet4);
        test2Graphe.ajouterSommet(test2Sommet5);
        test2Graphe.ajouterSommet(test2Sommet6);
        test2Graphe.calculerCheminplusCourtDepuisSource(test2Graphe,
                test2Sommet1);

        List<Sommet> test2Sommet1CheminsPlusCourt = new LinkedList();
        List<Sommet> test2Sommet2CheminsPlusCourt = new LinkedList();
        List<Sommet> test2Sommet3CheminsPlusCourt = new LinkedList();
        List<Sommet> test2Sommet4CheminsPlusCourt = new LinkedList();
        List<Sommet> test2Sommet5CheminsPlusCourt = new LinkedList();
        List<Sommet> test2Sommet6CheminsPlusCourt = new LinkedList();
        test2Sommet2CheminsPlusCourt.add(test2Sommet1);
        test2Sommet3CheminsPlusCourt.add(test2Sommet1);
        test2Sommet3CheminsPlusCourt.add(test2Sommet2);
        test2Sommet4CheminsPlusCourt.add(test2Sommet1);
        test2Sommet5CheminsPlusCourt.add(test2Sommet1);
        test2Sommet6CheminsPlusCourt.add(test2Sommet1);
        test2Sommet6CheminsPlusCourt.add(test2Sommet5);

        // Test 3 : Arbre simple
        Graphe test3Graphe = new Graphe();
        Sommet test3Sommet1 = new Sommet("sommet1");
        Sommet test3Sommet2 = new Sommet("sommet2");
        Sommet test3Sommet3 = new Sommet("sommet3");
        Sommet test3Sommet4 = new Sommet("sommet4");
        Sommet test3Sommet5 = new Sommet("sommet5");
        test3Sommet1.addDestination(test3Sommet2, 1);
        test3Sommet1.addDestination(test3Sommet3, 1);
        test3Sommet1.addDestination(test3Sommet5, 1);
        test3Sommet2.addDestination(test3Sommet1, 1);
        test3Sommet2.addDestination(test3Sommet3, 1);
        test3Sommet3.addDestination(test3Sommet1, 1);
        test3Sommet3.addDestination(test3Sommet2, 1);
        test3Sommet3.addDestination(test3Sommet4, 1);
        test3Sommet4.addDestination(test3Sommet3, 1);
        test3Sommet4.addDestination(test3Sommet5, 1);
        test3Sommet5.addDestination(test3Sommet1, 1);
        test3Sommet5.addDestination(test3Sommet4, 1);
        test3Graphe.ajouterSommet(test3Sommet1);
        test3Graphe.ajouterSommet(test3Sommet2);
        test3Graphe.ajouterSommet(test3Sommet3);
        test3Graphe.ajouterSommet(test3Sommet4);
        test3Graphe.ajouterSommet(test3Sommet5);
        test3Graphe.calculerCheminplusCourtDepuisSource(test3Graphe,
                test3Sommet1);

        List<Sommet> test3Sommet1CheminsPlusCourt = new LinkedList();
        List<Sommet> test3Sommet2CheminsPlusCourt = new LinkedList();
        List<Sommet> test3Sommet3CheminsPlusCourt = new LinkedList();
        List<Sommet> test3Sommet4CheminsPlusCourt = new LinkedList();
        List<Sommet> test3Sommet5CheminsPlusCourt = new LinkedList();
        List<Sommet> test3Sommet6CheminsPlusCourt = new LinkedList();
        test3Sommet2CheminsPlusCourt.add(test3Sommet1);
        test3Sommet3CheminsPlusCourt.add(test3Sommet1);
        test3Sommet4CheminsPlusCourt.add(test3Sommet1);
        test3Sommet4CheminsPlusCourt.add(test3Sommet3);
        test3Sommet5CheminsPlusCourt.add(test3Sommet1);

        // Test 4 : Sommet innateignable
        Graphe test4Graphe = new Graphe();
        Sommet test4Sommet1 = new Sommet("sommet1");
        Sommet test4Sommet2 = new Sommet("sommet2");
        test4Graphe.ajouterSommet(test4Sommet1);
        test4Graphe.ajouterSommet(test4Sommet2);
        test4Graphe.calculerCheminplusCourtDepuisSource(test4Graphe,
                test4Sommet1);

        List<Sommet> test4Sommet1CheminsPlusCourt = new LinkedList();
        List<Sommet> test4Sommet2CheminsPlusCourt = new LinkedList();

        // Test 5 : Avec une boucle
        Graphe test5Graphe = new Graphe();
        Sommet test5Sommet1 = new Sommet("sommet1");
        Sommet test5Sommet2 = new Sommet("sommet2");
        Sommet test5Sommet3 = new Sommet("sommet3");
        test5Sommet1.addDestination(test5Sommet2, 1);
        test5Sommet1.addDestination(test5Sommet3, 1);
        test5Sommet2.addDestination(test5Sommet1, 1);
        test5Sommet2.addDestination(test5Sommet2, 1);
        test5Sommet2.addDestination(test5Sommet3, 1);
        test5Sommet3.addDestination(test5Sommet1, 1);
        test5Sommet3.addDestination(test5Sommet2, 1);
        test5Graphe.ajouterSommet(test5Sommet1);
        test5Graphe.ajouterSommet(test5Sommet2);
        test5Graphe.ajouterSommet(test5Sommet3);
        test5Graphe.calculerCheminplusCourtDepuisSource(test5Graphe,
                test5Sommet1);

        List<Sommet> test5Sommet1CheminsPlusCourt = new LinkedList();
        List<Sommet> test5Sommet2CheminsPlusCourt = new LinkedList();
        List<Sommet> test5Sommet3CheminsPlusCourt = new LinkedList();
        test5Sommet2CheminsPlusCourt.add(test5Sommet1);
        test5Sommet3CheminsPlusCourt.add(test5Sommet1);

        assertAll(
                // Test 1 : Un seul sommet
                () -> assertEquals(0, test1Sommet1.getDistance()),
                () -> assertEquals(test1Sommet1CheminsPlusCourt,
                        test1Sommet1.getCheminPlusCourt()),

                // Test 2 : Arbre simple
                () -> assertEquals(0, test2Sommet1.getDistance()),
                () -> assertEquals(1, test2Sommet2.getDistance()),
                () -> assertEquals(2, test2Sommet3.getDistance()),
                () -> assertEquals(1, test2Sommet4.getDistance()),
                () -> assertEquals(1, test2Sommet5.getDistance()),
                () -> assertEquals(2, test2Sommet6.getDistance()),
                () -> assertEquals(test2Sommet1CheminsPlusCourt,
                        test2Sommet1.getCheminPlusCourt()),
                () -> assertEquals(test2Sommet2CheminsPlusCourt,
                        test2Sommet2.getCheminPlusCourt()),
                () -> assertEquals(test2Sommet3CheminsPlusCourt,
                        test2Sommet3.getCheminPlusCourt()),
                () -> assertEquals(test2Sommet4CheminsPlusCourt,
                        test2Sommet4.getCheminPlusCourt()),
                () -> assertEquals(test2Sommet5CheminsPlusCourt,
                        test2Sommet5.getCheminPlusCourt()),
                () -> assertEquals(test2Sommet6CheminsPlusCourt,
                        test2Sommet6.getCheminPlusCourt()),

                // Test 3 : Cycle
                () -> assertEquals(0, test2Sommet1.getDistance()),
                () -> assertEquals(1, test3Sommet2.getDistance()),
                () -> assertEquals(1, test3Sommet3.getDistance()),
                () -> assertEquals(2, test3Sommet4.getDistance()),
                () -> assertEquals(1, test3Sommet5.getDistance()),
                () -> assertEquals(test3Sommet1CheminsPlusCourt,
                        test3Sommet1.getCheminPlusCourt()),
                () -> assertEquals(test3Sommet2CheminsPlusCourt,
                        test3Sommet2.getCheminPlusCourt()),
                () -> assertEquals(test3Sommet3CheminsPlusCourt,
                        test3Sommet3.getCheminPlusCourt()),
                () -> assertEquals(test3Sommet4CheminsPlusCourt,
                        test3Sommet4.getCheminPlusCourt()),
                () -> assertEquals(test3Sommet5CheminsPlusCourt,
                        test3Sommet5.getCheminPlusCourt()),

                // Test 4 : Innateignable
                () -> assertEquals(0, test4Sommet1.getDistance()),
                () -> assertEquals(Integer.MAX_VALUE,
                        test4Sommet2.getDistance()),
                () -> assertEquals(test4Sommet1CheminsPlusCourt,
                        test4Sommet1.getCheminPlusCourt()),
                () -> assertEquals(test4Sommet2CheminsPlusCourt,
                        test4Sommet2.getCheminPlusCourt()),

                // Test 5 : Innateignable
                () -> assertEquals(0, test5Sommet1.getDistance()),
                () -> assertEquals(1, test5Sommet2.getDistance()),
                () -> assertEquals(1, test5Sommet3.getDistance()),
                () -> assertEquals(test5Sommet1CheminsPlusCourt,
                        test5Sommet1.getCheminPlusCourt()),
                () -> assertEquals(test5Sommet2CheminsPlusCourt,
                        test5Sommet2.getCheminPlusCourt()),
                () -> assertEquals(test5Sommet2CheminsPlusCourt,
                        test5Sommet3.getCheminPlusCourt())
        );
    }
}
