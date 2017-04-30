require 'securerandom'

class User < ApplicationRecord
  # Include default devise modules. Others available are:
  # :confirmable, :lockable, :timeoutable and :omniauthable
  devise :database_authenticatable, :registerable,
         :recoverable, :rememberable, :trackable, :validatable
  before_create :set_auth_token

  def ensure_authentication_token!
    self.auth_token = set_auth_token if !auth_token.present?
  end

  def send_push_notification msg
    token = self.device_token
    return if !token
    device = Pushbots::Device.new(token, :android)
    push = Pushbots::One.new(:android, token, msg, nil, {})
    push.send
  end

  private
  def set_auth_token
    return if auth_token.present?
    self.auth_token = generate_auth_token
  end

  def generate_auth_token
    SecureRandom.uuid.gsub(/\-/,'')
  end

  belongs_to :team, optional: true
end
