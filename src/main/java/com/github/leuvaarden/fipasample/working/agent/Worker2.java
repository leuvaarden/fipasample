package com.github.leuvaarden.fipasample.working.agent;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.leuvaarden.fipasample.common.data.Ability;
import com.github.leuvaarden.fipasample.common.data.ServiceType;
import com.github.leuvaarden.fipasample.common.util.SerializationUtils;
import jade.util.Logger;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Worker2 extends AbstractWorkingAgent {
    private final Logger log = Logger.getMyLogger(this.getClass().getName());
    private final List<Ability> abilities = new ArrayList<>();

    public Worker2() {
        super(ServiceType.SOLVE_TASK, Duration.ofSeconds(5), Duration.ofSeconds(10));
        Ability doubleToString = new Ability();
        doubleToString.setTime(Duration.ofMillis(100));
        doubleToString.setAbilityUuid(UUID.randomUUID());
        doubleToString.setInputType("Double");
        doubleToString.setOutputType("String");
        this.abilities.add(doubleToString);
    }

    @Override
    public List<Ability> getAbilities() {
        return this.abilities;
    }

    @SneakyThrows
    @Override
    public Object work(Ability ability, String input) {
        if (ability.getInputType().equals("Double") && ability.getOutputType().equals("String")) {
            List<Double> typedInput = SerializationUtils.deserialize(input, new TypeReference<>() {});
            return typedInput.stream().map(this::workDoubleToString).collect(Collectors.toList());
        }
        log.log(Level.WARNING, "Not found work for ability [{0}]", ability);
        return null;
    }

    private String workDoubleToString(Double d) {
        if (d == null) {
            return "";
        }
        return RandomStringUtils.randomAlphabetic((int) Math.abs(d) + 10);
    }
}
