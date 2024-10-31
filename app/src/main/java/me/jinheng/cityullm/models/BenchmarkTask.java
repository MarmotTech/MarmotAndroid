package me.jinheng.cityullm.models;

public class BenchmarkTask {
    public String taskName;
    public String taskValue;
    public Boolean isEnable;
    public BenchmarkTask(String _taskName, String _taskValue){
        taskName = _taskName;
        taskValue = _taskValue;
        isEnable = false;
    }
    public Boolean getEnable() {
        return isEnable;
    }

    public void setEnable(Boolean enable) {
        isEnable = enable;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskValue() {
        return taskValue;
    }

    public void setTaskValue(String taskValue) {
        this.taskValue = taskValue;
    }
}
