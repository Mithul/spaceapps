class Team < ApplicationRecord
	has_many :beaches
	has_many :users	
end
