package com.github.leuvaarden.fipasample.working.behaviour;

import com.github.leuvaarden.fipasample.common.util.SerializationUtils;
import com.github.leuvaarden.fipasample.working.agent.AbstractWorkingAgent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;
import lombok.SneakyThrows;

import java.time.Duration;
import java.util.UUID;
import java.util.logging.Level;

import static jade.lang.acl.MessageTemplate.MatchPerformative;

public class RespondingToAccept extends TickerBehaviour {
    private final jade.util.Logger log = Logger.getMyLogger(this.getClass().getName());
    private final AbstractWorkingAgent abstractWorkingAgent;

    public RespondingToAccept(AbstractWorkingAgent abstractWorkingAgent, Duration acceptCheckPeriod) {
        super(abstractWorkingAgent, acceptCheckPeriod.toMillis());
        this.abstractWorkingAgent = abstractWorkingAgent;
    }

    @SneakyThrows
    @Override
    protected void onTick() {
        MessageTemplate messageTemplate = MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
        ACLMessage acceptProposal;
        while ((acceptProposal = abstractWorkingAgent.receive(messageTemplate)) != null) {
            log.log(Level.INFO, "Message received: [{0}]", acceptProposal);
            UUID requiredAbility = UUID.fromString(acceptProposal.getInReplyTo());
            Object output = abstractWorkingAgent.work(requiredAbility, acceptProposal.getContent());
            ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
            inform.setSender(abstractWorkingAgent.getAID());
            inform.addReceiver(acceptProposal.getSender());
            inform.setInReplyTo(acceptProposal.getReplyWith());
            inform.setContent(SerializationUtils.serialize(output));
            abstractWorkingAgent.send(inform);
            log.log(Level.INFO, "Message sent: [{0}]", inform);
        }
    }
}
