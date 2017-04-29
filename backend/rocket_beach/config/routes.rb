Rails.application.routes.draw do

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

  devise_for :users, :controllers => {:registrations => "registrations"}
  # For details on the DSL available within this file, see http://guides.rubyonrails.org/routing.html
end
