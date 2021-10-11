package org.noear.snack.from.encoder;

import org.noear.snack.ONode;
import org.noear.snack.core.NodeEncoder;

/**
 * @author noear 2021/10/11 created
 */
public class StringEncoder implements NodeEncoder {
    public static final StringEncoder instance = new StringEncoder();

    @Override
    public void encode(Object source, ONode target) {
        target.val().setString((String) source);
    }
}
