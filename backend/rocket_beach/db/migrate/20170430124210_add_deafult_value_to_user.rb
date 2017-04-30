class AddDeafultValueToUser < ActiveRecord::Migration[5.0]
  def change
  	change_column_default :users, :xp, 100
  end
end
