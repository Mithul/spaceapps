class BeachesController < ApplicationController
  before_action :set_beach, only: [:show, :edit, :update, :destroy]

  # GET /beaches
  # GET /beaches.json
  def index
    @beaches = Beach.all
  end

  def search
    @beaches = []
    init = true
    #Try catch to make more robus
    begin
      #If lat/long given
      if params[:lat] or params[:long]
        #If only one is given
        Rails.logger.debug("LAT/LONG")
        if !(params[:lat] and params[:long])
          init = false
          @beaches = []
          message = "Please specify both lat and long"
          flash[:error] = message
          respond_to do |format|
            format.html {render 'index', :status => 422  }
            format.json {render :json => {status: :error, message: message}, :status => 422 }
          end
        #If both are given
        else
          init = false
          point = [params[:lat].to_f, params[:long].to_f]
          #Sort by distance for beaches < 10km away
          @beaches = Beach.all.select{|b| b.distance(point) < 1000}
        end
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

  private
    # Use callbacks to share common setup or constraints between actions.
    def set_beach
      @beach = Beach.find(params[:id])
    end

    # Never trust parameters from the scary internet, only allow the white list through.
    def beach_params
      params.require(:beach).permit(:name, :latitude, :longitude, :address)
    end
end
