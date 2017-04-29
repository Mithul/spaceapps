Rails.application.routes.draw do
  resources :beaches

  root 'home#index'
  get '/search/beaches' => 'beaches#search', as: :search_beaches
  devise_for :users, :controllers => {:registrations => "registrations"}
  # For details on the DSL available within this file, see http://guides.rubyonrails.org/routing.html
end
