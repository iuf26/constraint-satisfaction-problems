package csp.robot.models;

import csp.model.*;
import csp.robot.algorithms.AlgorithmArcConsistency4;
import csp.robot.constraints.AdjacencyConstraint;
import csp.robot.constraints.EnergyConstraint;
import csp.robot.constraints.RestrictedCellsConstraint;
import java.util.*;
import java.util.function.Predicate;

public class RobotGridCsp implements CspProblem{
    private final int rows, cols;
    private final int energyLimit;
    private final boolean[][] restrictedCells;
    private final int[][] targets;
    private final List<int[]> path = new ArrayList<>();
    private CSP csp;
    private final Map<Variable, Set<Object>> domains;

    private final Map<Variable, Set<Object>> domainsForTrackAndAdjust;
    List<Variable> variables = new ArrayList<>();
    List<Constraint> constraints = new ArrayList<>();

    public RobotGridCsp(int rows, int cols, int energyLimit, boolean[][] restrictedCells, int[][] targets) {
        this.rows = rows;
        this.cols = cols;
        this.energyLimit = energyLimit;
        this.restrictedCells = restrictedCells;
        this.targets = targets;
        this.domains = new HashMap<>();
        domainsForTrackAndAdjust = new HashMap<>();
        initializeDomains();
        initalizeConstraints();
        csp = new CSP(variables, constraints);
    }

