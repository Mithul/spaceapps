class CreateHabs < ActiveRecord::Migration[5.0]
  def change
    create_table :habs do |t|
      t.string :name
      t.decimal :latitude
      t.decimal :longitude
      t.decimal :depth

      t.timestamps
    end
  end
end
