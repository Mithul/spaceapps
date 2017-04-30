class AddResetTimeToUser < ActiveRecord::Migration[5.0]
  def change
    add_column :users, :last_death, :datetime
  end
end
