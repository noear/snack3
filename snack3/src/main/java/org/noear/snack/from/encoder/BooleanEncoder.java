package org.noear.snack.from.encoder;

import org.noear.snack.ONode;
import org.noear.snack.core.NodeEncoder;

/**
 * @author noear 2021/10/11 created
 */
public class BooleanEncoder implements NodeEncoder<Boolean> {
    public static final BooleanEncoder instance = new BooleanEncoder();

    @Override
    public void encode(Boolean source, ONode target) {
        target.val().setBool(source);
    }
}
