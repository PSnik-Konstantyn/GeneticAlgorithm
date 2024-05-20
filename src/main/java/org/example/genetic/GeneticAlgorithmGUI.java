package org.example.genetic;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.layout.Pane;
import javafx.geometry.Pos;

import java.util.*;

public class GeneticAlgorithmGUI extends Application {
    private Pane graphPane;
    private TextField populationSizeField;
    private TextField numPointsField;
    private TextArea outputArea;
    private List<Individual> population;
    private Button startButton;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Генетичний Алгоритм");

        VBox mainLayout = new VBox(10);
        mainLayout.setAlignment(Pos.CENTER);

        HBox inputLayout = new HBox(10);
        inputLayout.setAlignment(Pos.CENTER);

        populationSizeField = new TextField("20");
        populationSizeField.setPromptText("Розмір популяції");

        numPointsField = new TextField("40");
        numPointsField.setPromptText("Кількість точок");

        startButton = new Button("Запустити");
        startButton.setOnAction(event -> startAlgorithm());

        inputLayout.getChildren().addAll(populationSizeField, numPointsField, startButton);

        graphPane = new Pane();
        graphPane.setMinSize(400, 400);

        outputArea = new TextArea();
        outputArea.setEditable(false);

        mainLayout.getChildren().addAll(inputLayout, graphPane, outputArea);

        primaryStage.setScene(new Scene(mainLayout, 500, 500));
        primaryStage.show();
    }

    private void startAlgorithm() {
        int populationSize = Integer.parseInt(populationSizeField.getText());
        int numPoints = Integer.parseInt(numPointsField.getText());
        int maxGenerations = 10;
        double diversityThreshold = 10.0;

        population = GeneticAlgorithm.initializePopulation(populationSize, numPoints);

        GeneticAlgorithm.evaluatePopulationFitness(population);

        Individual bestOverallSolution = GeneticAlgorithm.getBestSolution(population);
        System.out.println("initial: " + bestOverallSolution.fitness);

        outputArea.setText("Покоління: 0\nФітнес найкращого індивіда: " + bestOverallSolution.fitness + "\n");

        int generation = 0;
        double x = bestOverallSolution.fitness;

        while (!GeneticAlgorithm.stoppingCondition(generation, maxGenerations)) {

            generation++;

            population = GeneticAlgorithm.selectNextGenerationWithClustering(
                    population,
                    3,
                    populationSize,
                    diversityThreshold
            );

            GeneticAlgorithm.evaluatePopulationFitness(population);

            Individual bestSolution = GeneticAlgorithm.getBestSolution(population);

            if (bestSolution.fitness > x) {
                x = bestSolution.fitness;
                bestOverallSolution = bestSolution;
            }

            outputArea.appendText("Покоління: " + generation + "\nФітнес найкращого індивіда: " + bestSolution.fitness + "\n");

        }

        graphPane.getChildren().clear();

        for (Point p : bestOverallSolution.points) {
            Circle circle = new Circle(p.x * 4, p.y * 4, 5);
            graphPane.getChildren().add(circle);
        }

        System.out.println(x);
        outputArea.appendText("Кінцевий результат:\nФітнес найкращого індивіда: " + x + "\n");
    }

}
