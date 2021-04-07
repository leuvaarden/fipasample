package com.github.leuvaarden.fipasample.working.agent;

import com.github.leuvaarden.fipasample.common.data.Ability;
import com.github.leuvaarden.fipasample.common.data.ServiceType;
import com.github.leuvaarden.fipasample.working.behaviour.RegisteringService;
import com.github.leuvaarden.fipasample.working.behaviour.RespondingToAccept;
import com.github.leuvaarden.fipasample.working.behaviour.RespondingToCallForProposal;
import jade.core.Agent;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

public abstract class AbstractWorkingAgent extends Agent {
    private final ServiceType serviceType;
    private final Duration cfpCheckPeriod;
    private final Duration acceptCheckPeriod;

    public AbstractWorkingAgent(ServiceType serviceType, Duration cfpCheckPeriod, Duration acceptCheckPeriod) {
        super();
        this.serviceType = serviceType;
        this.cfpCheckPeriod = cfpCheckPeriod;
        this.acceptCheckPeriod = acceptCheckPeriod;
    }

    @Override
    protected void setup() {
        super.setup();
        addBehaviour(new RegisteringService(this, serviceType));
        addBehaviour(new RespondingToCallForProposal(this, cfpCheckPeriod));
        addBehaviour(new RespondingToAccept(this, acceptCheckPeriod));
        getAbilities().forEach(ability -> ability.setAgentName(this.getAID().getName()));
    }

    abstract public List<Ability> getAbilities();

    abstract public Object work(UUID abilityUuid, String input);

}
