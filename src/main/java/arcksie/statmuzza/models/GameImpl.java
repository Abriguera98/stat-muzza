package arcksie.statmuzza.models;

import arcksie.statmuzza.models.Game.Status;
import java.time.LocalDateTime;

public class GameImpl {
    private String homeTeam;
    private int homeGoals;
    private int awayGoals;
    private String awayTeam;
    private Game.Status status;
    private LocalDateTime dateTime;

    public GameImpl(String homeTeam, int homeGoals, int awayGoals, String awayTeam, Game.Status status, LocalDateTime dateTime) {
        this.homeTeam = homeTeam;
        this.homeGoals = homeGoals;
        this.awayGoals = awayGoals;
        this.awayTeam = awayTeam;
        this.status = status;
        this.dateTime = dateTime;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public int getHomeGoals() {
        return homeGoals;
    }

    public void setHomeGoals(int homeGoals) {
        this.homeGoals = homeGoals;
    }

    public int getAwayGoals() {
        return awayGoals;
    }

    public void setAwayGoals(int awayGoals) {
        this.awayGoals = awayGoals;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
