package org.example.genetic;

public class Point {
    public double x;
    public double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("(%f, %f)", x, y);
    }

    public double distanceTo(Point other) {
        return Math.hypot(this.x - other.x, this.y - other.y);
    }
}