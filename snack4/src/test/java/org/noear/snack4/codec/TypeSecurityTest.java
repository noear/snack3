package org.noear.snack4.codec;

import org.junit.jupiter.api.Test;
import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.SnackException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 类型安全检测机制测试
 *
 * <p>覆盖：ALLOW 不可绕过全局黑名单、默认黑名单扩展、精确匹配不误伤、\n
 * ClassDecoder / loadClass 内聚检查、@type 格式校验、正常路径回归。</p>
 */
public class TypeSecurityTest {

    /// /////////////////
    // 默认黑名单（扩展后）
    /// /////////////////

    @Test
    public void globalBlacklistCoversGadgetLibs() {
        Options opts = Options.of();

        // JDK 侧
        assertTrue(opts.isTypeBlocked("sun.misc.Unsafe"));
        assertTrue(opts.isTypeBlocked("com.sun.rowset.JdbcRowSetImpl"));
        assertTrue(opts.isTypeBlocked("javax.management.BadAttributeValueExpException"));
        assertTrue(opts.isTypeBlocked("java.lang.Runtime"));
        assertTrue(opts.isTypeBlocked("java.security.SignedObject"));
        assertTrue(opts.isTypeBlocked("java.beans.EventHandler"));
        assertTrue(opts.isTypeBlocked("java.util.PriorityQueue"));

        // 第三方 gadget 常用包
        assertTrue(opts.isTypeBlocked("org.apache.commons.collections.Transformer"));
        assertTrue(opts.isTypeBlocked("org.apache.commons.collections4.Transformer"));
        assertTrue(opts.isTypeBlocked("org.apache.commons.beanutils.BeanComparator"));
        assertTrue(opts.isTypeBlocked("org.springframework.aop.support.DefaultBeanFactoryPointcutAdvisor"));
        assertTrue(opts.isTypeBlocked("com.mchange.v2.c3p0.impl.PoolBackedDataSourceBase"));
        assertTrue(opts.isTypeBlocked("org.codehaus.groovy.runtime.ConvertedClosure"));
        assertTrue(opts.isTypeBlocked("org.apache.xbean.propertyeditor.JndiConverter"));
        assertTrue(opts.isTypeBlocked("com.alibaba.fastjson.JSONObject"));
    }

    @Test
    public void preciseMatchNoFalsePositive() {
        Options opts = Options.of();

        // 精确类名不误伤相似类名
        assertFalse(opts.isTypeBlocked("java.lang.RuntimeFoo"));
        assertFalse(opts.isTypeBlocked("java.lang.ProcessBuilderEx"));

        // 包前缀不误伤相邻包
        assertFalse(opts.isTypeBlocked("org.apache.commons.collectionsXxx.Transformer"));

        // 正常类型放行
        assertFalse(opts.isTypeBlocked("java.lang.String"));
        assertFalse(opts.isTypeBlocked("java.util.ArrayList"));
        assertFalse(opts.isTypeBlocked("com.example.BizBean"));
    }

    /// /////////////////
    // 高危：ALLOW 不可绕过全局黑名单（DENY 优先）
    /// /////////////////

    @Test
    public void allowCannotBypassGlobalBlacklist() {
        Options opts = Options.of();
        // 宽松白名单：所有 com. 开头都 ALLOW
        opts.addTypeChecker(clz -> clz.startsWith("com.") ? TypeChecker.Result.ALLOW : TypeChecker.Result.SKIP);

        // com.sun. 在全局黑名单中，即使实例级 ALLOW 也必须拦截
        assertTrue(opts.isTypeBlocked("com.sun.rowset.JdbcRowSetImpl"));

        // 正常业务类（不在黑名单）仍可放行
        assertFalse(opts.isTypeBlocked("com.example.BizBean"));
    }

    /// /////////////////
    // 协议注入防护
    /// /////////////////

    @Test
    public void protocolInjectionBlocked() {
        Options opts = Options.of();
        assertTrue(opts.isTypeBlocked("ldap://evil.com/a"));
        assertTrue(opts.isTypeBlocked("rmi!evil.com"));
        assertTrue(opts.isTypeBlocked(null));
    }

    /// /////////////////
    // loadClass 内聚检查
    /// /////////////////

