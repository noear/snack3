package _models;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear 2023/8/31 created
 */
@Getter
@Setter
public class PersonColl {
    String label;
    Map<String,Person> users = new LinkedHashMap<>();
}
