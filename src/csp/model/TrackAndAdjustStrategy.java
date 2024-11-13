package csp.model;

import java.util.Map;
import java.util.Set;

public interface TrackAndAdjustStrategy {
    boolean trackAndAdjustPaths(CSP csp, Variable currentVar, Object value, Map<Variable, Object> assignment, Map<Variable, Set<Object>> domains);
}
