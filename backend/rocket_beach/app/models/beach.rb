class Beach < ApplicationRecord
	def distance point=nil
		point = [0,0] if point.nil?
		lat = point[0]
		lon = point[1]
		getDistanceFromLatLonInKm(self.latitude, self.longitude, lat, lon)
	end

	def uv_index
		appid = Figaro.env.openweather_appid
		date = DateTime.now.in_time_zone("UTC").change({hour: 12, minute: 0, second: 0}).strftime('%Y-%m-%dT%H:%M:%SZ')
		# http://api.openweathermap.org/v3/uvi/40.7,-74.2/2017-04-29T12:00:00Z.json?appid=b8bf2ecc18e7c20dc377279489063ac5
		# http://api.openweathermap.org/v3/uvi/2.456,3.546/2017-04-29T12:00:00Z.json?appid=b8bf2ecc18e7c20dc377279489063ac5
		# http://api.openweathermap.org/v3/uvi/1.345,6.543/2017-04-29T14:18:54+05:30.json?appid=
		# 2016-01-02T15:04:05Z
		# http://api.openweathermap.org/v3/uvi/{location}/{datetime}.json?appid={api_key}
		url = "http://api.openweathermap.org/v3/uvi/#{self.latitude.round(1)},#{self.longitude.round(1)}/#{date}.json?appid=#{appid}"
		require 'net/http'
		require 'uri'
		response = nil
		json = JSON.parse(Net::HTTP.get(URI.parse(url)))
		json[:message] = "found" if !json.include? "message"
		return json
	end

	private

	def getDistanceFromLatLonInKm(lat1,lon1,lat2,lon2)
	    r = 6371;
	    dLat = deg2rad(lat2-lat1)
	    dLat.abs
	    dLon = deg2rad(lon2-lon1)
	    dLon.abs
	    a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	    Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * 
	    Math.sin(dLon/2) * Math.sin(dLon/2)
	    c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a))
	    d = r * c
	    
		return d
	end

	def deg2rad(deg) 
		return deg * (Math::PI/180)
	end
end
