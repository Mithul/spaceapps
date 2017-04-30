package com.example.billy.rocketbeach;

class Beach {
    public Integer id;
    public String name;
    public String latitude;
    public String longitude;
    public String health;
    public Team team;
    public UVResponse uv_index;
    public String potential_xp;

    void validate_team(){
        if (team==null){
            team = new Team();
            team.name = "Neutral";
        }
    }
}
