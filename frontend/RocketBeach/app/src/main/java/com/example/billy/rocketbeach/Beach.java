package com.example.billy.rocketbeach;

class Beach {
    public Integer id;
    public String name;
    public String latitude;
    public String longitude;
    public String health;
    public Team team;
    public UVResponse uv_index;
    public double potential_xp;

    public double get_uv_value(){
        if(uv_index!=null)
            return Math.round(uv_index.value);
        else
            return 0;
    }

    public double get_health(){
        if(health!=null)
            return Math.round(Float.parseFloat(health));
        else
            return 0;
    }

    void validate_team(){
        if (team==null){
            team = new Team();
            team.name = "Neutral";
        }
    }

    public String[] flatten() {
        validate_team();
        return new String[] {
                id + "",
                name,
                latitude,
                longitude,
                health,
                team.name,
                uv_index.value + "",
                String.valueOf(potential_xp)
        };
    }

    public static Beach unflatten(String[] values) {
        Beach b = new Beach();
        b.id = Integer.parseInt(values[0]);
        b.name = values[1];
        b.latitude = values[2];
        b.longitude = values[3];
        b.health = values[4];
        b.validate_team();
        b.team.name = values[5];
        b.uv_index = new UVResponse();
        b.uv_index.value = Double.parseDouble(values[6]);
        b.potential_xp = Double.parseDouble(values[7]);

        return b;
    }
}
