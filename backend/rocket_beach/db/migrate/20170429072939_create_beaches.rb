class CreateBeaches < ActiveRecord::Migration[5.0]
  def change
    create_table :beaches do |t|
      t.string :name
      t.decimal :latitude
      t.decimal :longitude
      t.text :address

      t.timestamps
    end
  end
end
