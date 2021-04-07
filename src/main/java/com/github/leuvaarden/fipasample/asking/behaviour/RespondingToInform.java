package com.github.leuvaarden.fipasample.asking.behaviour;

import com.github.leuvaarden.fipasample.asking.agent.AbstractAskingAgent;
import com.github.leuvaarden.fipasample.common.data.Ability;
import com.github.leuvaarden.fipasample.common.data.TaskInfo;
import com.github.leuvaarden.fipasample.common.data.TaskStatus;
import com.github.leuvaarden.fipasample.common.util.SerializationUtils;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;
import lombok.SneakyThrows;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import static jade.lang.acl.MessageTemplate.MatchInReplyTo;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchSender;
import static jade.lang.acl.MessageTemplate.and;

public class RespondingToInform extends TickerBehaviour {
    private final Logger log = Logger.getMyLogger(this.getClass().getName());
    private final AbstractAskingAgent abstractAskingAgent;
    private final UUID uuid;
    private final Ability ability;
    private final MessageTemplate resultTemplate;

    public RespondingToInform(AbstractAskingAgent abstractAskingAgent, UUID uuid, Ability ability, Duration period) {
        super(abstractAskingAgent, period.toMillis());
        this.abstractAskingAgent = abstractAskingAgent;
        this.uuid = uuid;
        this.ability = ability;
        resultTemplate = and(
                MatchPerformative(ACLMessage.INFORM),
                and(
                        MatchSender(new AID(ability.getAgentName(), true)),
                        MatchInReplyTo(uuid.toString())
                )
        );
    }

    @Override
    protected void onTick() {
        TaskStatus taskStatus = abstractAskingAgent.getTaskStatus(uuid);
        if (TaskStatus.ACCEPT_PROPOSAL.equals(taskStatus)) {
            ACLMessage aclMessage = abstractAskingAgent.receive(resultTemplate);
            if (aclMessage == null) {
                return;
            }
            TaskInfo taskInfo = abstractAskingAgent.getTaskInfo(uuid);
            Object output = deserializeOutput(aclMessage.getContent());
            abstractAskingAgent.saveTaskOutputs(uuid, output);
            abstractAskingAgent.changeTaskStatus(uuid, TaskStatus.PROGRESS);
            this.stop();
            log.log(Level.INFO, "Received result for task: [{0}]", uuid);
        } else if (TaskStatus.PROGRESS.equals(taskStatus)) {
            Object inputs = abstractAskingAgent.getTaskOutputs(uuid);
            if (inputs == null) {
                inputs = abstractAskingAgent.getTaskInputs(uuid);
            }
            ACLMessage aclMessage = serializeInput(inputs, ability);
            abstractAskingAgent.send(aclMessage);
            abstractAskingAgent.changeTaskStatus(uuid, TaskStatus.ACCEPT_PROPOSAL);
            log.log(Level.INFO, "Message sent: [{0}]", aclMessage);
        }
    }

    @SneakyThrows
    private Object deserializeOutput(String content) {
        return SerializationUtils.deserialize(content, List.class);
    }

    @SneakyThrows
    private ACLMessage serializeInput(Object inputs, Ability ability) {
        ACLMessage aclMessage = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
        aclMessage.setSender(abstractAskingAgent.getAID());
        aclMessage.addReceiver(new AID(ability.getAgentName(), true));
        aclMessage.setInReplyTo(ability.getAbilityUuid().toString());
        aclMessage.setReplyWith(uuid.toString());
        aclMessage.setContent(SerializationUtils.serialize(inputs));
        return aclMessage;
    }
}
