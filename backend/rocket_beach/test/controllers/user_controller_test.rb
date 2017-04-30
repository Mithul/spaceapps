require 'test_helper'

class UserControllerTest < ActionDispatch::IntegrationTest
  test "should get health" do
    get user_health_url
    assert_response :success
  end

end
