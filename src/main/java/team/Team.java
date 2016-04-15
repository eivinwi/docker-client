package team;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Team implements Comparable {
    public static Instant startTime;

    private String name;
    private boolean tasks[];

    private boolean hasFinished;
    private String timeSpent;
    private String finishingTime;
    private String hostIp;

    public Team(String name, Integer numTasks) {
        this.name = name;
        hasFinished = false;
        timeSpent = null;
        finishingTime = null;
        tasks = new boolean[numTasks];
    }

    public static String getStartTime() {
        return startTime.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHasFinished() {
        return hasFinished;
    }

    public void setHasFinished(boolean hasFinished) {
        if(!this.hasFinished && hasFinished) {
            this.hasFinished = true;
            this.finishingTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME);// Instant.now(); //System.currentTimeMillis() - startTime;
            long seconds = Duration.between(startTime, Instant.now()).getSeconds();
            timeSpent = String.format(
                    "%02d:%02d:%02d",
                    seconds / 3600,
                    (seconds % 3600)/ 60,
                    seconds % 60
            );
        }
    }

    public void completeTask(int task) {
        if (task < tasks.length) {
            tasks[task] = true;
        }
    }

    public boolean[] getTasks() { return tasks; }

    public String getFinishingTime() { return finishingTime; }

    public String getTimeSpent() { return timeSpent; }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Team && this.name.equals(((Team) o).getName()));
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Team) {
            Team t = (Team) o;
            if(this.finishingTime == null) {
                return (t.getFinishingTime() == null)? 0 : -1;
            } else if(t.getFinishingTime() == null){
                return 1;
            }
            return this.finishingTime.compareTo(t.getFinishingTime());
        }
        return -1;
    }
}
