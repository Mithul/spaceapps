json.extract! beach, :id, :name, :latitude, :longitude, :address, :uv_index, :health, :potential_xp
json.url beach_url(beach, format: :json)
json.distance beach.distance(@point)
json.team