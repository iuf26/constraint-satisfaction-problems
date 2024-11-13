package csp.robot.models.strategies;

import csp.model.CSP;
import csp.model.TrackAndAdjustStrategy;
import csp.model.Variable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RobotNavigationTrackAndAdjust implements TrackAndAdjustStrategy {
    private final Set<Variable> visitedPaths = new HashSet<>(); // Track visited cells to avoid loops
    private final int energyLimit; // Energy limit, specific to the problem
    private final boolean[][] restrictedCells;

    public RobotNavigationTrackAndAdjust(int energyLimit, boolean[][] restrictedCells) {
        this.energyLimit = energyLimit;
        this.restrictedCells = restrictedCells;
    }


    @Override
    public boolean trackAndAdjustPaths(CSP csp, Variable currentVar, Object value, Map<Variable, Object> assignment, Map<Variable, Set<Object>> domains) {
        if (visitedPaths.contains(currentVar)) {
            return false; //skip path as it was visited and unsuccesful before
        }
        assignment.put(currentVar, value);
        visitedPaths.add(currentVar);
        if (assignment.size() > energyLimit) {
            //path that exceeds energy limit
            visitedPaths.remove(currentVar);
            assignment.remove(currentVar);
            return false;
        }
        boolean viablePath = hasFeasibleMove(currentVar, domains);
        if (!viablePath) {
            visitedPaths.remove(currentVar);
            assignment.remove(currentVar);
        }
        return viablePath;
    }

    private boolean hasFeasibleMove(Variable currentVar, Map<Variable, Set<Object>> domains) {
        for (Object direction : domains.get(currentVar)) {
            int[] newPosition = getNextPosition(currentVar, direction);
            if (!visitedPaths.contains(new Variable("Cell_" + newPosition[0] + "_" + newPosition[1]))) {
                return true;
            }
        }
        return false; // No feasible moves left, prune this path
    }

    private int[] getNextPosition(Variable currentVar, Object direction) {
        int[] currentPosition = (int[]) currentVar.getDomain().get(0);
        int row = currentPosition[0];
        int col = currentPosition[1];
        switch (direction.toString().toLowerCase()) {
            case "up":
                return new int[]{row - 1, col}; //up
            case "down":
                return new int[]{row + 1, col}; //down
            case "left":
                return new int[]{row, col - 1}; //left
            case "right":
                return new int[]{row, col + 1}; //right
            default:
                throw new IllegalArgumentException("Invalid direction: " + direction);
        }
    }

    private boolean isRestricted(int row, int col) {
        if (row < 0 || row >= restrictedCells.length || col < 0 || col >= restrictedCells[0].length) {
            return true;
        }
        return restrictedCells[row][col];
    }


}
