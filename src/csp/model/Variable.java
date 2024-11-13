package csp.model;

import java.util.List;
import java.util.Objects;

public class Variable {
    private String name;
    private List<Object> domain;

    public Variable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Object> getDomain() {
        return domain;
    }

    public void setDomain(List<Object> domain) {
        this.domain = domain;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;  // Check if comparing with itself
        if (o == null || getClass() != o.getClass()) return false;  // Check if o is null or not the same class
        Variable variable = (Variable) o;
        return Objects.equals(name, variable.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