    private void initalizeConstraints(){
        //add the energy constraint
        constraints.add(new EnergyConstraint(energyLimit));
        //add the restricted cells constraint
        constraints.add(new RestrictedCellsConstraint(restrictedCells));

        // Add adjacency constraints for each pair of adjacent cells
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (!restrictedCells[row][col]) {
                    Variable currentVar = getVariableAt(row, col);

                    // Add constraints for each adjacent cell (up, down, left, right)
                    if (row > 0 && !restrictedCells[row - 1][col]) {
                        Variable adjacentVar = getVariableAt(row - 1, col);
                        constraints.add(new AdjacencyConstraint(currentVar, adjacentVar));
                    }
                    if (row < rows - 1 && !restrictedCells[row + 1][col]) {
                        Variable adjacentVar = getVariableAt(row + 1, col);
                        constraints.add(new AdjacencyConstraint(currentVar, adjacentVar));
                    }
                    if (col > 0 && !restrictedCells[row][col - 1]) {
                        Variable adjacentVar = getVariableAt(row, col - 1);
                        constraints.add(new AdjacencyConstraint(currentVar, adjacentVar));
                    }
                    if (col < cols - 1 && !restrictedCells[row][col + 1]) {
                        Variable adjacentVar = getVariableAt(row, col + 1);
                        constraints.add(new AdjacencyConstraint(currentVar, adjacentVar));
                    }
                }
            }
        }
    }

    private Variable getVariableAt(int row, int col) {
        return domains.keySet().stream()
                .filter(var -> var.getName().equals("Cell_" + row + "_" + col))
                .findFirst()
                .orElse(null);
    }

    private Predicate<Map<Variable, Object>> stoppingCondition() {
        return assignment -> allTargetsReached(assignment) && assignment.size() <= energyLimit;
    }


    // Method to check if all target cells have been reached
    private boolean allTargetsReached(Map<Variable, Object> assignment) {
        for (int[] target : targets) {
            boolean found = false;
            for (Object position : assignment.values()) {
                int[] pos = (int[]) position;
                if (pos[0] == target[0] && pos[1] == target[1]) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false; // Target cell not reached
            }
        }
        return true;
    }

    public Map<Variable, Object> solveWithArc4Algorithm() {
        AlgorithmArcConsistency4 ac4 = new AlgorithmArcConsistency4(domains, constraints);
        ac4.enforceArcConsistency();
        Map<Variable, Object> assignment = new HashMap<>();
        Variable startVariable = getVariableAt(0, 0);
        if (startVariable != null) {
            startVariable.setDomain(List.of(new int[]{0, 0}));
            assignment.put(startVariable, new int[]{0, 0});
        }

        if (csp.backtrackingSearchForArc4(assignment, stoppingCondition(), domains)) {
            return assignment;
        }

        return null;
    }

    // Apply node consistency before solving
    public Map<Variable, Object> solveWithNodeConsistency() {
        // Apply node consistency before solving
        nodeConsistency();
        return solveBacktracking(null, null);
    }

    public Map<Variable, Object> solveBacktracking(LookaheadStrategy lookaheadStrategy, TrackAndAdjustStrategy trackAndAdjustStrategy) {
        Map<Variable, Object> assignment = new LinkedHashMap<>();
        Variable startVariable = new Variable("Cell_0_0");
        startVariable.setDomain(List.of(new int[]{0, 0}));
        assignment.put(startVariable, new int[]{0, 0});
        if (Objects.nonNull(lookaheadStrategy)) {
            csp.setLookaheadStrategy(lookaheadStrategy);
        } else {
            if (Objects.nonNull(trackAndAdjustStrategy)) {
                csp.setTrackAndAdjustStrategy(trackAndAdjustStrategy);
            }
        }
        if (csp.backtrackingSearch(assignment, stoppingCondition(), domainsForTrackAndAdjust)) {
            return assignment;  // Return the assignment if a solution is found
        }
        return null;  // Return null if no solution is found
    }

    // Method to display solution (path)
    public void displaySolution(Map<Variable, Object> assignment) {
        if (Objects.isNull(assignment) || assignment.isEmpty()) {
            System.out.println("No path found within the given constraints.");
        } else {
            System.out.println("Path found:");
            for (Map.Entry<Variable, Object> entry : assignment.entrySet()) {
                System.out.println(entry.getKey().getName());
            }
        }
    }

    public void displayConstraintGraphSize() {
        int numVariables = 0;
        int numConstraints = 0;
        // Count variables (cells that are not restricted)
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (!restrictedCells[row][col]) {
                    numVariables++;
                }
            }
        }
        //count constraints
        // Constraint 1: Each cell must not exceed the energy limit
        numConstraints += numVariables; // Assuming each cell can potentially consume energy
        // Constraint 2: Restricted cells is equal to the number  of total cells minus unrestricted cells.
        numConstraints += (rows * cols) - numVariables; // Restricted cells are a form of "constraint"
        // Constraint 3: Target cells (each target is a constraint to be met)
        numConstraints += targets.length;
        // Constraint 4: Adjacency constraints (each move has a potential constraint edge)
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (!restrictedCells[row][col]) {
                    // Each non-restricted cell has up to four possible neighbors
                    if (row > 0 && !restrictedCells[row - 1][col]) numConstraints++; // Up
                    if (row < rows - 1 && !restrictedCells[row + 1][col]) numConstraints++; // Down
                    if (col > 0 && !restrictedCells[row][col - 1]) numConstraints++; // Left
                    if (col < cols - 1 && !restrictedCells[row][col + 1]) numConstraints++; // Right
                }
            }
        }
        // Display the graph size
        System.out.println("Constraint Graph Size:");
        System.out.println("Number of Variables (Non-restricted Cells): " + numVariables);
        System.out.println("Number of Constraints: " + numConstraints);
    }

    public void nodeConsistency() {
        for (Variable var : csp.getVariables()) {
            List<Object> domain = var.getDomain();
            List<Object> consistentDomain = new ArrayList<>();

            for (Object value : domain) {
                int[] position = (int[]) value;
                int row = position[0];
                int col = position[1];

                // Check if this cell is non-restricted
                if (!restrictedCells[row][col]) {
                    consistentDomain.add(value);  // Keep value if it's consistent
                }
            }

            // Update the variable's domain to the consistent values
            var.setDomain(consistentDomain);
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getEnergyLimit() {
        return energyLimit;
    }

    public boolean[][] getRestrictedCells() {
        return restrictedCells;
    }

    public int[][] getTargets() {
        return targets;
    }

    public List<int[]> getPath() {
        return path;
    }

    @Override
    public void initializeDomains(){
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (!restrictedCells[row][col]) {
                    String currentCell = "Cell_" + row + "_" + col;
                    Variable cell = new Variable(currentCell);
                    cell.setDomain(List.of(new int[]{row, col}));  // Each cell has a single domain value (its position)
                    variables.add(cell);
                    domains.put(cell, Set.of(new int[]{row, col}));
                    Set<Object> possibleMoves = new HashSet<>();
                    if (row > 0 && !restrictedCells[row - 1][col]) {
                        possibleMoves.add("up");
                    }
                    if (row < rows - 1 && !restrictedCells[row + 1][col]) {
                        possibleMoves.add("down");
                    }
                    if (col > 0 && !restrictedCells[row][col - 1]) {
                        possibleMoves.add("left");
                    }
                    if (col < cols - 1 && !restrictedCells[row][col + 1]) {
                        possibleMoves.add("right");
                    }
                    domainsForTrackAndAdjust.put(cell, possibleMoves);
                }
            }
        }
    }

    @Override
    public void applyProblemReduction(Map<Variable, Set<Object>> domains) {

    }

    @Override
    public Map<Variable, Object> baselineBacktracking() {
        Map<Variable, Object> assignment = new LinkedHashMap<>();
        Variable startVariable = new Variable("Cell_0_0");
        startVariable.setDomain(List.of(new int[]{0, 0}));
        assignment.put(startVariable, new int[]{0, 0});
        if (csp.backtrackingSearch(assignment, stoppingCondition(), null)) {
            return assignment;  // Return the assignment if a solution is found
        }
        return null;
    }

    @Override
    public Map<Variable, Object> lookaheadBacktracking() {
        return null;
    }

    @Override
    public Map<Variable, Object> trackAndAdjustBacktracking() {
        return null;
    }
}
