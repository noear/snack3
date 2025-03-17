package org.noear.snack.schema.rule;


import org.noear.snack.ONode;
import org.noear.snack.exception.SchemaException;

import java.util.HashSet;
import java.util.Set;

/**
 * 枚举验证规则实现
 */
public class EnumRule implements ValidationRule {
    private final Set<ONode> allowedValues;

    public EnumRule(ONode enumNode) {
        this.allowedValues = new HashSet<>();
        if (enumNode.isArray()) {
            for (ONode value : enumNode.getArray()) {
                allowedValues.add(value);
            }
        }
    }

    @Override
    public void validate(ONode data) throws SchemaException {
        if (!allowedValues.contains(data)) {
            throw new SchemaException("Value not in enum list");
        }
    }
}