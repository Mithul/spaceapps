Rails.application.routes.draw do
  devise_for :users
  resources :beaches

  root 'home#index'
  get '/search/beaches' => 'beaches#search', as: :search_beaches
  # For details on the DSL available within this file, see http://guides.rubyonrails.org/routing.html
end
