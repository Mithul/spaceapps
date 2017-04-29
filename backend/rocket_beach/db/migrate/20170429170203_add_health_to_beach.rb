class AddHealthToBeach < ActiveRecord::Migration[5.0]
  def change
    add_column :beaches, :health, :decimal
  end
end
