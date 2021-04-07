package com.github.leuvaarden.fipasample.asking.agent;

import com.github.leuvaarden.fipasample.common.data.Ability;
import com.github.leuvaarden.fipasample.common.data.TaskInfo;
import com.github.leuvaarden.fipasample.common.data.TaskStatus;
import jade.util.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;

public class AskingAgent extends AbstractAskingAgent {
    private final Logger log = Logger.getMyLogger(this.getClass().getName());

    // TODO HashMaps to Queues
    // TODO UUID lock
    private final ConcurrentMap<UUID, TaskInfo> taskInfos = new ConcurrentHashMap<>();
    private final ConcurrentMap<UUID, TaskStatus> taskStatuses = new ConcurrentHashMap<>();
    private final ConcurrentMap<UUID, List<Ability>> taskAbilities = new ConcurrentHashMap<>();
    private final ConcurrentMap<UUID, Object> taskInputs = new ConcurrentHashMap<>();
    private final ConcurrentMap<UUID, Object> taskOutputs = new ConcurrentHashMap<>();

    public AskingAgent() {
        super(Duration.ofMinutes(1), Duration.ofSeconds(15), Duration.ofSeconds(5));
    }

    @Override
    protected void setup() {
        super.setup();
        log.log(Level.INFO, getAID().getName() + " is ready");
    }

    @Override
    public TaskInfo getTaskInfo(UUID uuid) {
        return taskInfos.get(uuid);
    }

    @Override
    public TaskStatus getTaskStatus(UUID uuid) {
        return taskStatuses.get(uuid);
    }

    @Override
    public List<Ability> getTaskAbilities(UUID uuid) {
        return taskAbilities.get(uuid);
    }

    @Override
    public Object getTaskInputs(UUID uuid) {
        return taskInputs.get(uuid);
    }

    @Override
    public Object getTaskOutputs(UUID uuid) {
        return taskOutputs.get(uuid);
    }

    @Override
    public void createTask(UUID uuid, TaskInfo taskInfo, Object inputs) {
        taskInfos.put(uuid, taskInfo);
        taskAbilities.put(uuid, new ArrayList<>());
        changeTaskStatus(uuid, TaskStatus.CREATED);
        taskInputs.put(uuid, inputs);
    }

    @Override
    public void changeTaskStatus(UUID uuid, TaskStatus taskStatus) {
        taskStatuses.put(uuid, taskStatus);
    }

    @Override
    public void saveTaskAbility(UUID uuid, Ability ability) {
        taskAbilities.get(uuid).add(ability);
    }

    @Override
    public void saveTaskOutputs(UUID uuid, Object outputs) {
        taskOutputs.put(uuid, outputs);
    }

    @Override
    public Collection<Map.Entry<UUID, TaskStatus>> getTaskStatuses() {
        return taskStatuses.entrySet();
    }
}
