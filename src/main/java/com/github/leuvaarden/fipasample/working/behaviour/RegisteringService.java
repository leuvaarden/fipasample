package com.github.leuvaarden.fipasample.working.behaviour;

import com.github.leuvaarden.fipasample.common.data.ServiceType;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.util.Logger;

import java.util.logging.Level;

public class RegisteringService extends OneShotBehaviour {
    private final Logger log = Logger.getMyLogger(this.getClass().getName());
    private final ServiceType serviceType;

    public RegisteringService(Agent agent, ServiceType serviceType) {
        super(agent);
        this.serviceType = serviceType;
    }

    @Override
    public void action() {
        DFAgentDescription dfAgentDescription = new DFAgentDescription();
        dfAgentDescription.setName(myAgent.getAID());
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setName(myAgent.getName() + ":" + serviceType.name());
        serviceDescription.setType(serviceType.name());
        dfAgentDescription.addServices(serviceDescription);
        try {
            DFService.register(myAgent, dfAgentDescription);
        } catch (FIPAException fe) {
            log.log(Level.WARNING, "Can not register service", fe);
        }
    }
}
