package com.introtoai.project;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BudgetCartController {

    private final GeneticAlgorithmService gaService;

    public BudgetCartController(GeneticAlgorithmService gaService) {
        this.gaService = gaService;
    }

    @GetMapping("/optimize")
    public Map<String, Object> optimizeCart(@RequestParam double budget) {
        // Run AI with a strict population of 20 and 10 generations
        Map<String, Object> gaResult = gaService.runOptimization(budget);

        // Extract the items to calculate totals
        @SuppressWarnings("unchecked")
        List<GroceryItem> optimizedList = (List<GroceryItem>) gaResult.get("items");

        double totalCost = optimizedList.stream().mapToDouble(GroceryItem::getCost).sum();
        int totalCalories = optimizedList.stream().mapToInt(GroceryItem::getCalories).sum();

        // Add the totals back into the map to send to the UI
        gaResult.put("totalCost", totalCost);
        gaResult.put("totalCalories", totalCalories);

        return gaResult;
    }
}