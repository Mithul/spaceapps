class BeachesController < ApplicationController
  before_action :set_beach, only: [:show, :edit, :update, :destroy]

  # GET /beaches
  # GET /beaches.json
  def index
    @beaches = Beach.all
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
