package csp.robot.constraints;

import csp.model.Constraint;
import csp.model.Variable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RestrictedCellsConstraint implements Constraint {
    private final boolean[][] restrictedCells;

    public RestrictedCellsConstraint(boolean[][] restrictedCells) {
        this.restrictedCells = restrictedCells;
    }

    @Override
    public boolean isSatisfied(Map<Variable, Object> assignment) {
        for (Object position : assignment.values()) {
            int[] pos = (int[]) position;
            if (restrictedCells[pos[0]][pos[1]]) {
                return false; // Fails if any cell in the path is restricted
            }
        }
        return true;
    }

    @Override
    public List<Variable> getVariables() {
        return Collections.emptyList();

    }


}
