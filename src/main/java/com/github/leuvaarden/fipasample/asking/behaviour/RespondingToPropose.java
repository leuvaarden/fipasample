package com.github.leuvaarden.fipasample.asking.behaviour;

import com.github.leuvaarden.fipasample.asking.agent.AbstractAskingAgent;
import com.github.leuvaarden.fipasample.common.util.AbilityUtils;
import com.github.leuvaarden.fipasample.common.data.Ability;
import com.github.leuvaarden.fipasample.common.util.SerializationUtils;
import com.github.leuvaarden.fipasample.common.data.TaskInfo;
import com.github.leuvaarden.fipasample.common.data.TaskStatus;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;
import lombok.SneakyThrows;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

// TODO divide to small behaviours
public class RespondingToPropose extends TickerBehaviour {
    private final Logger log = Logger.getMyLogger(this.getClass().getName());
    private final Duration maxWait;
    private final AbstractAskingAgent abstractAskingAgent;
    private final Duration CHECK_RESULT_PERIOD = Duration.ofSeconds(5);

    public RespondingToPropose(AbstractAskingAgent abstractAskingAgent, Duration maxWait, Duration period) {
        super(abstractAskingAgent, period.toMillis());
        this.abstractAskingAgent = abstractAskingAgent;
        this.maxWait = maxWait;
    }

    @Override
    protected void onTick() {
        MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
        ACLMessage proposeMessage;

        // receive abilities
        while ((proposeMessage = abstractAskingAgent.receive(messageTemplate)) != null) {
            Ability ability = getAbility(proposeMessage);
            UUID uuid = UUID.fromString(proposeMessage.getInReplyTo());
            if (ability != null) {
                abstractAskingAgent.saveTaskAbility(uuid, ability);
            } else {
                log.log(Level.WARNING, "Could not parse ability for: [{0}]", proposeMessage);
            }
        }

        // processing tasks
        abstractAskingAgent.getTaskStatuses()
                .stream()
                // filter tasks in CFP status
                .filter(entry -> TaskStatus.CFP.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                // filter tasks with existing abilities
                .filter(uuid -> !abstractAskingAgent.getTaskAbilities(uuid).isEmpty())
                .forEach(this::handleProposals);
    }

    private void handleProposals(UUID uuid) {
        TaskInfo taskInfo = abstractAskingAgent.getTaskInfo(uuid);
        // still has some time to wait for better proposals
        if (OffsetDateTime.now().isBefore(taskInfo.getCreated().plus(maxWait))) {
            log.log(Level.INFO, "Waiting for more proposals for task: [{0}]", uuid);
            return;
        }
        // get desired ability
        Ability desired = fromTaskInfo(taskInfo);
        // get possible ability chains
        List<List<Ability>> chains = AbilityUtils.findChain(abstractAskingAgent.getTaskAbilities(uuid), desired)
                .map(abilityStream -> abilityStream.collect(Collectors.toList()))
                .collect(Collectors.toList());
        logRegularChains(chains);
        // get fastest ability chains
        List<List<Ability>> fastest = AbilityUtils.findFastest(chains);
        logFastestChains(fastest);
        if (fastest.isEmpty()) {
            log.log(Level.INFO, "No chains found for task: [{0}]", uuid);
            return;
        }
        // schedule sequential solution
        SequentialBehaviour sequentialBehaviour = new SequentialBehaviour(abstractAskingAgent);
        for (Ability ability : fastest.get(0)) {
            sequentialBehaviour.addSubBehaviour(new RespondingToInform(abstractAskingAgent, uuid, ability, CHECK_RESULT_PERIOD));
        }
        sequentialBehaviour.addSubBehaviour(new FinishingTask(abstractAskingAgent, uuid));
        abstractAskingAgent.addBehaviour(sequentialBehaviour);
        // change task status to PROGRESS
        abstractAskingAgent.changeTaskStatus(uuid, TaskStatus.PROGRESS);
    }

    @SneakyThrows
    private Ability getAbility(ACLMessage proposeMessage) {
        return SerializationUtils.deserialize(proposeMessage.getContent(), Ability.class);
    }

    private Ability fromTaskInfo(TaskInfo taskInfo) {
        Ability desired = new Ability();
        desired.setInputType(taskInfo.getInputType());
        desired.setOutputType(taskInfo.getOutputType());
        desired.setTime(taskInfo.getTimeLimit().dividedBy(taskInfo.getOutputSize()));
        return desired;
    }

    private void logRegularChains(List<List<Ability>> lists) {
        lists.forEach(list -> log.log(Level.INFO, "Found regular chain: [{0}]", chainToString(list)));
    }

    private void logFastestChains(List<List<Ability>> lists) {
        lists.forEach(list -> log.log(Level.INFO, "Found fastest chain: [{0}]", chainToString(list)));
    }

    private String chainToString(List<Ability> abilities) {
        if (abilities.isEmpty()) {
            return "%EMPTY%";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Ability ability : abilities) {
            stringBuilder.append(ability.getInputType());
            stringBuilder.append("--(");
            stringBuilder.append(ability.getTime().toString());
            stringBuilder.append(")-->");
        }
        stringBuilder.append(abilities.get(abilities.size() - 1).getOutputType());
        return stringBuilder.toString();
    }
}
