class AddXpToUser < ActiveRecord::Migration[5.0]
  def change
    add_column :users, :xp, :integer
  end
end
