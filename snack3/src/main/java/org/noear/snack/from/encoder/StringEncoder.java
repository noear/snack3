package org.noear.snack.from.encoder;

import org.noear.snack.ONode;
import org.noear.snack.core.NodeEncoder;

/**
 * @author noear 2021/10/11 created
 */
public class StringEncoder implements NodeEncoder<String> {
    public static final StringEncoder instance = new StringEncoder();

    @Override
    public void encode(String source, ONode target) {
        target.val().setString(source);
    }
}
