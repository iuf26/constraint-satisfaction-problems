package csp.model;

import java.util.List;
import java.util.Map;

public interface Constraint {
    boolean isSatisfied(Map<Variable, Object> assignment);

    // Returns the list of variables involved in this constraint
    List<Variable> getVariables();
}