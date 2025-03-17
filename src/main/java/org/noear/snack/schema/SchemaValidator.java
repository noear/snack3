// file: JsonSchemaValidator.java
package org.noear.snack.schema;

import org.noear.snack.ONode;
import org.noear.snack.core.JsonTypes;
import org.noear.snack.schema.rule.EnumRule;
import org.noear.snack.schema.rule.TypeRule;
import org.noear.snack.schema.rule.ValidationRule;
import org.noear.snack.core.PathTracker;
import org.noear.snack.exception.SchemaException;

import java.util.*;

/**
 * JSON模式验证器，支持JSON Schema规范
 */
public class SchemaValidator {
    private final ONode schema;
    private final Map<String, CompiledRule> compiledRules;

    public SchemaValidator(ONode schema) {
        if (!schema.isObject()) {
            throw new IllegalArgumentException("Schema must be a JSON object");
        }
        this.schema = schema;
        this.compiledRules = compileSchema(schema);
    }

    public void validate(ONode data) throws SchemaException {
        validateNode(schema, data, PathTracker.begin());
    }

    // 核心验证方法（完整实现）
    private void validateNode(ONode schemaNode, ONode dataNode, PathTracker path) throws SchemaException {
        // 执行预编译规则
        CompiledRule rule = compiledRules.get(path.currentPath());
        if (rule != null) {
            rule.validate(dataNode, path);
        }

        // 处理类型校验
        if (schemaNode.hasKey("type")) {
            validateType(schemaNode.get("type"), dataNode, path);
        }

        // 处理枚举校验
        if (schemaNode.hasKey("enum")) {
            validateEnum(schemaNode.get("enum"), dataNode, path);
        }

        // 处理对象属性校验
        if (dataNode.isObject() && schemaNode.hasKey("properties")) {
            validateProperties(schemaNode.get("properties"), dataNode, path);
        }

        // 处理数组校验
        if (dataNode.isArray()) {
            validateArrayConstraints(schemaNode, dataNode, path);
        }

        // 处理数值范围校验
        if (dataNode.isNumber()) {
            validateNumericConstraints(schemaNode, dataNode, path);
        }

        // 处理字符串格式校验
        if (dataNode.isString()) {
            validateStringConstraints(schemaNode, dataNode, path);
        }

        // 处理条件校验
        validateConditional(schemaNode, dataNode, path);
    }