    @Test
    public void loadClassEnforcesCheck() {
        Options opts = Options.of();

        // 黑名单类：默认抛异常，disallowedThrow=true 返回 null
        assertThrows(SnackException.class, () -> opts.loadClass("java.lang.Runtime"));
        assertNull(opts.loadClass("java.lang.Runtime", false));

        // 协议注入类名：抛异常
        assertThrows(SnackException.class, () -> opts.loadClass("ldap://evil"));

        // 正常类仍可加载
        assertSame(String.class, opts.loadClass("java.lang.String"));
    }

    /// /////////////////
    // ClassDecoder 路径（双保险）
    /// /////////////////

    @Test
    public void classDecoderBlocksBlockedType() {
        Options opts = Options.of();
        String json = "{\"clz\":\"java.beans.EventHandler\"}";
        ClzBean bean = ONode.ofJson(json, opts).toBean(ClzBean.class);
        assertNull(bean.clz); // 被拦截返回 null
    }

    @Test
    public void classDecoderNormalPath() {
        Options opts = Options.of();
        String json = "{\"clz\":\"java.util.ArrayList\"}";
        ClzBean bean = ONode.ofJson(json, opts).toBean(ClzBean.class);
        assertSame(ArrayList.class, bean.clz);
    }

    /// /////////////////
    // 正常路径回归
    /// /////////////////

    @Test
    public void interfaceStringLoadNormal() {
        Options opts = Options.of();
        String json = "{\"list\":\"java.util.ArrayList\"}";
        ListBean bean = ONode.ofJson(json, opts).toBean(ListBean.class);
        assertNotNull(bean.list);
        assertTrue(bean.list instanceof ArrayList);
    }

    @Test
    public void autoTypeNormalClass() {
        Options opts = Options.of(Feature.Read_AutoType);
        String json = "{\"@type\":\"java.util.LinkedHashMap\",\"a\":1}";
        Object obj = ONode.ofJson(json, opts).toBean(Object.class);
        assertTrue(obj instanceof LinkedHashMap);
    }

    @Test
    public void autoTypeBlockedClass() {
        Options opts = Options.of(Feature.Read_AutoType);
        String json = "{\"@type\":\"org.apache.commons.collections.Transformer\"}";
        assertThrows(CodecException.class, () -> ONode.ofJson(json, opts).toBean(Object.class));
    }

    /// /////////////////
    // @type 非法格式校验
    /// /////////////////

    @Test
    public void autoTypeInvalidClassName() {
        Options opts = Options.of(Feature.Read_AutoType);
        String json = "{\"@type\":\"abc def\",\"x\":1}"; // 含空格，非合法类名
        assertThrows(CodecException.class, () -> ONode.ofJson(json, opts).toBean(Object.class));
    }

    @Test
    public void autoTypeInvalidClassNameIgnoreError() {
        Options opts = Options.of(Feature.Read_AutoType, Feature.Decode_IgnoreError);
        String json = "{\"@type\":\"abc def\",\"x\":1}";
        Object obj = ONode.ofJson(json, opts).toBean(Object.class);
        // 忽略类型声明，按默认类型（LinkedHashMap）解码
        assertTrue(obj instanceof LinkedHashMap);
    }

    /// /////////////////
    // 白名单模式 + 全局黑名单可配置
    /// /////////////////

    @Test
    public void whitelistModeWorks() {
        Options opts = Options.of();
        opts.addTypeChecker(clz -> {
            if (clz.equals("com.example.AllowedBean")) {
                return TypeChecker.Result.ALLOW;
            }
            if (clz.startsWith("com.")) {
                return TypeChecker.Result.DENY;
            }
            return TypeChecker.Result.SKIP;
        });

        assertFalse(opts.isTypeBlocked("com.example.AllowedBean")); // 白名单内放行
        assertTrue(opts.isTypeBlocked("com.example.EvilBean"));     // 白名单外拒绝
    }

    @Test
    public void globalBlacklistRemove() {
        TypeSafelist.GLOBAL.denyRemove("com.alibaba.fastjson.");
        try {
            Options opts = Options.of();
            assertFalse(opts.isTypeBlocked("com.alibaba.fastjson.JSONObject"));
        } finally {
            // 恢复，避免影响其他测试
            TypeSafelist.GLOBAL.denyAdd("com.alibaba.fastjson.");
        }
    }

    /// /////////////////
    // 辅助 Bean
    /// /////////////////

    public static class ClzBean {
        public Class<?> clz;
    }

    public static class ListBean {
        public List<String> list;
    }
}
