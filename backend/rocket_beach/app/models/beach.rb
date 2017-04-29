class Beach < ApplicationRecord
	def distance point=nil
		point = [0,0] if point.nil?
		lat = point[0]
		lon = point[1]
		getDistanceFromLatLonInKm(self.latitude, self.longitude, lat, lon)
	end

	def uv_index
		# Rails.logger.debug(cache_key)
		# Rails.cache.delete("#{cache_key}/")
		json_uv = nil
		json_weather = nil
		uv = Rails.cache.fetch("#{cache_key}/", expires_in: 2.hours) do
			appids = Figaro.env.openweather_appids.split(",").map{|a| a.strip}
			Rails.logger.debug(appids)
			date = DateTime.now.in_time_zone("UTC").change({hour: 12, minute: 0, second: 0}).strftime('%Y-%m-%dT%H:%M:%SZ')
			require 'net/http'
			require 'uri'
			t = []
			
			t << Thread.new {
				appids.each do |appid|
					url = "http://api.openweathermap.org/v3/uvi/#{self.latitude.round(1)},#{self.longitude.round(1)}/current.json?appid=#{appid}"
					json_uv = JSON.parse(Net::HTTP.get(URI.parse(url))) 
					break if !(json_uv.include? "cod" and json_uv["cod"] == 429)
				end
			}
			# json[:message] = "found" if !json.include? "message"

			# http://api.openweathermap.org/data/2.5/weather?lat=35&lon=139
			t << Thread.new {
				appids.each do |appid|
					url = "http://api.openweathermap.org/data/2.5/weather?lat=#{self.latitude}&lon=#{self.longitude}&appid=#{appid}"
					json_weather = JSON.parse(Net::HTTP.get(URI.parse(url)))
					break if !(json_weather.include? "cod" and json_weather["cod"] == 429)
				end
			}

			t.each{|thread| thread.join}
Rails.logger.debug([json_uv, json_weather])

			Rails.logger.debug([json_uv, json_weather])
			if json_uv["data"] and json_weather["clouds"]
				val = json_uv["data"].to_f * (0.9889746039) ** (json_weather["clouds"]["all"].to_f/100)
				uv = {status: true, value: val, raw: json_uv["data"].to_f, weather: json_weather}
			else
				uv = {status: false}
			end
			uv
			# return uv
   		end
		if json_uv and json_uv.include? "cod" and json_uv["cod"] == 429
			Rails.cache.delete("#{cache_key}/")
		end
		return uv
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