    // 类型校验（完整实现）
    private void validateType(ONode typeNode, ONode dataNode, PathTracker path) throws SchemaException {
        if (typeNode.isString()) {
            String expectedType = typeNode.getString();
            if (!matchType(dataNode, expectedType)) {
                throw typeMismatch(expectedType, dataNode, path);
            }
        } else if (typeNode.isArray()) {
            boolean matched = false;
            for (ONode typeOption : typeNode.getArray()) {
                if (matchType(dataNode, typeOption.getString())) {
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                throw new SchemaException("Type not in allowed types at " + path.currentPath());
            }
        }
    }

    private boolean matchType(ONode node, String type) {
        switch (type) {
            case "string": return node.isString();
            case "number": return node.isNumber();
            case "integer": return node.isNumber() && isInteger(node.getNumber());
            case "boolean": return node.isBoolean();
            case "object": return node.isObject();
            case "array": return node.isArray();
            case "null": return node.isNull();
            default: return false;
        }
    }

    private boolean isInteger(Number num) {
        return num instanceof Integer || num instanceof Long ||
                (num instanceof Double && num.doubleValue() == num.longValue());
    }

    // 枚举校验（完整实现）
    private void validateEnum(ONode enumNode, ONode dataNode, PathTracker path) throws SchemaException {
        if (!enumNode.isArray()) return;

        for (ONode allowedValue : enumNode.getArray()) {
            if (deepEquals(allowedValue, dataNode)) {
                return;
            }
        }
        throw new SchemaException("Value not in enum list at " + path.currentPath());
    }

    // 对象属性校验（完整实现）
    private void validateProperties(ONode propertiesNode, ONode dataNode, PathTracker path) throws SchemaException {
        Map<String, ONode> properties = propertiesNode.getObject();
        Map<String, ONode> dataObj = dataNode.getObject();

        // 校验必填字段
        if (schema.hasKey("required")) {
            ONode requiredNode = schema.get("required");
            if (requiredNode.isArray()) {
                for (ONode requiredField : requiredNode.getArray()) {
                    String field = requiredField.getString();
                    if (!dataObj.containsKey(field)) {
                        throw new SchemaException("Missing required field: " + field + " at " + path.currentPath());
                    }
                }
            }
        }

        // 校验每个属性
        for (Map.Entry<String, ONode> propEntry : properties.entrySet()) {
            String propName = propEntry.getKey();
            path.enterProperty(propName);
            if (dataObj.containsKey(propName)) {
                validateNode(propEntry.getValue(), dataObj.get(propName), path);
            }
            path.exit();
        }
    }

    // 数组约束校验（完整实现）
    private void validateArrayConstraints(ONode schemaNode, ONode dataNode, PathTracker path) throws SchemaException {
        List<ONode> items = dataNode.getArray();

        if (schemaNode.hasKey("minItems")) {
            int min = schemaNode.get("minItems").getInt();
            if (items.size() < min) {
                throw new SchemaException("Array length " + items.size() + " < minItems(" + min + ") at " + path.currentPath());
            }
        }
        if (schemaNode.hasKey("maxItems")) {
            int max = schemaNode.get("maxItems").getInt();
            if (items.size() > max) {
                throw new SchemaException("Array length " + items.size() + " > maxItems(" + max + ") at " + path.currentPath());
            }
        }

        if (schemaNode.hasKey("items")) {
            ONode itemsSchema = schemaNode.get("items");
            for (int i = 0; i < items.size(); i++) {
                path.enterIndex(i);
                validateNode(itemsSchema, items.get(i), path);
                path.exit();
            }
        }
    }

    // 数值范围校验（完整实现）
    private void validateNumericConstraints(ONode schemaNode, ONode dataNode, PathTracker path) throws SchemaException {
        double value = dataNode.getDouble();

        if (schemaNode.hasKey("minimum")) {
            double min = schemaNode.get("minimum").getDouble();
            if (value < min) {
                throw new SchemaException("Value " + value + " < minimum(" + min + ") at " + path.currentPath());
            }
        }
        if (schemaNode.hasKey("maximum")) {
            double max = schemaNode.get("maximum").getDouble();
            if (value > max) {
                throw new SchemaException("Value " + value + " > maximum(" + max + ") at " + path.currentPath());
            }
        }
    }

    // 字符串约束校验（完整实现）
    private void validateStringConstraints(ONode schemaNode, ONode dataNode, PathTracker path) throws SchemaException {
        String value = dataNode.getString();

        if (schemaNode.hasKey("minLength")) {
            int min = schemaNode.get("minLength").getInt();
            if (value.length() < min) {
                throw new SchemaException("String length " + value.length() + " < minLength(" + min + ") at " + path.currentPath());
            }
        }
        if (schemaNode.hasKey("maxLength")) {
            int max = schemaNode.get("maxLength").getInt();
            if (value.length() > max) {
                throw new SchemaException("String length " + value.length() + " > maxLength(" + max + ") at " + path.currentPath());
            }
        }
        if (schemaNode.hasKey("pattern")) {
            String pattern = schemaNode.get("pattern").getString();
            if (!value.matches(pattern)) {
                throw new SchemaException("String does not match pattern: " + pattern + " at " + path.currentPath());
            }
        }
    }

    // 条件校验（完整实现）
    private void validateConditional(ONode schemaNode, ONode dataNode, PathTracker path) throws SchemaException {
        validateConditionalGroup(schemaNode, "anyOf", dataNode, path, false);
        validateConditionalGroup(schemaNode, "allOf", dataNode, path, true);
        validateConditionalGroup(schemaNode, "oneOf", dataNode, path, false);
    }

    private void validateConditionalGroup(ONode schemaNode, String key,
                                          ONode dataNode, PathTracker path,
                                          boolean requireAll) throws SchemaException {
        if (!schemaNode.hasKey(key)) return;

        List<ONode> schemas = schemaNode.get(key).getArray();
        int matchCount = 0;
        List<SchemaException> errors = new ArrayList<>();

        for (ONode subSchema : schemas) {
            try {
                validateNode(subSchema, dataNode, path);
                matchCount++;
            } catch (SchemaException e) {
                errors.add(e);
                if (requireAll) throw e;
            }
        }

        if (requireAll && matchCount != schemas.size()) {
            throw new SchemaException("Failed to satisfy allOf constraints at " + path.currentPath());
        }
        if (!requireAll && key.equals("anyOf") && matchCount == 0) {
            throw new SchemaException("Failed to satisfy anyOf constraints at " + path.currentPath());
        }
        if (!requireAll && key.equals("oneOf") && matchCount != 1) {
            throw new SchemaException("Must satisfy exactly one of oneOf constraints at " + path.currentPath());
        }
    }

    // 深度比较方法（完整实现）
    private boolean deepEquals(ONode a, ONode b) {
        if (a.getType() != b.getType()) return false;

        switch (a.getType()) {
            case JsonTypes.TYPE_NULL: return true;
            case JsonTypes.TYPE_BOOLEAN: return a.getBoolean() == b.getBoolean();
            case JsonTypes.TYPE_NUMBER:
                return a.getNumber().doubleValue() == b.getNumber().doubleValue();
            case JsonTypes.TYPE_STRING:
                return a.getString().equals(b.getString());
            case JsonTypes.TYPE_ARRAY:
                List<ONode> aArr = a.getArray();
                List<ONode> bArr = b.getArray();
                if (aArr.size() != bArr.size()) return false;
                for (int i = 0; i < aArr.size(); i++) {
                    if (!deepEquals(aArr.get(i), bArr.get(i))) return false;
                }
                return true;
            case JsonTypes.TYPE_OBJECT:
                Map<String, ONode> aObj = a.getObject();
                Map<String, ONode> bObj = b.getObject();
                if (aObj.size() != bObj.size()) return false;
                for (Map.Entry<String, ONode> entry : aObj.entrySet()) {
                    String key = entry.getKey();
                    if (!bObj.containsKey(key)) return false;
                    if (!deepEquals(entry.getValue(), bObj.get(key))) return false;
                }
                return true;
            default: return false;
        }
    }

    // 异常处理
    private SchemaException typeMismatch(String expected, ONode actual, PathTracker path) {
        return new SchemaException("Expected type " + expected + " but got " +
                JsonTypes.getTypeName(actual.getType()) + " at " + path.currentPath());
    }

    // 预编译相关实现
    private Map<String, CompiledRule> compileSchema(ONode schema) {
        Map<String, CompiledRule> rules = new HashMap<>();
        compileSchemaRecursive(schema, rules, PathTracker.begin());
        return rules;
    }

    private void compileSchemaRecursive(ONode schemaNode, Map<String, CompiledRule> rules, PathTracker path) {
        List<ValidationRule> localRules = new ArrayList<>();

        if (schemaNode.hasKey("type")) {
            localRules.add(new TypeRule(schemaNode.get("type")));
        }
        if (schemaNode.hasKey("enum")) {
            localRules.add(new EnumRule(schemaNode.get("enum")));
        }

        if (!localRules.isEmpty()) {
            rules.put(path.currentPath(), new CompiledRule(localRules));
        }

        if (schemaNode.hasKey("properties")) {
            ONode propsNode = schemaNode.get("properties");
            for (Map.Entry<String, ONode> entry : propsNode.getObject().entrySet()) {
                path.enterProperty(entry.getKey());
                compileSchemaRecursive(entry.getValue(), rules, path);
                path.exit();
            }
        }

        if (schemaNode.hasKey("items")) {
            path.enterIndex(0);
            compileSchemaRecursive(schemaNode.get("items"), rules, path);
            path.exit();
        }
    }
}