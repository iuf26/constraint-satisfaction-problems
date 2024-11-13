package csp.robot.models.strategies;

import csp.model.CSP;
import csp.model.LookaheadStrategy;
import csp.model.Variable;

import java.util.Map;

public class RobotNavigationLookaheadStrategy implements LookaheadStrategy {
    private int energyLimit;

    public RobotNavigationLookaheadStrategy(int energyLimit) {
        this.energyLimit = energyLimit;
    }
    @Override
    public boolean lookaheadCheck(CSP csp, Variable variable, Object value, Map<Variable, Object> assignment) {
        assignment.put(variable, value);

        // Verify if remaining energy allows reaching any target
        int remainingEnergy = energyLimit - assignment.size();
        if (remainingEnergy <= 0) {
            assignment.remove(variable);
            return false;
        }
        assignment.remove(variable);
        return true;
    }
}
