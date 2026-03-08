package com.introtoai.project;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class GeneticAlgorithmService {

    // Hardcoded dataset to keep it completely stateless
    private final List<GroceryItem> dataset = Arrays.asList(
            new GroceryItem("Rice (1kg)", 3.50, 1300),
            new GroceryItem("Eggs (1 Dozen)", 4.00, 840),
            new GroceryItem("Chicken Breast", 12.00, 1100),
            new GroceryItem("Beans (Canned)", 1.50, 350),
            new GroceryItem("Spinach", 3.00, 50),
            new GroceryItem("Pasta", 2.00, 1600),
            new GroceryItem("Peanut Butter", 5.00, 2800),
            new GroceryItem("Milk (2L)", 4.50, 1000),
            new GroceryItem("Bread", 3.00, 800),
            new GroceryItem("Apples (Bag)", 6.00, 500)
    );

    private final Random random = new Random();

    public Map<String, Object> runOptimization(double maxBudget, int populationSize, int generations) {
        int chromosomeLength = dataset.size();
        List<int[]> population = new ArrayList<>();
        List<String> historyLog = new ArrayList<>(); // <-- New list to store steps

        for (int i = 0; i < populationSize; i++) {
            int[] chromosome = new int[chromosomeLength];
            for (int j = 0; j < chromosomeLength; j++) {
                chromosome[j] = random.nextInt(2);
            }
            population.add(chromosome);
        }

        for (int gen = 0; gen < generations; gen++) {
            population.sort((a, b) -> Integer.compare(calculateFitness(b, maxBudget), calculateFitness(a, maxBudget)));

            // === Record the steps for the UI ===
            if (gen % 10 == 0) {
                int bestFitnessThisGen = calculateFitness(population.get(0), maxBudget);
                historyLog.add("Generation " + gen + " | Best Fitness: " + bestFitnessThisGen + " kcal");
            }

            List<int[]> newPopulation = new ArrayList<>();
            int survivorsCount = populationSize / 2;
            for (int i = 0; i < survivorsCount; i++) {
                newPopulation.add(population.get(i));
            }

            while (newPopulation.size() < populationSize) {
                int[] parentA = population.get(random.nextInt(survivorsCount));
                int[] parentB = population.get(random.nextInt(survivorsCount));

                int[] child = crossover(parentA, parentB);
                mutate(child, 0.05);
                newPopulation.add(child);
            }
            population = newPopulation;
        }

        population.sort((a, b) -> Integer.compare(calculateFitness(b, maxBudget), calculateFitness(a, maxBudget)));
        historyLog.add("-> OPTIMIZATION COMPLETE.");

        // Pack both the items and the log into a Map
        Map<String, Object> result = new HashMap<>();
        result.put("items", decodeChromosome(population.get(0)));
        result.put("history", historyLog);

        return result;
    }

    // AI Logic: Fitness Function
    private int calculateFitness(int[] chromosome, double maxBudget) {
        double totalCost = 0;
        int totalCalories = 0;

        for (int i = 0; i < chromosome.length; i++) {
            if (chromosome[i] == 1) {
                totalCost += dataset.get(i).getCost();
                totalCalories += dataset.get(i).getCalories();
            }
        }
        // Constraint: Exceeding budget kills the chromosome
        if (totalCost > maxBudget) return 0;
        return totalCalories;
    }

    // AI Logic: Crossover
    private int[] crossover(int[] parentA, int[] parentB) {
        int[] child = new int[parentA.length];
        int midpoint = parentA.length / 2;
        for (int i = 0; i < parentA.length; i++) {
            child[i] = (i < midpoint) ? parentA[i] : parentB[i];
        }
        return child;
    }

    // AI Logic: Mutation
    private void mutate(int[] chromosome, double mutationRate) {
        for (int i = 0; i < chromosome.length; i++) {
            if (random.nextDouble() < mutationRate) {
                chromosome[i] = (chromosome[i] == 0) ? 1 : 0; // Flip bit
            }
        }
    }

    private List<GroceryItem> decodeChromosome(int[] chromosome) {
        List<GroceryItem> result = new ArrayList<>();
        for (int i = 0; i < chromosome.length; i++) {
            if (chromosome[i] == 1) result.add(dataset.get(i));
        }
        return result;
    }
}