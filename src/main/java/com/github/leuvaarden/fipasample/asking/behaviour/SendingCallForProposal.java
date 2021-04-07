package com.github.leuvaarden.fipasample.asking.behaviour;

import com.github.leuvaarden.fipasample.asking.agent.AbstractAskingAgent;
import com.github.leuvaarden.fipasample.common.util.SerializationUtils;
import com.github.leuvaarden.fipasample.common.data.ServiceType;
import com.github.leuvaarden.fipasample.common.data.TaskStatus;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class SendingCallForProposal extends TickerBehaviour {
    private final Logger log = Logger.getMyLogger(this.getClass().getName());
    private final AbstractAskingAgent abstractAskingAgent;
    private final ServiceType serviceType;

    public SendingCallForProposal(AbstractAskingAgent abstractAskingAgent, ServiceType serviceType, Duration period) {
        super(abstractAskingAgent, period.toMillis());
        this.abstractAskingAgent = abstractAskingAgent;
        this.serviceType = serviceType;
    }

    @Override
    protected void onTick() {
        // search for agents that provide
        DFAgentDescription[] workerDescriptions = safeSearch();
        if (workerDescriptions == null || workerDescriptions.length == 0) {
            log.log(Level.INFO, "Workers not found");
            return;
        }

        // gather uuids with CREATED status
        abstractAskingAgent.getTaskStatuses()
                .stream()
                .filter(entry -> TaskStatus.CREATED.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .map(uuid -> {
                    log.log(Level.INFO, "Creating cfp for task: [{0}]", uuid);
                    return Pair.of(uuid, createCfp(uuid, abstractAskingAgent, workerDescriptions));
                })
                .forEach(pair -> {
                    abstractAskingAgent.send(pair.getRight());
                    abstractAskingAgent.changeTaskStatus(pair.getLeft(), TaskStatus.CFP);
                    log.log(Level.INFO, "CFP sent for task: [{0}]", pair.getLeft());
                });
    }

    @SneakyThrows
    private ACLMessage createCfp(UUID taskUuid, AbstractAskingAgent abstractAskingAgent, DFAgentDescription[] workerDescriptions) {
        ACLMessage aclMessage = new ACLMessage(ACLMessage.CFP);
        aclMessage.setReplyWith(taskUuid.toString());
        aclMessage.setSender(abstractAskingAgent.getAID());
        aclMessage.setContent(SerializationUtils.serialize(abstractAskingAgent.getTaskInfo(taskUuid)));
        Arrays.stream(workerDescriptions)
                .map(DFAgentDescription::getName)
                .forEach(aclMessage::addReceiver);
        return aclMessage;
    }

    private DFAgentDescription[] safeSearch() {
        try {
            return DFService.search(abstractAskingAgent, workerDescription());
        } catch (FIPAException e) {
            log.log(Level.WARNING, "Error while searching for workers", e);
            return null;
        }
    }

    private DFAgentDescription workerDescription() {
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(serviceType.name());
        DFAgentDescription dfAgentDescription = new DFAgentDescription();
        dfAgentDescription.addServices(serviceDescription);
        return dfAgentDescription;
    }
}
