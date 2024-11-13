package csp.robot.algorithms;

import csp.model.Constraint;
import csp.model.Variable;

import java.util.*;

public class AlgorithmArcConsistency4 {
    private final Map<Variable, Set<Object>> domains;
    private final List<Constraint> constraints;
    private final Map<Pair, Integer> counter;
    private final Map<Variable, Map<Object, Set<Pair>>> support;
    private final Queue<Pair> list;

    public AlgorithmArcConsistency4(Map<Variable, Set<Object>> domains, List<Constraint> constraints) {
        this.domains = domains;
        this.constraints = constraints;
        this.counter = new HashMap<>();
        this.support = new HashMap<>();
        this.list = new LinkedList<>();
    }

    private static class Pair {
        Variable var;
        Object value;

        Pair(Variable var, Object value) {
            this.var = var;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair pair = (Pair) o;
            return Objects.equals(var, pair.var) && Objects.equals(value, pair.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(var, value);
        }
    }

    public void enforceArcConsistency() {
        // Step 1: Initialization
        for (Constraint constraint : constraints) {
            List<Variable> variables = constraint.getVariables();
            if (variables.size() == 2) {
                Variable var1 = variables.get(0);
                Variable var2 = variables.get(1);

                for (Object value1 : domains.get(var1)) {
                    int totalSupport = 0;
                    Set<Pair> supportingPairs = new HashSet<>();

                    for (Object value2 : domains.get(var2)) {
                        Map<Variable, Object> partialAssignment = new HashMap<>();
                        partialAssignment.put(var1, value1);
                        partialAssignment.put(var2, value2);

                        // Check if this pair satisfies the constraint
                        if (constraint.isSatisfied(partialAssignment)) {
                            totalSupport++;
                            supportingPairs.add(new Pair(var2, value2));
                        }
                    }

                    if (totalSupport == 0) {
                        // No support, so add to the rejection list
                        list.add(new Pair(var1, value1));
                    } else {
                        // Record support information
                        support.computeIfAbsent(var1, k -> new HashMap<>()).put(value1, supportingPairs);
                        counter.put(new Pair(var1, value1), totalSupport);
                    }
                }
            }
        }

        // Step 2: Remove unsupported labels
        while (!list.isEmpty()) {
            Pair p = list.poll();

            Variable var = p.var;
            Object value = p.value;

            // Remove the value from the domain of the variable
            domains.get(var).remove(value);

            for (Constraint constraint : constraints) {
                if (constraint.getVariables().contains(var)) {
                    List<Variable> variables = constraint.getVariables();
                    Variable neighborVar = variables.get(0).equals(var) ? variables.get(1) : variables.get(0);

                    for (Object neighborValue : domains.get(neighborVar)) {
                        Pair neighborPair = new Pair(neighborVar, neighborValue);
                        Set<Pair> supportSet = support.get(neighborVar).get(neighborValue);

                        if (supportSet != null && supportSet.contains(p)) {
                            counter.put(neighborPair, counter.get(neighborPair) - 1);
                            supportSet.remove(p);

                            // If no support left, add to the list for removal
                            if (counter.get(neighborPair) == 0) {
                                list.add(neighborPair);
                            }
                        }
                    }
                }
            }
        }
    }
}
