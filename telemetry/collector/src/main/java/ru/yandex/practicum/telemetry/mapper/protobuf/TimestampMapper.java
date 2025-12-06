package ru.yandex.practicum.telemetry.mapper.protobuf;

import com.google.protobuf.Timestamp;

import java.time.Instant;

public final class TimestampMapper {

    private TimestampMapper() {}

    public static Instant toInstant(Timestamp timestamp) {
        if (timestamp == null) {
            return Instant.now();
        }
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}