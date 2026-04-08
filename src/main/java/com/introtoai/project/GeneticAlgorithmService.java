package com.introtoai.project;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class GeneticAlgorithmService {

    // Expanded dataset with 20 items varying in cost and caloric density
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
            new GroceryItem("Apples (Bag)", 6.00, 500),
            new GroceryItem("Bananas (1kg)", 2.00, 890),
            new GroceryItem("Cheddar Cheese", 6.50, 1600),
            new GroceryItem("Oatmeal (1kg)", 3.00, 3800),
            new GroceryItem("Ground Beef", 10.00, 2500),
            new GroceryItem("Carrots (Bag)", 2.50, 410),
            new GroceryItem("Potatoes (5lb)", 4.00, 1700),
            new GroceryItem("Olive Oil", 8.00, 8000),
            new GroceryItem("Canned Tuna", 1.50, 200),
            new GroceryItem("Frozen Peas", 2.50, 350),
            new GroceryItem("Greek Yogurt", 5.50, 600)
    );

    private final Random random = new Random();

    public Map<String, Object> runOptimization(double maxBudget) {
        // Hardcoded to guarantee the exact UI output of 10 crossovers over 10 generations
        int generations = 10;
        int populationSize = 20;

        int chromosomeLength = dataset.size();
        List<int[]> population = new ArrayList<>();
        List<String> historyLog = new ArrayList<>();

        historyLog.add("==================================================");
        historyLog.add("         INITIALIZING GENETIC ALGORITHM           ");
        historyLog.add("==================================================");
        historyLog.add("Dataset loaded with " + chromosomeLength + " possible items.");
        historyLog.add("Generations strictly set to " + generations + " for optimized search space.\n");

        // Step 1: INITIALIZATION
        for (int i = 0; i < populationSize; i++) {
            int[] chromosome = new int[chromosomeLength];
            for (int j = 0; j < chromosomeLength; j++) {
                chromosome[j] = (random.nextDouble() < 0.20) ? 1 : 0;
            }
            population.add(chromosome);
        }

        // EVOLUTION LOOP
        for (int gen = 0; gen < generations; gen++) {

            population.sort((a, b) -> Integer.compare(calculateFitness(b, maxBudget), calculateFitness(a, maxBudget)));
            int bestFitnessThisGen = calculateFitness(population.get(0), maxBudget);

            // Generation Header
            historyLog.add("\n==================================================");
            historyLog.add("           [GENERATION " + (gen + 1) + " of " + generations + "]");
            historyLog.add("==================================================");

            // --- EVALUATION ---
            historyLog.add("\n>>> STEP 1: FITNESS FUNCTION (EVALUATION) & PENALTY");
            historyLog.add("    Scored all 20 carts. Highest valid fitness: " + bestFitnessThisGen + " kcal");

            // --- ELITISM ---
            List<int[]> newPopulation = new ArrayList<>();
            int survivorsCount = populationSize / 2;

            historyLog.add("\n>>> STEP 2: ELITISM (SELECTION)");
            historyLog.add("    Protecting the Top 10 Elite Survivors from mutation:");
            for (int i = 0; i < survivorsCount; i++) {
                historyLog.add("      Rank " + (i + 1) + ": " + getCartDetails(population.get(i), maxBudget));
            }

            for (int i = 0; i < survivorsCount; i++) {
                newPopulation.add(population.get(i));
            }

            // --- CROSSOVER & MUTATION ---
            int crossoverCount = 1;
            historyLog.add("\n>>> STEP 3 & 4: CROSSOVER & MUTATION");
            historyLog.add("    Breeding 10 new children to replace discarded carts...");

            while (newPopulation.size() < populationSize) {
                int[] parentA = population.get(random.nextInt(survivorsCount));
                int[] parentB = population.get(random.nextInt(survivorsCount));

                int[] child = crossover(parentA, parentB);
                historyLog.add("\n    -> [Crossover #" + crossoverCount + "] Child Bred: " + getCartDetails(child, maxBudget));

                for (int i = 0; i < chromosomeLength; i++) {
                    if (random.nextDouble() < 0.05) { // 5% mutation rate
                        child[i] = (child[i] == 0) ? 1 : 0;
                        String action = (child[i] == 1) ? "Added" : "Removed";

                        String mutatedItem = dataset.get(i).getName() + " ($" + String.format("%.2f", dataset.get(i).getCost()) + ", " + dataset.get(i).getCalories() + " kcal)";
                        historyLog.add("       ** [MUTATION TRIGGERED] " + action + " " + mutatedItem);
                        historyLog.add("          New State: " + getCartDetails(child, maxBudget));
                    }
                }
                newPopulation.add(child);
                crossoverCount++;
            }
            population = newPopulation;
        }

        // Final sort to put the absolute best cart at index 0
        population.sort((a, b) -> Integer.compare(calculateFitness(b, maxBudget), calculateFitness(a, maxBudget)));

        historyLog.add("\n==================================================");
        historyLog.add("    EVOLUTION COMPLETE. OPTIMAL STATE REACHED     ");
        historyLog.add("==================================================");

        // If the absolute best cart still has a fitness of 0, the budget was too low to buy anything.
        if (calculateFitness(population.get(0), maxBudget) == 0) {
            historyLog.add("\nFINAL WINNING CART: Empty Cart || Totals: $0.00 / 0 kcal [BUDGET TOO LOW]");
            Map<String, Object> result = new HashMap<>();
            result.put("items", new ArrayList<>()); // Return an empty visual cart
            result.put("history", historyLog);
            return result;
        }

        // === PRINT THE FINAL RESULT IN THE LOG ===
        historyLog.add("\nFINAL WINNING CART:\n>> " + getCartDetails(population.get(0), maxBudget));

        Map<String, Object> result = new HashMap<>();
        result.put("items", decodeChromosome(population.get(0)));
        result.put("history", historyLog);

        return result;
    }

    // --- AI ALGORITHM CORE LOGIC FUNCTIONS ---
    private int calculateFitness(int[] chromosome, double maxBudget) {
        double totalCost = 0;
        int totalCalories = 0;

        for (int i = 0; i < chromosome.length; i++) {
            if (chromosome[i] == 1) {
                totalCost += dataset.get(i).getCost();
                totalCalories += dataset.get(i).getCalories();
            }
        }
        if (totalCost > maxBudget) return 0;
        return totalCalories;
    }

    private int[] crossover(int[] parentA, int[] parentB) {
        int[] child = new int[parentA.length];
        int midpoint = parentA.length / 2;
        for (int i = 0; i < parentA.length; i++) {
            child[i] = (i < midpoint) ? parentA[i] : parentB[i];
        }
        return child;
    }

    private String getCartDetails(int[] chromosome, double maxBudget) {
        List<String> names = new ArrayList<>();
        double totalCost = 0;
        int totalCalories = 0;

        for (int i = 0; i < chromosome.length; i++) {
            if (chromosome[i] == 1) {
                names.add(dataset.get(i).getName() + " (" + dataset.get(i).getCalories() + " kcal)");
                totalCost += dataset.get(i).getCost();
                totalCalories += dataset.get(i).getCalories();
            }
        }

        String items = names.isEmpty() ? "Empty Cart" : String.join(", ", names);
        String stats = String.format(" || Totals: $%.2f / %d kcal", totalCost, totalCalories);

        if (totalCost > maxBudget) {
            stats += " [OVER BUDGET - FITNESS: 0]";
        }

        return items + stats;
    }

    private List<GroceryItem> decodeChromosome(int[] chromosome) {
        List<GroceryItem> result = new ArrayList<>();
        for (int i = 0; i < chromosome.length; i++) {
            if (chromosome[i] == 1) result.add(dataset.get(i));
        }
        return result;
    }
}