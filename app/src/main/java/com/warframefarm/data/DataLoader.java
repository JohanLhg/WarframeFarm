package com.warframefarm.data;

import android.app.Application;

import com.warframefarm.AppExecutors;
import com.warframefarm.CommunicationHandler;
import com.warframefarm.database.WarframeFarmDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class DataLoader {

    protected final Executor backgroundThread, mainThread;

    protected final WarframeFarmDatabase database;

    private CommunicationHandler communicationHandler;
    private Integer currentAction;
    private final List<Integer> taskQueue = new ArrayList<>();

    public DataLoader(Application application) {
        database = WarframeFarmDatabase.getInstance(application);

        AppExecutors executors = new AppExecutors();
        backgroundThread = executors.getBackgroundThread();
        mainThread = executors.getMainThread();
    }

    public void setCommunicationHandler(CommunicationHandler communicationHandler) {
        this.communicationHandler = communicationHandler;
    }

    protected void setCurrentAction(Integer currentAction) {
        this.currentAction = currentAction;
    }

    protected void addTasksToQueue(int... tasks) {
        for (int task : tasks) {
            taskQueue.add(task);
        }
    }

    protected void finishTask(Integer taskID) {
        taskQueue.remove(taskID);
        if (taskQueue.isEmpty())
            finishAction(currentAction);
    }

    protected void finishAction(Integer actionID) {
        taskQueue.clear();
        if (communicationHandler != null)
            mainThread.execute(() -> communicationHandler.finishAction(actionID));
    }
}
