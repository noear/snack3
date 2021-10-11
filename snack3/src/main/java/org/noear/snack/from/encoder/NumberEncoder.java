package org.noear.snack.from.encoder;

import org.noear.snack.ONode;
import org.noear.snack.core.NodeEncoder;

/**
 * @author noear 2021/10/11 created
 */
public class NumberEncoder implements NodeEncoder<Number> {
    public static final NumberEncoder instance = new NumberEncoder();

    @Override
    public void encode(Number source, ONode target) {
        target.val().setNumber(source);
    }
}
