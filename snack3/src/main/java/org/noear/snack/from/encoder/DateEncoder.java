package org.noear.snack.from.encoder;

import org.noear.snack.ONode;
import org.noear.snack.core.NodeEncoder;

import java.util.Date;

/**
 * @author noear 2021/10/11 created
 */
public class DateEncoder implements NodeEncoder<Date> {
    public static final DateEncoder instance = new DateEncoder();

    @Override
    public void encode(Date source, ONode target) {
        target.val().setDate(source);
    }
}
