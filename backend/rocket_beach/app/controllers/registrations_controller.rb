class RegistrationsController < Devise::RegistrationsController
  def new
    super
  end

  def create
    build_resource(sign_up_params)
    yield resource if block_given?

    if resource.save
    	response.headers['Authorization'] = resource.auth_token
	render :json => resource.as_json(:auth_token => resource.auth_token, :email=> resource.email), :status => 201
    else
	warden.custom_failure!
	render :json => resource.errors, :status => 442
    end

    
  end

  def update
    super
  end
end 
