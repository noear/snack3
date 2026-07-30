/*
 * Copyright 2005-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.snack4.jsonpath.filter;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.JsonPathException;
import org.noear.snack4.jsonpath.Operator;
import org.noear.snack4.jsonpath.OperatorLib;
import org.noear.snack4.jsonpath.QueryContext;
import org.noear.snack4.jsonpath.util.TokenizeUtil;
import org.noear.snack4.util.Asserts;

import java.util.*;


/**
 * 逻辑表达式
 *
 * @author noear 2025/5/5 created
 * @since 4.0
 */
public class Expression {
    private static Map<String, Expression> expressionMap = Collections.synchronizedMap(
            new LinkedHashMap<String, Expression>(512, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, Expression> eldest) {
                    return size() > 512;
                }
            });

    public static Expression of(String expressionStr) {
        return expressionMap.computeIfAbsent(expressionStr, Expression::new);
    }

    /// ///////////////////
    private final String expressionStr;
    private final List<Token> rpn;

    private Expression(String expressionStr) {
        this.expressionStr = expressionStr;
        List<Token> tokens = TokenizeUtil.tokenize(expressionStr);
        this.rpn = convertToRPN(tokens);
    }

    @Override
    public String toString() {
        return expressionStr;
    }

    // 评估逆波兰式
    public boolean test(ONode node, QueryContext ctx) {
        try {
            Deque<Boolean> stack = new ArrayDeque<>();
            for (Token token : rpn) {
                if (token.type == TokenType.ATOM) {
                    stack.push(evaluateTerm(ctx, node, token.value));
                } else if (token.type == TokenType.AND || token.type == TokenType.OR) {
                    boolean b = stack.pop();
                    boolean a = stack.pop();
                    stack.push(token.type == TokenType.AND ? a && b : a || b);
                }
            }
            return stack.pop();
        } catch (Throwable ex) {
            throw new JsonPathException(ex);
        }
    }


    /**
     * 转换为逆波兰式
     */
    public static List<Token> convertToRPN(List<Token> tokens) {
        List<Token> output = new ArrayList<>();
        Deque<Token> stack = new ArrayDeque<>();

        for (Token token : tokens) {
            switch (token.type) {
                case ATOM:
                    output.add(token);
                    break;
                case LPAREN:
                    stack.push(token);
                    break;
                case RPAREN:
                    while (!stack.isEmpty() && stack.peek().type != TokenType.LPAREN) {
                        output.add(stack.pop());
                    }
                    stack.pop();
                    break;
                case AND:
                case OR:
                    while (!stack.isEmpty() && stack.peek().type != TokenType.LPAREN &&
                            precedence(token) <= precedence(stack.peek())) {
                        output.add(stack.pop());
                    }
                    stack.push(token);
                    break;
            }
        }

        while (!stack.isEmpty()) {
            output.add(stack.pop());
        }
        return output;
    }

    /**
     * 优先级
     *
     */
    private static int precedence(Token token) {
        return token.type == TokenType.AND ? 2 : token.type == TokenType.OR ? 1 : 0;
    }

    private boolean evaluateTerm(QueryContext ctx, ONode node, String termStr) {
        Term term = Term.of(termStr);

        boolean result = doEvaluateTerm(ctx, node, term);
        return term.isNot() ? !result : result;
    }


    private boolean doEvaluateTerm(QueryContext ctx, ONode node, Term term) {
        // 过滤空条件（操作符处理时，就不需要再过滤了）
        if (Asserts.isEmpty(term.getLeft().getValue())) {
            return false;
        }

        // 单元操作（如 @.price）
        if (Asserts.isEmpty(term.getRight().getValue())) {
            if (term.getOp() == null) {
                ONode leftNode = term.getLeftNode(ctx, node);
                return confirmQuery(node, leftNode);
            } else {
                return false;
            }
        }

        Operator operation = OperatorLib.get(term.getOp());

        if (operation == null) {
            throw new JsonPathException("Unsupported operator : " + term.getOp());
        }

        return operation.apply(ctx, node, term);
    }

    private boolean confirmQuery(ONode node, ONode leftNode) { //node 方便调试
        if (leftNode.isBoolean()) {
            return leftNode.getBoolean();
        }

        if (leftNode.isArray()) {
            return leftNode.getArray().size() > 0;
        }

        return !leftNode.isNull();
    }

    public static enum TokenType {ATOM, AND, OR, LPAREN, RPAREN}

    public static class Token {
        final TokenType type;
        final String value;

        public Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return "Token{" +
                    "type=" + type +
                    ", value='" + value + '\'' +
                    '}';
        }
    }
}