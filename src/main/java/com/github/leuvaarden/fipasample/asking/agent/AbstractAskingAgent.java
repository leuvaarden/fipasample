package com.github.leuvaarden.fipasample.asking.agent;

import com.github.leuvaarden.fipasample.asking.behaviour.SendingCallForProposal;
import com.github.leuvaarden.fipasample.asking.behaviour.GeneratingTask;
import com.github.leuvaarden.fipasample.asking.behaviour.RespondingToPropose;
import com.github.leuvaarden.fipasample.common.data.Ability;
import com.github.leuvaarden.fipasample.common.data.ServiceType;
import com.github.leuvaarden.fipasample.common.data.TaskInfo;
import com.github.leuvaarden.fipasample.common.data.TaskStatus;
import jade.core.Agent;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractAskingAgent extends Agent {
    private final Duration cfpExpiration;
    private final Duration cfpSendPeriod;
    private final Duration proposalCheckPeriod;

    public AbstractAskingAgent(Duration cfpExpiration, Duration cfpSendPeriod, Duration proposalCheckPeriod) {
        super();
        this.cfpExpiration = cfpExpiration;
        this.cfpSendPeriod = cfpSendPeriod;
        this.proposalCheckPeriod = proposalCheckPeriod;
    }

    @Override
    protected void setup() {
        super.setup();
        addBehaviour(new GeneratingTask(this));
        addBehaviour(new SendingCallForProposal(this, ServiceType.SOLVE_TASK, cfpSendPeriod));
        addBehaviour(new RespondingToPropose(this, cfpExpiration, proposalCheckPeriod));
    }

    abstract public TaskInfo getTaskInfo(UUID uuid);

    abstract public List<Ability> getTaskAbilities(UUID uuid);

    abstract public void createTask(UUID uuid, TaskInfo taskInfo, Object inputs);

    abstract public void changeTaskStatus(UUID uuid, TaskStatus taskStatus);

    abstract public void saveTaskAbility(UUID uuid, Ability ability);

    abstract public Collection<Map.Entry<UUID, TaskStatus>> getTaskStatuses();

    abstract public Object getTaskInputs(UUID uuid);

    abstract public Object getTaskOutputs(UUID uuid);

    abstract public void saveTaskOutputs(UUID uuid, Object outputs);

    abstract public TaskStatus getTaskStatus(UUID uuid);
}
