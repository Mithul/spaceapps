class TeamsController < ApplicationController
  before_action :set_team, only: [:show, :edit, :update, :destroy]

  # GET /teams
  # GET /teams.json
  def index
    @teams = Team.all
  end

  def associate
    team = Team.where(id: params[:id]).first
    if team
      current_user.team = team
      current_user.save
      render json: {team: team, status: true}
    else
      render json: {message: "Team not found", status: false}
    end
  end

  def get_association
    render json: current_user.team
  end

  def get_details
    life = false
    if(!current_user.last_death.nil?)
      if (current_user.last_death - DateTime.current) > 3
        life = true 
        current_user.health = 100
        current_user.save
      end
    else
      life = true
    end
    render json: {user: current_user.attributes.except("created_at","updated_at","encrypted_password", "current_sign_in_at","last_sign_in_at","current_sign_in_ip","last_sign_in_ip","sign_in_count","remember_created_at","reset_password_token","reset_password_sent_at"), team: current_user.team, user_alive: life}
  end

  # GET /teams/1
  # GET /teams/1.json
  def show
  end

  # GET /teams/new
  def new
    @team = Team.new
  end

  # GET /teams/1/edit
  def edit
  end

  # POST /teams
  # POST /teams.json
  def create
    @team = Team.new(team_params)

    respond_to do |format|
      if @team.save
        format.html { redirect_to @team, notice: 'Team was successfully created.' }
        format.json { render :show, status: :created, location: @team }
      else
        format.html { render :new }
        format.json { render json: @team.errors, status: :unprocessable_entity }
      end
    end
  end

  # PATCH/PUT /teams/1
  # PATCH/PUT /teams/1.json
  def update
    respond_to do |format|
      if @team.update(team_params)
        format.html { redirect_to @team, notice: 'Team was successfully updated.' }
        format.json { render :show, status: :ok, location: @team }
      else
        format.html { render :edit }
        format.json { render json: @team.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /teams/1
  # DELETE /teams/1.json
  def destroy
    @team.destroy
    respond_to do |format|
      format.html { redirect_to teams_url, notice: 'Team was successfully destroyed.' }
      format.json { head :no_content }
    end
  end

  private
    # Use callbacks to share common setup or constraints between actions.
    def set_team
      @team = Team.find(params[:id])
    end

    # Never trust parameters from the scary internet, only allow the white list through.
    def team_params
      params.require(:team).permit(:name)
    end
end
