package com.github.leuvaarden.fipasample.asking.behaviour;

import com.github.leuvaarden.fipasample.asking.agent.AbstractAskingAgent;
import com.github.leuvaarden.fipasample.common.data.TaskStatus;
import jade.core.behaviours.OneShotBehaviour;
import jade.util.Logger;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class FinishingTask extends OneShotBehaviour {
    private final Logger log = Logger.getMyLogger(this.getClass().getName());
    private final AbstractAskingAgent abstractAskingAgent;
    private final UUID uuid;

    public FinishingTask(AbstractAskingAgent abstractAskingAgent, UUID uuid) {
        super(abstractAskingAgent);
        this.uuid = uuid;
        this.abstractAskingAgent = abstractAskingAgent;
    }

    @Override
    public void action() {
        abstractAskingAgent.changeTaskStatus(uuid, TaskStatus.DONE);
        log.log(Level.INFO, "Task is done: [{0}]", uuid);
        List inputs = (List) abstractAskingAgent.getTaskInputs(uuid);
        List outputs = (List) abstractAskingAgent.getTaskOutputs(uuid);
        for (int i = 0; i < inputs.size(); i++) {
            log.log(Level.INFO, "input: [{0}], output: [{1}]", new Object[]{inputs.get(i), outputs.get(i)});
        }
    }
}
