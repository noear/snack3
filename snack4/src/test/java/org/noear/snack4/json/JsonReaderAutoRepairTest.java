package org.noear.snack4.json;

import org.junit.jupiter.api.Test;
import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 JsonReader 在 AutoRepair 模式下的死循环问题。
 *
 * 背景：SSE 流式传输中，AI 模型返回的 tool call arguments 可能包含
 * 多余的闭合括号（如 {"query":"test"}}），导致 readLast() 死循环。
 *
 * 根因：parseValue() 的 AutoRepair 分支遇到无法识别的字符时，
 * 返回空 ONode 但不推进 bufferPosition。
 */
class JsonReaderAutoRepairTest {

    private static final Options AUTO_REPAIR = Options.of(Feature.Read_AutoRepair);

    /**
     * 带超时的 readLast 调用，防止测试真的卡死。
     * 如果 3 秒内未完成，判定为死循环。
     */
    private ONode readLastWithTimeout(String json) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<ONode> future = executor.submit(() -> {
                JsonReader reader = new JsonReader(json, AUTO_REPAIR);
                return reader.readLast();
            });
            return future.get(3, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            fail("readLast() 死循环: 输入 = \"" + json + "\"");
            return null; // unreachable
        } finally {
            executor.shutdownNow();
        }
    }

    // ====== 死循环复现用例 ======

    @Test
    void readLast_shouldNotDeadLoop_onExtraClosingBrace() throws Exception {
        // 多余的 } — parseObject() 解析完 {"a":1} 后，多余的 } 无法被消费
        String input = "{\"a\":1}" + "}";
        ONode result = readLastWithTimeout(input);
        assertNotNull(result);
    }

    @Test
    void readLast_shouldNotDeadLoop_onMultipleExtraClosingBraces() throws Exception {
        // 多个多余的 }
        String input = "{\"a\":1}" + "}}}";
        ONode result = readLastWithTimeout(input);
        assertNotNull(result);
    }

    @Test
    void readLast_shouldNotDeadLoop_onExtraClosingBracket() throws Exception {
        // 多余的 ]
        ONode result = readLastWithTimeout("[1,2]]");
        assertNotNull(result);
    }

    @Test
    void readLast_shouldNotDeadLoop_onMixedExtraBraces() throws Exception {
        // 混合多余的闭合括号
        String input = "{\"a\":[1]}" + "]}";
        ONode result = readLastWithTimeout(input);
        assertNotNull(result);
    }

    @Test
    void readLast_shouldNotDeadLoop_onNestedJsonWithExtraBrace() throws Exception {
        // 模拟 SSE 流式 tool call arguments 场景
        // AI 模型返回了嵌套 JSON 但多了一个 }
        String input = "{\"filter\":{\"status\":\"active\"}}" + "}";
        ONode result = readLastWithTimeout(input);
        assertNotNull(result);
    }

    @Test
    void readLast_shouldNotDeadLoop_onTrailingGarbage() throws Exception {
        // JSON 后跟无法识别的字符
        ONode result = readLastWithTimeout("{\"a\":1}xyz");
        assertNotNull(result);
    }

    @Test
    void readLast_shouldNotDeadLoop_onSingleUnrecognizedChar() throws Exception {
        // 单个无法识别的字符
        ONode result = readLastWithTimeout("}");
        // 结果可以是 null 或空节点，但不应死循环
    }

    @Test
    void readLast_shouldNotDeadLoop_onRepeatedUnrecognizedChars() throws Exception {
        // 多个无法识别的字符
        ONode result = readLastWithTimeout("}}}");
    }

    // ====== 正常场景验证（确保修复不破坏正常功能）======

    @Test
    void readLast_shouldParseCompleteJson() throws Exception {
        ONode result = readLastWithTimeout("{\"a\":1}");
        assertNotNull(result);
        assertTrue(result.isObject());
        assertEquals(1, result.get("a").getInt());
    }

    @Test
    void readLast_shouldParseNestedJson() throws Exception {
        ONode result = readLastWithTimeout("{\"filter\":{\"status\":\"active\"}}");
        assertNotNull(result);
        assertTrue(result.isObject());
        assertEquals("active", result.get("filter").get("status").getString());
    }

    @Test
    void readLast_shouldParseArray() throws Exception {
        ONode result = readLastWithTimeout("[1,2,3]");
        assertNotNull(result);
        assertTrue(result.isArray());
        assertEquals(3, result.getArray().size());
    }

    @Test
    void readLast_shouldHandleIncompleteJson() throws Exception {
        // 不完整的 JSON（流式中间态）— 应正常返回，不死循环
        ONode result = readLastWithTimeout("{\"query\":\"test");
        // 可能返回部分解析结果或 null，但不应死循环
    }

    @Test
    void readLast_shouldHandleEmptyInput() throws Exception {
        ONode result = readLastWithTimeout("");
        assertNull(result);
    }

    @Test
    void readLast_shouldHandleWhitespaceOnly() throws Exception {
        ONode result = readLastWithTimeout("   \t\n  ");
        assertNull(result);
    }
}
