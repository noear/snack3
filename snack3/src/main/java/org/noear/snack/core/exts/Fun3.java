package org.noear.snack.core.exts;

import org.noear.snack.ONode;

public interface Fun3<R,T1,T2,T3> {
    R run(T1 t1, T2 t2, T3 t3);
}
