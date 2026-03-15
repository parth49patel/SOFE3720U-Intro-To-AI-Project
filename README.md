# SOFE3720U-Intro-To-AI-Project: BudgetCart (AI Grocery Optimizer)

## Project Overview
BudgetCart is AI-driven web application designed to solve the **Knapsack Problem** applied to real-world grocery shopping. Given a strict user-defined budget, the application uses a custom-built **Genetic Algorithm** to find the exact combination of groceries that maximizes total caloric (nutritional) value without exceeding the cost limit.  
To fulfill the course requirements, the AI logic was implemented manually in pure Java without the use of external machine learning libraries.

## Key Features
* **Custom Genetic Algorithm:** Implements Elitism (selection), Single-Point Crossover (breeding), and Bit-Flip Mutation.
* **Live Evolution Logging:** The UI features a real-time terminal that streams the algorithm's step-by-step decision-making process, explicitly showing elitism survivors, crossover children, and mutations across 10 generations.
* **Modular Architecture:** Built on a Client-Server model enforcing strict separation of concerns between the stateless UI and the backend AI engine.
* **Frictionless Execution:** Runs entirely locally via Spring Boot with zero database configuration required.

## Tech Stack
* **Backend Engine:** Java 17, Spring Boot (REST API)
* **Frontend UI:** Vanilla HTML, CSS, JavaScript (Fetch API)
* **Build Tool:** Maven

## How to Run the Project (Live Demo)

### Prerequisites
* Java 17 or higher installed.
* Maven installed (or use your IDE's built-in Maven wrapper).

### Execution Steps
1. Clone this repository to your local machine:
   ```
   git clone https://github.com/parth49patel/SOFE3720U-Intro-To-AI-Project.git
   ```
2. Start the Spring Boot application using Maven:
    ```
    mvn clean spring-boot:run
    ```
3. Once the terminal display **com.introtoai.project.Application : Started Application**, open your web browser and navigate to: **http://localhost:8080**

## How the AI Works
1. Initialization: The system generates a random population of 20 shopping carts.

2. Evaluation: Carts are scored based on total calories. Carts exceeding the budget constraint receive a fitness score of 0.

3. Elitism: The top 10 carts are cloned directly into the next generation.

4. Crossover & Mutation: The remaining 10 slots are filled by breeding parents from the elite pool. Each item in a child cart has a 5% chance of mutating (flipping its inclusion state) to maintain genetic diversity.

5. Convergence: After 10 generations, the Rank 1 cart is mathematically verified as the optimal state and returned to the user.