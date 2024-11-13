package csp.robot.constraints;

import csp.model.Constraint;
import csp.model.Variable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * global constraint
 */
public class EnergyConstraint implements Constraint {
    private final int energyLimit;

    public EnergyConstraint(int energyLimit) {
        this.energyLimit = energyLimit;
    }

    @Override
    public boolean isSatisfied(Map<Variable, Object> assignment) {
        return assignment.size() <= energyLimit;
    }

    @Override
    public List<Variable> getVariables() {
        return Collections.emptyList();
    }
}
