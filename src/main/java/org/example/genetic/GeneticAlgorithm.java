package org.example.genetic;

import java.util.*;
import java.util.stream.Collectors;

public class GeneticAlgorithm {

    // Відстань між двома індивідами (середня відстань між їхніми точками)
    static double distanceBetween(Individual a, Individual b) {
        double totalDistance = 0.0;
        int n = a.points.size();
        for (int i = 0; i < n; i++) {
            totalDistance += a.points.get(i).distanceTo(b.points.get(i));
        }
        return totalDistance / n;
    }

    // Оцінка фітнесу для індивідів
    static void evaluateFitness(Individual individual) {
        double fitness = 0.0;
        int n = individual.points.size();
        double penaltyForOverlap = -2000.0; // Штраф за перекриття
        double minimumDistance = 5.0; // Мінімальна відстань для уникнення перекриття

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                Point a = individual.points.get(i);
                Point b = individual.points.get(j);
                double distance = a.distanceTo(b);

                if (distance < minimumDistance) {
                    fitness += penaltyForOverlap;
                } else {
                    fitness += distance;
                }
            }
        }

        individual.fitness = fitness;
    }

    // Оцінка фітнесу для всієї популяції
    static void evaluatePopulationFitness(List<Individual> population) {
        population.parallelStream().forEach(GeneticAlgorithm::evaluateFitness);
    }

    // Вибір із диверсифікацією (турнірний вибір)
    static Individual tournamentSelectionWithDiversity(List<Individual> population, int tournamentSize, double diversityThreshold) {
        Random random = new Random();
        List<Individual> tournament = new ArrayList<>();
        int maxAttempts = 100;
        int attempts = 0;

        while (tournament.size() < tournamentSize && attempts < maxAttempts) {
            Individual candidate = population.get(random.nextInt(population.size()));
            attempts++;

            // Якщо кандидат відповідає вимогам щодо різноманітності
            if (tournament.stream().allMatch(t -> distanceBetween(t, candidate) > diversityThreshold)) {
                tournament.add(candidate);
            }
        }

        // Якщо турнір не заповнений, додаємо випадкових індивідів
        while (tournament.size() < tournamentSize) {
            tournament.add(population.get(random.nextInt(population.size())));
        }

        return tournament.stream()
                .max(Comparator.comparingDouble(ind -> ind.fitness))
                .orElseThrow();
    }

    // Кросовер
    static Individual crossover(Individual parent1, Individual parent2) {
        Random random = new Random();
        List<Point> childPoints = new ArrayList<>();
        int crossoverPoint = random.nextInt(parent1.points.size());

        // Перша частина від першого батька, друга від другого
        for (int i = 0; i < crossoverPoint; i++) {
            childPoints.add(parent1.points.get(i));
        }
        for (int i = crossoverPoint; i < parent2.points.size(); i++) {
            childPoints.add(parent2.points.get(i));
        }

        return new Individual(childPoints);
    }

    // Мутація
    static void mutate(Individual individual, double mutationRate) {
        Random random = new Random();
        double smallChange = 4;
        for (int i = 0; i < individual.points.size(); i++) {
            if (random.nextDouble() < mutationRate) {
                individual.points.get(i).x += random.nextDouble() * smallChange - (smallChange / 2);
                individual.points.get(i).y += random.nextDouble() * smallChange - (smallChange / 2);
            }
        }
    }

    // Кластеризація популяції
    static List<List<Individual>> clusterizePopulation(List<Individual> population, double clusterThreshold) {
        List<List<Individual>> clusters = new ArrayList<>();
        for (Individual ind : population) {
            boolean addedToCluster = false;
            for (List<Individual> cluster : clusters) {
                if (cluster.stream().anyMatch(c -> distanceBetween(c, ind) < clusterThreshold)) {
                    cluster.add(ind);
                    addedToCluster = true;
                    break;
                }
            }
            if (!addedToCluster) {
                List<Individual> newCluster = new ArrayList<>();
                newCluster.add(ind);
                clusters.add(newCluster);
            }
        }
        return clusters;
    }

    // Вибір наступного покоління з елітарним механізмом
    static List<Individual> selectNextGenerationWithClustering(
            List<Individual> population,
            int eliteCount,
            int populationSize,
            double diversityThreshold
    ) {
        List<Individual> nextPopulation = new ArrayList<>();

        // Зберігаємо еліту
        nextPopulation.addAll(
                population.stream()
                        .sorted(Comparator.comparingDouble(ind -> ind.fitness))
                        .limit(eliteCount)
                        .collect(Collectors.toList())
        );

        List<List<Individual>> clusters = clusterizePopulation(population, diversityThreshold);

        Random random = new Random();
        while (nextPopulation.size() < populationSize) {
            List<Individual> cluster = clusters.get(random.nextInt(clusters.size()));

            Individual parent1 = tournamentSelectionWithDiversity(cluster, 5, diversityThreshold);
            Individual parent2 = tournamentSelectionWithDiversity(cluster, 5, diversityThreshold);

            Individual child = crossover(parent1, parent2);
            mutate(child, 0.15); // Ймовірність мутації

            nextPopulation.add(child); // Додаємо нового індивіда до нового покоління
        }

        return nextPopulation;
    }

    // Створення початкової популяції
    static List<Individual> initializePopulation(int populationSize, int numPoints) {
        List<Individual> population = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < populationSize; i++) {
            List<Point> points = new ArrayList<>();
            for (int j = 0; j < numPoints; j++) {
                points.add(new Point(random.nextDouble() * 100, random.nextDouble() * 100));
            }
            population.add(new Individual(points));
        }
        return population;
    }

    // Функція зупинки
    static boolean stoppingCondition(int generationCount, int maxGenerations) {
        return generationCount >= maxGenerations;
    }

    // Отримання найкращого індивіда
    static Individual getBestSolution(List<Individual> population) {
        return population.stream()
                .max(Comparator.comparingDouble(ind -> ind.fitness))
                .orElseThrow();
    }
}
