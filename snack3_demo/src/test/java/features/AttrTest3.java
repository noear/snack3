package features;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;
import org.noear.snack.annotation.ONodeAttr;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 *
 * @author noear 2025/9/1 created
 *
 */
public class AttrTest3 {
    @Test
    public void test() {
        // 测试 @ONodeAttr.format 仍有有效
        LocalDateTime dateTime = LocalDateTime.parse("2025-08-08T12:34:01");
        AnnoTimeObject t = new AnnoTimeObject(dateTime);
        String json = ONode.serialize(t);

        Assertions.assertTrue(json.contains("\"2025-08-08 12:34:01\""));
        Assertions.assertTrue(json.contains("\"2025/08/08\""));
        Assertions.assertTrue(json.contains("\"12:34:01\""));

        // 下面这行代码会报错，因为 Snack3 在反序列化时还没有 @JSONField.format 优先
        AnnoTimeObject obj = ONode.deserialize(json, AnnoTimeObject.class);
        Assertions.assertEquals(obj, t);
    }

    @Test
    public void test2() {
        String json = "{\"dateTime\":\"2025-08-08 12:34:01\",\"date\":\"2025/08/08\",\"time\":\"12:34:01\"}";

        AnnoTimeObject obj = ONode.load(json).toObject(AnnoTimeObject.class);

        System.out.println(obj);
        assert obj != null;
        assert obj.dateTime != null;
        assert obj.date != null;
        assert obj.time != null;

    }



    public static class AnnoTimeObject {

        @ONodeAttr(format = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime dateTime;

        @ONodeAttr(format = "yyyy/MM/dd")
        private LocalDate date;

        @ONodeAttr(format = "HH:mm:ss")
        private LocalTime time;

        public AnnoTimeObject() {
        }

        public AnnoTimeObject(LocalDateTime dateTime) {
            this.dateTime = dateTime;
            this.date = dateTime.toLocalDate();
            this.time = dateTime.toLocalTime();
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public LocalDateTime getDateTime() {
            return dateTime;
        }

        public void setDateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
        }

        public LocalTime getTime() {
            return time;
        }

        public void setTime(LocalTime time) {
            this.time = time;
        }

        @Override
        public String toString() {
            return "AnnoTimeObject{" +
                    "dateTime=" + dateTime +
                    ", date=" + date +
                    ", time=" + time +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof AnnoTimeObject)) return false;
            AnnoTimeObject that = (AnnoTimeObject) o;
            return Objects.equals(dateTime, that.dateTime) && Objects.equals(date, that.date) && Objects.equals(time, that.time);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dateTime, date, time);
        }
    }
}
