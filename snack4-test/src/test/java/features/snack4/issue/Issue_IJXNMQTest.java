package features.snack4.issue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * java.time 默认序列化格式回归测试。
 *
 * @author noear 2026/7/24 created
 * @since 4.0
 */
public class Issue_IJXNMQTest {
    @Test
    public void default_java_time_serialization() {
        TimeBean bean = new TimeBean();
        bean.localDate = LocalDate.of(2026, 6, 29);
        bean.localTime = LocalTime.of(18, 18, 52, 123_000_000);
        bean.localDateTime = LocalDateTime.of(2026, 6, 29, 18, 18, 52);
        bean.offsetDateTime = OffsetDateTime.of(2026, 6, 29, 18, 18, 52, 0, ZoneOffset.ofHours(8));
        bean.offsetTime = OffsetTime.of(18, 18, 52, 0, ZoneOffset.ofHours(8));
        bean.zonedDateTime = ZonedDateTime.of(2026, 6, 29, 18, 18, 52, 0, ZoneId.of("Asia/Shanghai"));
        bean.year = Year.of(2026);
        bean.yearMonth = YearMonth.of(2026, 6);
        bean.instant = Instant.parse("2026-06-29T10:18:52Z");
        bean.period = Period.of(1, 2, 3);

        ONode node = ONode.ofBean(bean);

        Assertions.assertEquals("2026-06-29T10:18:52Z", node.get("instant").getString());
        Assertions.assertEquals("2026-06-29", node.get("localDate").getString());
        Assertions.assertFalse(node.get("localDateTime").isString());
        Assertions.assertEquals("18:18:52.123", node.get("localTime").getString());
        Assertions.assertEquals("2026-06-29T18:18:52+08:00", node.get("offsetDateTime").getString());
        Assertions.assertEquals("18:18:52+08:00", node.get("offsetTime").getString());
        Assertions.assertEquals("P1Y2M3D", node.get("period").getString());
        Assertions.assertEquals("2026", node.get("year").getString());
        Assertions.assertEquals("2026-06", node.get("yearMonth").getString());
        Assertions.assertEquals("2026-06-29T18:18:52+08:00[Asia/Shanghai]", node.get("zonedDateTime").getString());
    }

    @Test
    public void local_time_default_serialization() {
        Assertions.assertEquals("18:18", ONode.ofBean(LocalTime.of(18, 18)).getString());
        Assertions.assertEquals(LocalTime.of(18, 18), ONode.ofJson("\"18:18\"").toBean(LocalTime.class));
        Assertions.assertEquals("18:18:52.123456789", ONode.ofBean(LocalTime.of(18, 18, 52, 123_456_789)).getString());
    }

    @Test
    public void java_time_roundtrip() {
        TimeBean expected = new TimeBean();
        expected.instant = Instant.parse("2026-06-29T10:18:52Z");
        expected.localDate = LocalDate.of(2026, 6, 29);
        expected.localDateTime = LocalDateTime.of(2026, 6, 29, 18, 18, 52);
        expected.localTime = LocalTime.of(18, 18, 52, 123_456_789);
        expected.offsetDateTime = OffsetDateTime.of(2026, 6, 29, 18, 18, 52, 987_654_321, ZoneOffset.ofHoursMinutes(5, 30));
        expected.offsetTime = OffsetTime.of(18, 18, 52, 987_654_321, ZoneOffset.ofHoursMinutes(5, 30));
        expected.period = Period.of(1, 2, 3);
        expected.year = Year.of(2026);
        expected.yearMonth = YearMonth.of(2026, 6);
        expected.zonedDateTime = ZonedDateTime.of(2026, 1, 15, 18, 18, 52, 987_654_321, ZoneId.of("Asia/Shanghai"));

        TimeBean actual = ONode.ofJson(ONode.ofBean(expected).toJson()).toBean(TimeBean.class);

        Assertions.assertEquals(expected.instant, actual.instant);
        Assertions.assertEquals(expected.localDate, actual.localDate);
        Assertions.assertEquals(expected.localDateTime, actual.localDateTime);
        Assertions.assertEquals(expected.localTime, actual.localTime);
        Assertions.assertEquals(expected.offsetDateTime, actual.offsetDateTime);
        Assertions.assertEquals(expected.offsetTime, actual.offsetTime);
        Assertions.assertEquals(expected.period, actual.period);
        Assertions.assertEquals(expected.year, actual.year);
        Assertions.assertEquals(expected.yearMonth, actual.yearMonth);
        Assertions.assertEquals(expected.zonedDateTime, actual.zonedDateTime);
        Assertions.assertEquals(expected.zonedDateTime.getZone(), actual.zonedDateTime.getZone());
    }

    @Test
    public void java_time_null_deserialization() {
        String json = "{\"localTime\":null,\"offsetDateTime\":null,\"offsetTime\":null,\"zonedDateTime\":null}";
        TimeBean bean = ONode.ofJson(json).toBean(TimeBean.class);

        Assertions.assertNull(bean.localTime);
        Assertions.assertNull(bean.offsetDateTime);
        Assertions.assertNull(bean.offsetTime);
        Assertions.assertNull(bean.zonedDateTime);
    }

    public static class TimeBean {
        public Instant instant;
        public LocalDate localDate;
        public LocalDateTime localDateTime;
        public LocalTime localTime;
        public OffsetDateTime offsetDateTime;
        public OffsetTime offsetTime;
        public Period period;
        public Year year;
        public YearMonth yearMonth;
        public ZonedDateTime zonedDateTime;
    }
}
