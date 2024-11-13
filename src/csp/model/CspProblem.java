package csp.model;

import java.util.Map;
import java.util.Set;

public interface CspProblem {
    public void initializeDomains();

    public void applyProblemReduction(Map<Variable, Set<Object>> domains);

    public Map<Variable,Object> baselineBacktracking();

    public Map<Variable,Object> lookaheadBacktracking();

    public Map<Variable,Object> trackAndAdjustBacktracking();

}
