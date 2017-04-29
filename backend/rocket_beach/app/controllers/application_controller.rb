class ApplicationController < ActionController::Base
  # protect_from_forgery with: :exception
  protect_from_forgery with: :null_session
  before_action :sign_in_user

  private

  def sign_in_user
  	session.destroy
  	Rails.logger.debug(current_user.to_json)
  	user = nil
  	authenticate_with_http_token do |token, options|
  		user = User.find_by(auth_token: token)
  		break if user
    end
  	user = User.find_by(auth_token: request.headers["X-AUTH-TOKEN"]) if !user and request.headers["X-AUTH-TOKEN"]
  	sign_in(user, scope: :user) if user
  end
end
