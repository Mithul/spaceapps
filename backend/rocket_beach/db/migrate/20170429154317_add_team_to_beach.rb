class AddTeamToBeach < ActiveRecord::Migration[5.0]
  def change
    add_reference :beaches, :team, foreign_key: true
  end
end
