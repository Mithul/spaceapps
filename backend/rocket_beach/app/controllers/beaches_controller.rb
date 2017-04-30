class BeachesController < ApplicationController
  before_action :set_beach, only: [:show, :edit, :update, :destroy]
  before_action :authenticate

  # before_filter :check_lat_long, only[:attack, :search]

  # GET /beaches
  # GET /beaches.json
  def index
    @beaches = Beach.all
  end

  def attack
    if params[:id]
      beach = Beach.where(id: params[:id]).first
      if !beach
        render json: {message: "Beach not found", status: false}
        return
      end
    end
    point = [params[:lat].to_f, params[:long].to_f]
    beach = Beach.all.sort_by{|b| b.distance(point)}.first if !params[:id]
    if beach.distance(point) < 1.0
      beach.health = 0.0 if beach.health.nil?
      beach.team = current_user.team if !beach.team_id
      if beach.team == current_user.team
        beach.health = beach.health + 1
      else
        beach.health = beach.health - 1          
      end
      beach.health = 100 if beach.health > 100
      if beach.health < 0
        beach.health = 0.0 
        beach.team = nil
      end
      beach.save
      Rails.logger.info(beach.errors.inspect) 
      render json: {beach: JSON.parse(beach.to_json), team: JSON.parse(beach.team.to_json), status: true}
    else
      render json: {message: "Cannot attack beach, Go nearer", status: false}
      return
    end
  end

  def search
    @beaches = []
    init = true
    #Try catch to make more robus
    begin
      #If lat/long given
      if params[:lat] or params[:long]
        Rails.logger.debug("LAT/LONG")
        init = false
        point = [params[:lat].to_f, params[:long].to_f]
        #Sort by distance for beaches < 10km away
        @beaches = Beach.all.select{|b| b.distance(point) < 1000}
      end
      if params[:address]
        init = false
        Rails.logger.debug("ADDRESS")
        Rails.logger.debug(@beaches.to_json)
        @beaches = @beaches + Beach.all.select{|b| b.address.downcase.include? params[:address].downcase}
      end
    rescue => error
      init = false
      @beaches = []
      message = error.to_s
      flash[:error] = message
      respond_to do |format|
        format.html {render 'index', :status => 500  }
        format.json {render :json => {status: :error, message: message}, :status => 500 }
      end
    end
    @point = point
    if !performed?
      Rails.logger.debug("DEFAULT")
      @beaches = Beach.all if init
      @beaches = @beaches.sort_by{|b| b.distance(point)} if !point.nil?
      @beaches = Kaminari.paginate_array(@beaches).page(params[:page]).per(10)
      render 'index'
    end
  end

  # GET /beaches/1
  # GET /beaches/1.json
  def show
  end

  # GET /beaches/new
  def new
    @beach = Beach.new
  end

  # GET /beaches/1/edit
  def edit
  end

  # POST /beaches
  # POST /beaches.json
  def create
    @beach = Beach.new(beach_params)

    respond_to do |format|
      if @beach.save
        format.html { redirect_to @beach, notice: 'Beach was successfully created.' }
        format.json { render :show, status: :created, location: @beach }
      else
        format.html { render :new }
        format.json { render json: @beach.errors, status: :unprocessable_entity }
      end
    end
  end

  # PATCH/PUT /beaches/1
  # PATCH/PUT /beaches/1.json
  def update
    respond_to do |format|
      if @beach.update(beach_params)
        format.html { redirect_to @beach, notice: 'Beach was successfully updated.' }
        format.json { render :show, status: :ok, location: @beach }
      else
        format.html { render :edit }
        format.json { render json: @beach.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /beaches/1
  # DELETE /beaches/1.json
  def destroy
    @beach.destroy
    respond_to do |format|
      format.html { redirect_to beaches_url, notice: 'Beach was successfully destroyed.' }
      format.json { head :no_content }
    end
  end

  protected

  def authenticate
    authenticate_token || render_unauthorized
  end

  def authenticate_token
    return current_user
  end

  def render_unauthorized
    self.headers['WWW-Authenticate'] = 'Token realm="Application"'
    render json: 'Bad credentials', status: 401
  end 
	
  private

    def check_lat_long
      if !(params[:lat] and params[:long])
          message = "Please specify both lat and long"
          flash[:error] = message
          respond_to do |format|
            format.html {render 'index', :status => 422  }
            format.json {render :json => {status: :error, message: message}, :status => 422 }
          end
      end
    end
    # Use callbacks to share common setup or constraints between actions.
    def set_beach
      @beach = Beach.find(params[:id])
    end

    # Never trust parameters from the scary internet, only allow the white list through.
    def beach_params
      params.require(:beach).permit(:name, :latitude, :longitude, :address)
    end
end
