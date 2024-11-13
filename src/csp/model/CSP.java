package csp.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public class CSP {
    private List<Variable> variables;
    private List<Constraint> constraints;
    private LookaheadStrategy lookaheadStrategy;
    private TrackAndAdjustStrategy trackAndAdjustStrategy;

    public CSP(List<Variable> variables, List<Constraint> constraints) {
        this.variables = variables;
        this.constraints = constraints;
    }

    public void setLookaheadStrategy(LookaheadStrategy lookaheadStrategy) {
        this.lookaheadStrategy = lookaheadStrategy;
    }

    public void setTrackAndAdjustStrategy(TrackAndAdjustStrategy trackAndAdjustStrategy) {
        this.trackAndAdjustStrategy = trackAndAdjustStrategy;
    }

    // Backtracking method for CSP
    public boolean backtrackingSearch(Map<Variable, Object> assignment, Predicate<Map<Variable, Object>> stoppingCondition, Map<Variable, Set<Object>> domains) {
        //stop if stopping condition was met
        if (stoppingCondition.test(assignment)) {
            return true;
        }
        Variable unassigned = selectUnassignedVariable(assignment);
        assert unassigned != null;
        for (Object value : unassigned.getDomain()) {
            if ((Objects.nonNull(lookaheadStrategy) && lookaheadStrategy.lookaheadCheck(this, unassigned, value, assignment))
                    || (Objects.nonNull(trackAndAdjustStrategy) && trackAndAdjustStrategy.trackAndAdjustPaths(this,unassigned,value,assignment,domains))
                    || (Objects.isNull(trackAndAdjustStrategy) && Objects.isNull(lookaheadStrategy))
            ) {
                if (isConsistent(assignment)) {
                    assignment.put(unassigned, value);
                    if (backtrackingSearch(assignment, stoppingCondition,domains)) {
                        return true;
                    }
                    assignment.remove(unassigned); // Remove assignment on failure
                }
            }
        }
        return false;
    }

    public boolean backtrackingSearchForArc4(Map<Variable, Object> assignment, Predicate<Map<Variable, Object>> stoppingCondition, Map<Variable, Set<Object>> domains) {
        if (stoppingCondition.test(assignment)) {
            return true;
        }
        Variable unassigned = selectUnassignedVariable(assignment);
        if (unassigned == null) {
            return false;
        }
        Set<Object> domain = domains.get(unassigned);
        for (Object value : domain) {
            if (Objects.isNull(lookaheadStrategy) ||
                    lookaheadStrategy.lookaheadCheck(this, unassigned, value, assignment)) {
                if (isConsistent(assignment)) {
                    assignment.put(unassigned, value);
                    if (backtrackingSearch(assignment, stoppingCondition,domains)) {
                        return true;
                    }
                    assignment.remove(unassigned);
                }
            }
        }
        return false; // No solution found on this path
    }


    private boolean isConsistent(Map<Variable, Object> assignment) {
        for (Constraint constraint : constraints) {
            if (!constraint.isSatisfied(assignment)) {
                return false;
            }
        }
        return true;
    }

    // Selects an unassigned variable
    private Variable selectUnassignedVariable(Map<Variable, Object> assignment) {
        for (Variable var : variables) {
            if (!assignment.containsKey(var)) {
                return var;
            }
        }
        return null; // All variables are assigned
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public List<Constraint> getConstraints() {
        return constraints;
    }
}
