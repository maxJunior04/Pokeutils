class PokemonsController < ApplicationController
	before_action :set_pokemon, only: [:show, :edit, :update, :destroy]

	# GET /pokemons
	# GET /pokemons.json
	def index
		@pokemons = Pokemon.all
	end

	# GET /pokemons/1
	# GET /pokemons/1.json
	def show
	end

	# GET /pokemons/new
	def new
		@pokemon = Pokemon.new
	end

	# GET /pokemons/1/edit
	def edit
	end

	# POST /pokemons
	# POST /pokemons.json
	def create
		@pokemon = Pokemon.new(pokemon_params)

		respond_to do |format|
			if @pokemon.save
			format.html { redirect_to @pokemon, notice: 'Pokemon was successfully created.' }
			format.json { render :show, status: :created, location: @pokemon }
			else
			format.html { render :new }
			format.json { render json: @pokemon.errors, status: :unprocessable_entity }
			end
		end
	end

	# PATCH/PUT /pokemons/1
	# PATCH/PUT /pokemons/1.json
	def update
		respond_to do |format|
			if @pokemon.update(pokemon_params)
			format.html { redirect_to @pokemon, notice: 'Pokemon was successfully updated.' }
			format.json { render :show, status: :ok, location: @pokemon }
			else
			format.html { render :edit }
			format.json { render json: @pokemon.errors, status: :unprocessable_entity }
			end
		end
	end

	# DELETE /pokemons/1
	# DELETE /pokemons/1.json
	def destroy
		@pokemon.destroy
		respond_to do |format|
			format.html { redirect_to pokemons_url, notice: 'Pokemon was successfully destroyed.' }
			format.json { head :no_content }
		end
	end

	private
		# Use callbacks to share common setup or constraints between actions.
		def set_pokemon
			form = (params[:form]) ? params[:form] : 0
			@pokemon = Pokemon.where(dex_number: params[:id], form: form).first
			@all_forms = Pokemon.where(dex_number: params[:id])
		end

		# Never trust parameters from the scary internet, only allow the white list through.
		def pokemon_params
			params.require(:pokemon).permit(:dex_number, :form, :egg_group_1, :egg_group_2, :evolution, :height, :pokemon_name_id, :weight, :ability_1, :ability_2, :ability_3, :min_level, :base_hp, :base_attack, :base_defense, :base_sp_attack, :base_sp_defense, :base_speed, :type_1, :type_2)
		end
end
