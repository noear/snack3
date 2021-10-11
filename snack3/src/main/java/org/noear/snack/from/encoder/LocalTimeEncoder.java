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
public class LocalTimeEncoder implements NodeEncoder<LocalTime> {
    public static final LocalTimeEncoder instance = new LocalTimeEncoder();

    @Override
    public void encode(LocalTime source, ONode target) {
        Instant instant = (source).atDate(LocalDate.of(1970,1,1)).atZone(DEFAULTS.DEF_TIME_ZONE.toZoneId()).toInstant();
        target.val().setDate(new Date(instant.getEpochSecond() * 1000));
    }
}
