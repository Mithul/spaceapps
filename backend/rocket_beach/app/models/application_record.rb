class ApplicationRecord < ActiveRecord::Base
  self.abstract_class = true

  
  def to_json(options={})
  	options[:except] ||= [:created_at, :updated_at]
  	super(options)
  end
end
