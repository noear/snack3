package _models;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.OffsetTime;

/**
 * @author noear 2024/7/17 created
 */
@Data
@Accessors(chain = true)
public class FoodRestarurantHoursPageVO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 营业名称
     */
    private String hoursName;

    /**
     * 日期
     */
    private LocalDate date;

    /**
     * 开始时间
     */
    private OffsetTime startTime;

    /**
     * 结束时间
     */
    private OffsetTime endTime;
}
