package com.github.leuvaarden.fipasample.working.agent;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.leuvaarden.fipasample.common.data.Ability;
import com.github.leuvaarden.fipasample.common.data.ServiceType;
import com.github.leuvaarden.fipasample.common.util.SerializationUtils;
import jade.util.Logger;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Worker1 extends AbstractWorkingAgent {
    private final Logger log = Logger.getMyLogger(this.getClass().getName());
    private final List<Ability> abilities = new ArrayList<>();

    public Worker1() {
        super(ServiceType.SOLVE_TASK, Duration.ofSeconds(5), Duration.ofSeconds(10));
        Ability stringToInteger = new Ability();
        stringToInteger.setTime(Duration.ofMinutes(1));
        stringToInteger.setAbilityUuid(UUID.randomUUID());
        stringToInteger.setInputType("String");
        stringToInteger.setOutputType("Integer");
        this.abilities.add(stringToInteger);
        Ability stringToDouble = new Ability();
        stringToDouble.setTime(Duration.ofMillis(100));
        stringToDouble.setAbilityUuid(UUID.randomUUID());
        stringToDouble.setInputType("String");
        stringToDouble.setOutputType("Double");
        this.abilities.add(stringToDouble);
    }

    @Override
    public List<Ability> getAbilities() {
        return this.abilities;
    }

    @SneakyThrows
    @Override
    public Object work(Ability ability, String input) {
        if (ability.getInputType().equals("String") && ability.getOutputType().equals("Double")) {
            List<String> typedInput = SerializationUtils.deserialize(input, new TypeReference<>() {});
            return typedInput.stream().map(this::workStringToDouble).collect(Collectors.toList());
        }
        if (ability.getInputType().equals("String") && ability.getOutputType().equals("Integer")) {
            List<String> typedInput = SerializationUtils.deserialize(input, new TypeReference<>() {});
            return typedInput.stream().map(this::workStringToInteger).collect(Collectors.toList());
        }
        log.log(Level.WARNING, "Not found work for ability [{0}]", ability);
        return null;
    }

    private Double workStringToDouble(String s) {
        if (s == null) {
            return .0;
        }
        return Math.PI * s.length();
    }

    private Integer workStringToInteger(String s) {
        if (s == null) {
            return 0;
        }
        return RandomUtils.nextInt(100, 200);
    }
}
