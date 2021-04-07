package com.github.leuvaarden.fipasample.working.behaviour;

import com.github.leuvaarden.fipasample.common.data.Ability;
import com.github.leuvaarden.fipasample.common.util.SerializationUtils;
import com.github.leuvaarden.fipasample.working.agent.AbstractWorkingAgent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;
import lombok.SneakyThrows;

import java.time.Duration;
import java.util.logging.Level;

import static jade.lang.acl.MessageTemplate.MatchPerformative;

public class RespondingToCallForProposal extends TickerBehaviour {
    private final Logger log = Logger.getMyLogger(this.getClass().getName());
    private final AbstractWorkingAgent abstractWorkingAgent;

    public RespondingToCallForProposal(AbstractWorkingAgent abstractWorkingAgent, Duration cfpCheckPeriod) {
        super(abstractWorkingAgent, cfpCheckPeriod.toMillis());
        this.abstractWorkingAgent = abstractWorkingAgent;
    }

    @SneakyThrows
    @Override
    protected void onTick() {
        MessageTemplate messageTemplate = MatchPerformative(ACLMessage.CFP);
        ACLMessage cfp;
        while ((cfp = abstractWorkingAgent.receive(messageTemplate)) != null) {
            log.log(Level.INFO, "Message received: [{0}]", cfp);
            for (Ability ability : abstractWorkingAgent.getAbilities()) {
                ACLMessage propose = new ACLMessage(ACLMessage.PROPOSE);
                propose.setInReplyTo(cfp.getReplyWith());
                propose.setReplyWith(ability.getAbilityUuid().toString());
                propose.setSender(abstractWorkingAgent.getAID());
                propose.addReceiver(cfp.getSender());
                propose.setContent(SerializationUtils.serialize(ability));
                abstractWorkingAgent.send(propose);
                log.log(Level.INFO, "Message sent: [{0}]", propose);
            }
        }
    }
}
