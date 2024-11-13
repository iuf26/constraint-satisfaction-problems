package csp.model;

import csp.model.CSP;
import csp.model.Variable;

import java.util.Map;
import java.util.Set;

public interface LookaheadStrategy {
    boolean lookaheadCheck(CSP csp, Variable variable, Object value, Map<Variable, Object> assignment);
}
