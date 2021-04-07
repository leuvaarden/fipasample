package com.github.leuvaarden.fipasample.common.data;

import lombok.Data;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class TaskInfo {
    private UUID uuid;
    private OffsetDateTime created;
    private int inputSize;
    private String inputType;
    private int outputSize;
    private String outputType;
    private Duration timeLimit;
}
