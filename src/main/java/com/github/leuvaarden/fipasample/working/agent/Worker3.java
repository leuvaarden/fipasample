package com.github.leuvaarden.fipasample.working.agent;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.leuvaarden.fipasample.common.data.Ability;
import com.github.leuvaarden.fipasample.common.data.ServiceType;
import com.github.leuvaarden.fipasample.common.util.SerializationUtils;
import jade.util.Logger;
import lombok.SneakyThrows;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Worker3 extends AbstractWorkingAgent {
    private final Logger log = Logger.getMyLogger(this.getClass().getName());
    private final List<Ability> abilities = new ArrayList<>();

    public Worker3() {
        super(ServiceType.SOLVE_TASK, Duration.ofSeconds(5), Duration.ofSeconds(10));
        Ability doubleToInteger = new Ability();
        doubleToInteger.setTime(Duration.ofMillis(100));
        doubleToInteger.setAbilityUuid(UUID.randomUUID());
        doubleToInteger.setInputType("Double");
        doubleToInteger.setOutputType("Integer");
        this.abilities.add(doubleToInteger);
    }

    @Override
    public List<Ability> getAbilities() {
        return this.abilities;
    }

    @SneakyThrows
    @Override
    public Object work(UUID abilityUuid, String input) {
        Optional<Ability> optionalAbility = getAbilities().stream()
                .filter(ability -> ability.getAbilityUuid().equals(abilityUuid))
                .findFirst();
        if (optionalAbility.isEmpty()) {
            log.log(Level.WARNING, "Not found ability for uuid: [{0}]", abilityUuid);
            return null;
        }
        Ability ability = optionalAbility.get();
        if (ability.getInputType().equals("Double") && ability.getOutputType().equals("Integer")) {
            List<Double> typedInput = SerializationUtils.deserialize(input, new TypeReference<>() {});
            return typedInput.stream().map(this::doubleToInteger).collect(Collectors.toList());
        }
        log.log(Level.WARNING, "Not found work for ability [{0}]", ability);
        return null;
    }

    private Integer doubleToInteger(Double d) {
        if (d == null) {
            return 0;
        }
        return d.intValue();
    }
}
