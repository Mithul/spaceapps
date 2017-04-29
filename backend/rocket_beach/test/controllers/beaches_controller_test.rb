require 'test_helper'

class BeachesControllerTest < ActionDispatch::IntegrationTest
  setup do
    @beach = beaches(:one)
  end

  test "should get index" do
    get beaches_url
    assert_response :success
  end

  test "should get new" do
    get new_beach_url
    assert_response :success
  end

  test "should create beach" do
    assert_difference('Beach.count') do
      post beaches_url, params: { beach: { address: @beach.address, latitude: @beach.latitude, longitude: @beach.longitude, name: @beach.name } }
    end

    assert_redirected_to beach_url(Beach.last)
  end

  test "should show beach" do
    get beach_url(@beach)
    assert_response :success
  end

  test "should get edit" do
    get edit_beach_url(@beach)
    assert_response :success
  end

  test "should update beach" do
    patch beach_url(@beach), params: { beach: { address: @beach.address, latitude: @beach.latitude, longitude: @beach.longitude, name: @beach.name } }
    assert_redirected_to beach_url(@beach)
  end

  test "should destroy beach" do
    assert_difference('Beach.count', -1) do
      delete beach_url(@beach)
    end

    assert_redirected_to beaches_url
  end
end
