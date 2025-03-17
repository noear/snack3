package org.noear.snack.schema;

import org.noear.snack.ONode;
import org.noear.snack.exception.SchemaException;
import org.noear.snack.schema.rule.ValidationRule;
import org.noear.snack.core.PathTracker;

import java.util.List;

/**
 * 编译验证规则实现
 */
public class CompiledRule {
    private final List<ValidationRule> rules;

    public CompiledRule(List<ValidationRule> rules) {
        this.rules = rules;
    }

   public void validate(ONode data, PathTracker path) throws SchemaException {
        for (ValidationRule rule : rules) {
            rule.validate(data);
        }
    }
}