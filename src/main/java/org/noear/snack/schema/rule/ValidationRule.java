package org.noear.snack.schema.rule;

import org.noear.snack.ONode;
import org.noear.snack.exception.SchemaException;

/**
 * 预编译规则接口
 */
public interface ValidationRule {
    void validate(ONode data) throws SchemaException;
}