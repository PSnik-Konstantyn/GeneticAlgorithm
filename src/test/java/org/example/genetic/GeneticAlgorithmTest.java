package org.example.genetic;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.*;

public class GeneticAlgorithmTest {

    @Test
    public void testInitializePopulation() {
        int populationSize = 10;
        int numPoints = 5;

        List<Individual> population = GeneticAlgorithm.initializePopulation(populationSize, numPoints);

        assertEquals("Розмір популяції має бути правильним", populationSize, population.size());

        for (Individual ind : population) {
            assertEquals("Кількість точок у кожному індивіді має бути правильною", numPoints, ind.points.size());
        }
    }

    @Test
    public void testEvaluateFitness() {
        Individual individual = new Individual(List.of(
                new Point(0, 0),
                new Point(30, 10)
        ));

        GeneticAlgorithm.evaluateFitness(individual);

        assertTrue("Фітнес індивідуального індивіда має бути більшим за 0", individual.fitness > 0);
    }

    @Test
    public void testTournamentSelection() {
        List<Individual> population = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Individual ind = new Individual(List.of(new Point(i, i)));
            GeneticAlgorithm.evaluateFitness(ind);
            population.add(ind);
        }

        Individual selected = GeneticAlgorithm.tournamentSelectionWithDiversity(population, 3, 1.0);

        assertNotNull("Вибраний індивід через турнір не повинен бути null", selected);
    }

    @Test
    public void testCrossover() {
        Individual parent1 = new Individual(List.of(new Point(0, 0), new Point(1, 1)));
        Individual parent2 = new Individual(List.of(new Point(2, 2), new Point(3, 3)));

        Individual child = GeneticAlgorithm.crossover(parent1, parent2);

        assertNotNull("Нащадок після кросовера не повинен бути null", child);
        assertEquals("Кількість точок у нащадка має бути правильною", parent1.points.size(), child.points.size());
    }


    @Test
    public void testMutation() {
        Individual individual = new Individual(List.of(new Point(0, 0), new Point(1, 1)));
        GeneticAlgorithm.mutate(individual, 0.25);

        boolean mutated = individual.points.stream()
                .anyMatch(point -> point.x != 0 || point.y != 0);

        assertTrue("Після мутації хоча б одна точка має бути змінена", mutated);
    }

    @Test
    public void testSelectNextGenerationWithClustering() {
        List<Individual> population = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Individual ind = new Individual(List.of(new Point(i, i)));
            GeneticAlgorithm.evaluateFitness(ind);
            population.add(ind);
        }

        List<Individual> nextGeneration = GeneticAlgorithm.selectNextGenerationWithClustering(
                population,
                3,
                10,
                1.0
        );

        assertEquals("Розмір нової популяції має бути правильним", 10, nextGeneration.size());
    }

    @Test
    public void testStoppingCondition() {
        int maxGenerations = 10;

        assertTrue("Умова зупинки виконується при 10 поколіннях",
                GeneticAlgorithm.stoppingCondition(10, maxGenerations));
        assertFalse("Умова зупинки не виконується при 5 поколіннях",
                GeneticAlgorithm.stoppingCondition(5, maxGenerations));
    }

    @Test
    public void testGetBestSolution() {
        List<Individual> population = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Individual ind = new Individual(List.of(new Point(i, i)));
            ind.fitness = i * 10;
            population.add(ind);
        }

        Individual best = GeneticAlgorithm.getBestSolution(population);

        assertEquals("Фітнес найкращого індивіда має бути максимальним",
                90.0, best.fitness, 0.01);
    }

}
