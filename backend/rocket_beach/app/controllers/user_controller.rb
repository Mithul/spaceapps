class UserController < ApplicationController
  before_action :authenticate

  def get_health
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
  	render json: {user_alive: life, health: current_user.health}
  end

  def register_device
    current_user.device_token = params[:device_token]
    render json: {status: current_user.save}
  end

  def update_health
  	life = true
  	if params[:id]
      beach = Beach.where(id: params[:id]).first
      if !beach
        render json: {message: "Beach not found", status: false}
        return
      end
    end

    point = [params[:lat].to_f, params[:long].to_f]
    beach = Beach.all.sort_by{|b| b.distance(point)}.first if !params[:id]
    current_user.health = 100.0 if current_user.health.nil?
    
    if beach.distance(point) < 1.0
      uv = beach.uv_index
      uv = (uv[:value]/12) * 10
      uv = 0.01 if uv==0
    else
      uv = 0.01
    end
    
    current_user.health = current_user.health - uv
    current_user.health = 0.00 if current_user.health < 0.00
    current_user.save
    if current_user.health <= 0.00
    	current_user.last_death = DateTime.now
    	life = false
    end 
    render json: {message: "Health update successfull", health:current_user.health, user_alive: life,status: true}

  end

def reset_health
	current_user.health = 100
	current_user.save
	render json: {message: "Health reset to 100", health: current_user.health, status: true}
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


end
