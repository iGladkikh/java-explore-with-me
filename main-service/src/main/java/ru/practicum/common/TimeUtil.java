package ru.practicum.common;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@UtilityClass
public class TimeUtil {
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final ZoneId DEFAULT_ZONE_ID = ZoneOffset.systemDefault();

    public LocalDateTime instantToLocalDateTime(Instant time) {
        if (time == null) {
            return null;
        }
        return LocalDateTime.from(time.atZone(DEFAULT_ZONE_ID));
    }

    public Instant localDateTimeToInstant(LocalDateTime time) {
        if (time == null) {
            return null;
        }
        return Instant.from(time.atZone(DEFAULT_ZONE_ID));
    }
}
