package csp.robot.constraints;

import csp.model.Constraint;
import csp.model.Variable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AdjacencyConstraint implements Constraint {
    private final Variable var1;
    private final Variable var2;

    public AdjacencyConstraint(Variable var1, Variable var2) {
        this.var1 = var1;
        this.var2 = var2;
    }
    @Override
    public boolean isSatisfied(Map<Variable, Object> assignment) {
        // Check if both variables have been assigned
        if (!assignment.containsKey(var1) || !assignment.containsKey(var2)) {
            return true;  // If either variable is unassigned, it’s not violated yet
        }
        int[] pos1 = (int[]) assignment.get(var1);
        int[] pos2 = (int[]) assignment.get(var2);
        // Check if positions are adjacent
        int rowDiff = Math.abs(pos1[0] - pos2[0]);
        int colDiff = Math.abs(pos1[1] - pos2[1]);
        return (rowDiff + colDiff == 1);
    }

    @Override
    public List<Variable> getVariables() {
        return Arrays.asList(var1, var2);
    }
}
