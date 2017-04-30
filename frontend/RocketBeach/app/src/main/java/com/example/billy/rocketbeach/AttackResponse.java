package com.example.billy.rocketbeach;

/**
 * Created by mithul on 30/4/17.
 */

public class AttackResponse {
    Team team;
    Beach beach;
    boolean status;
    String message;

    void validate_team(){
        if (team==null){
            team = new Team();
            team.name = "Neutral";
        }
    }
}
