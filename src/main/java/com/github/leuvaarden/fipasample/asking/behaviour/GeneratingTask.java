package com.github.leuvaarden.fipasample.asking.behaviour;

import com.github.leuvaarden.fipasample.asking.agent.AbstractAskingAgent;
import com.github.leuvaarden.fipasample.common.data.TaskInfo;
import jade.core.behaviours.OneShotBehaviour;
import jade.util.Logger;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GeneratingTask extends OneShotBehaviour {
    private final Logger log = Logger.getMyLogger(this.getClass().getName());
    private final AbstractAskingAgent abstractAskingAgent;

    public GeneratingTask(AbstractAskingAgent abstractAskingAgent) {
        super(abstractAskingAgent);
        this.abstractAskingAgent = abstractAskingAgent;
    }

    @Override
    public void action() {
        UUID uuid = UUID.randomUUID();

        List<String> taskInput = generateSentences(RandomUtils.nextInt(50, 100));

        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setUuid(uuid);
        taskInfo.setCreated(OffsetDateTime.now());
        taskInfo.setInputSize(taskInput.size());
        taskInfo.setInputType("String");
        taskInfo.setOutputSize(taskInput.size());
        taskInfo.setOutputType("Integer");
        taskInfo.setTimeLimit(Duration.ofSeconds(taskInput.size()));

        abstractAskingAgent.createTask(uuid, taskInfo, taskInput);
        log.log(Level.INFO, "Generated task: [{0}]", taskInfo);
    }

    private List<String> generateSentences(int count) {
        return IntStream.range(0, count)
                .mapToObj(ignored -> generateSentence())
                .collect(Collectors.toList());
    }

    private String generateSentence() {
        return IntStream.range(0, RandomUtils.nextInt(5, 16))
                .map(ignored -> RandomUtils.nextInt(3, 9))
                .mapToObj(RandomStringUtils::randomAlphabetic)
                .collect(Collectors.joining(" "));
    }
}
