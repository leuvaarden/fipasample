package com.github.leuvaarden.fipasample.common.data;

import lombok.Data;

import java.time.Duration;
import java.util.UUID;

@Data
public class Ability {
    private String agentName;
    private UUID abilityUuid;
    private String inputType;
    private String outputType;
    private Duration time;
}
