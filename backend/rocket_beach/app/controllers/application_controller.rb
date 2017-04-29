class ApplicationController < ActionController::Base
  protect_from_forgery with: :exception
  before_action :sign_in_user

  private

  def sign_in_user
  	authenticate_with_http_token do |token, options|
  		sign_in(User.find_by(auth_token: token), scope: :user)
    end
  end
end
