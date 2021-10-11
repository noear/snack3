package org.noear.snack.from.encoder;

import org.noear.snack.ONode;
import org.noear.snack.core.DEFAULTS;
import org.noear.snack.core.NodeEncoder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author noear 2021/10/11 created
 */
public class LocalDateTimeEncoder implements NodeEncoder<LocalDateTime> {
    public static final LocalDateTimeEncoder instance = new LocalDateTimeEncoder();

    @Override
    public void encode(LocalDateTime source, ONode target) {
        Instant instant = (source).atZone(DEFAULTS.DEF_TIME_ZONE.toZoneId()).toInstant();
        target.val().setDate(new Date((instant.getEpochSecond() * 1000) + (instant.getNano() / 1000_000)));
    }
}
