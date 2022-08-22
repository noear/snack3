package _model4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Term implements Cloneable {

    private String column;
    private Object value;
    private Type type;
    private String termType;
    private List<String> options;
    private List<Term> terms;

    public Term() {
        this.type = Type.and;
        this.termType = "eq";
        this.options = new ArrayList();
        this.terms = new LinkedList();
    }

    public Term or(String term, Object value) {
        return this.or(term, "eq", value);
    }

    public Term and(String term, Object value) {
        return this.and(term, "eq", value);
    }

    public Term or(String term, String termType, Object value) {
        Term queryTerm = new Term();
        queryTerm.setTermType(termType);
        queryTerm.setColumn(term);
        queryTerm.setValue(value);
        queryTerm.setType(Type.or);
        this.terms.add(queryTerm);
        return this;
    }

    public Term and(String term, String termType, Object value) {
        Term queryTerm = new Term();
        queryTerm.setTermType(termType);
        queryTerm.setColumn(term);
        queryTerm.setValue(value);
        queryTerm.setType(Type.and);
        this.terms.add(queryTerm);
        return this;
    }

    public Term nest() {
        return this.nest((String)null, (Object)null);
    }

    public Term orNest() {
        return this.orNest((String)null, (Object)null);
    }

    public Term nest(String term, Object value) {
        Term queryTerm = new Term();
        queryTerm.setType(Type.and);
        queryTerm.setColumn(term);
        queryTerm.setValue(value);
        this.terms.add(queryTerm);
        return queryTerm;
    }

    public Term orNest(String term, Object value) {
        Term queryTerm = new Term();
        queryTerm.setType(Type.or);
        queryTerm.setColumn(term);
        queryTerm.setValue(value);
        this.terms.add(queryTerm);
        return queryTerm;
    }

    public Term addTerm(Term term) {
        if (term == this) {
            term = term.clone();
        }

        this.terms.add(term);
        return this;
    }

    public void setColumn(String column) {
        if (column != null) {
            if (column.contains("$")) {
                String[] tmp = column.split("[$]");
                this.setTermType(tmp[1]);
                column = tmp[0];
                if (tmp.length > 2) {
                    this.options.addAll(Arrays.asList(tmp).subList(2, tmp.length));
                }
            }

            this.column = column;
        }
    }

    public void setTermType(String termType) {
        if (termType.contains("$")) {
            String[] tmp = termType.split("[$]");
            termType = tmp[0];
            if (tmp.length > 1) {
                this.options.addAll(Arrays.asList(tmp).subList(1, tmp.length));
            }
        }

        this.termType = termType;
    }

    public Term clone() {
        try {
            Term term = (Term)super.clone();
            term.setColumn(this.column);
            term.setValue(this.value);
            term.setTermType(this.termType);
            term.setType(this.type);
            term.setTerms((List)this.terms.stream().map(Term::clone).collect(Collectors.toList()));
            term.setOptions(new ArrayList(this.getOptions()));
            return term;
        } catch (Throwable var2) {
            throw null;
        }
    }

    public String getColumn() {
        return this.column;
    }

    public Object getValue() {
        return this.value;
    }

    public Type getType() {
        return this.type;
    }

    public String getTermType() {
        return this.termType;
    }

    public List<String> getOptions() {
        return this.options;
    }

    public List<Term> getTerms() {
        return this.terms;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public void setTerms(List<Term> terms) {
        this.terms = terms;
    }

    public static enum Type {
        or,
        and;

        private Type() {
        }
    }
}
