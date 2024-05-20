package org.example.genetic;

import java.util.ArrayList;
import java.util.List;

public class Individual {
    public List<Point> points;
    public double fitness;

    public Individual(List<Point> points) {
        this.points = new ArrayList<>(points);
        this.fitness = 0.0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Individual: ");
        for (Point point : points) {
            sb.append(point.toString()).append(" ");
        }
        sb.append("Fitness: ").append(fitness);
        return sb.toString();
    }
}