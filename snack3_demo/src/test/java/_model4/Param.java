package _model4;

import lombok.NonNull;

import java.util.*;
import java.util.stream.Collectors;

public class Param implements Cloneable {

    protected List<Term> terms = new LinkedList();
    protected Set<String> includes = new LinkedHashSet();
    protected Set<String> excludes;// = new LinkedHashSet();

    public Param() {
    }

    public <T extends Param> T or(String column, String termType, Object value) {
        Term term = new Term();
        term.setTermType(termType);
        term.setColumn(column);
        term.setValue(value);
        term.setType(Term.Type.or);
        this.terms.add(term);
        return (T) this;
    }

    public <T extends Param> T and(String column, String termType, Object value) {
        Term term = new Term();
        term.setTermType(termType);
        term.setColumn(column);
        term.setValue(value);
        term.setType(Term.Type.and);
        this.terms.add(term);
        return (T) this;
    }

    public Term nest() {
        return this.nest((String) null, (Object) null);
    }

    public Term orNest() {
        return this.orNest((String) null, (Object) null);
    }

    public Term nest(String termString, Object value) {
        Term term = new Term();
        term.setColumn(termString);
        term.setValue(value);
        term.setType(Term.Type.and);
        this.terms.add(term);
        return term;
    }

    public Term orNest(String termString, Object value) {
        Term term = new Term();
        term.setColumn(termString);
        term.setValue(value);
        term.setType(Term.Type.or);
        this.terms.add(term);
        return term;
    }

    public <T extends Param> T includes(String... fields) {
        this.includes.addAll(Arrays.asList(fields));
        return (T) this;
    }

    public <T extends Param> T excludes(String... fields) {
        //this.excludes.addAll(Arrays.asList(fields));
        this.includes.removeAll(Arrays.asList(fields));
        return (T) this;
    }

    public <T extends Param> T addTerm(Term term) {
        this.terms.add(term);
        return (T) this;
    }

    public Param clone() {
        try {
            Param param = (Param) super.clone();
            //param.setExcludes(new LinkedHashSet(this.excludes));
            param.setIncludes(new LinkedHashSet(this.includes));
            param.setTerms((List) this.terms.stream().map(Term::clone).collect(Collectors.toList()));
            return param;
        } catch (Throwable var2) {
            throw null;
        }
    }

    @NonNull
    public List<Term> getTerms() {
        return this.terms;
    }

    @NonNull
    public Set<String> getIncludes() {
        return this.includes;
    }

    @NonNull
    public Set<String> getExcludes() {
        return this.excludes;
    }

    public void setTerms(@NonNull List<Term> terms) {
        if (terms == null) {
            throw new NullPointerException("terms");
        } else {
            this.terms = terms;
        }
    }

    public void setIncludes(@NonNull Set<String> includes) {
        if (includes == null) {
            throw new NullPointerException("includes");
        } else {
            this.includes = includes;
        }
    }

    public void setExcludes(@NonNull Set<String> excludes) {
        if (excludes == null) {
            throw new NullPointerException("excludes");
        } else {
            //this.excludes = excludes;
        }
    }
}

