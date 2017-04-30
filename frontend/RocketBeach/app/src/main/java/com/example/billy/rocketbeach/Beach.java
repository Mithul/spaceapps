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

    public String[] flatten() {
        return new String[] {
                id + "",
                name,
                latitude,
                longitude,
        };
    }

    public static Beach unflatten(String[] values) {
        Beach b = new Beach();
        b.id = Integer.parseInt(values[0]);
        b.name = values[1];
        b.latitude = values[2];
        b.longitude = values[3];

        return b;
    }
}
