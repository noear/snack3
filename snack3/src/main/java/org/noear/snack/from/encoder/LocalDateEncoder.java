package org.noear.snack.from.encoder;

import org.noear.snack.ONode;
import org.noear.snack.core.DEFAULTS;
import org.noear.snack.core.NodeEncoder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

/**
 * @author noear 2021/10/11 created
 */
public class LocalDateEncoder implements NodeEncoder<LocalDate> {
    public static final LocalDateEncoder instance = new LocalDateEncoder();

    @Override
    public void encode(LocalDate source, ONode target) {
        Instant instant = (source).atTime(LocalTime.MIN).atZone(DEFAULTS.DEF_TIME_ZONE.toZoneId()).toInstant();
        target.val().setDate(new Date(instant.getEpochSecond() * 1000));
    }
}
