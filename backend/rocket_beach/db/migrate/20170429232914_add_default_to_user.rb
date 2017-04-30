class AddDefaultToUser < ActiveRecord::Migration[5.0]
  def change
    change_column_default :users, :health,100.00
  end
end
