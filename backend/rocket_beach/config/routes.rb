Rails.application.routes.draw do

  get 'notifications/create'

  get '/health/status' => 'user#get_health', as: :health
  get '/health/update' => 'user#update_health', as: :update_health
  get '/health/reset' => 'user#reset_health'

  resources :teams
  resources :beaches

  root 'home#index'
  get '/search/beaches' => 'beaches#search', as: :search_beaches
  get '/attack/:id' => 'beaches#attack', as: :attack
  get '/attack' => 'beaches#attack', as: :attack_closest
  get '/get_status' => 'beaches#status', as: :get_beach_status
  
  get '/me' => 'teams#get_details', as: :get_user_details
  get '/get_association' => 'teams#get_association', as: :get_association
  get '/associate/:id' => 'teams#associate', as: :associate

  get '/notifications/create' => 'notifications#create'

  post '/register_device' => 'user#register_device'  

  devise_for :users, :controllers => {:registrations => "registrations", sessions: "sessions"}
  # For details on the DSL available within this file, see http://guides.rubyonrails.org/routing.html
end
