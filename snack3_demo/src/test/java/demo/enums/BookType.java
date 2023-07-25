package demo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.noear.snack.annotation.EnumCode;

/**
 * 用于单元测试枚举解析
 *
 * @author hans
 */
@ToString
@AllArgsConstructor
@Getter
public enum BookType {
    NOVEL(1,"小说"),
    CLASSICS(2,"名著"),
    ;

    @EnumCode
    public final int code;
    public final String des;


}
