package com.suicune.pokeutils.ui.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.CursorToStringConverter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.suicune.pokeutils.app.Natures;
import com.suicune.pokeutils.app.Pokemon;
import com.suicune.pokeutils.R;
import com.suicune.pokeutils.app.TeamPokemon;
import com.suicune.pokeutils.ui.activities.EditTeamPokemonActivity;
import com.suicune.pokeutils.database.PokeContract;

public class EditTeamPokemonFragment extends Fragment implements
		LoaderCallbacks<Cursor>, TextWatcher, CursorToStringConverter,
		OnItemSelectedListener, OnItemClickListener {
	private static final String ARG_POKEMON = "pokemon";
	private static final int LOADER_NAME_AUTO_COMPLETE = 1;
	private static final int LOADER_POKEMON = 2;
	private static final int LOADER_ATTACKS = 3;

	public static final String ARG_POKEMON_ID = "pokemonId";

	private TeamPokemon mPokemon;

	private AutoCompleteTextView mNameView;
	private EditText mNicknameView;
	private EditText mLevelView;
	private Spinner mAbilityView;
	private Spinner mNatureView;
	private Spinner mItemView;
	private TextView mBaseHpView;
	private TextView mBaseAttView;
	private TextView mBaseDefView;
	private TextView mBaseSpAttView;
	private TextView mBaseSpDefView;
	private TextView mBaseSpeedView;
	private EditText mIvHpView;
	private EditText mIvAttView;
	private EditText mIvDefView;
	private EditText mIvSpAttView;
	private EditText mIvSpDefView;
	private EditText mIvSpeedView;
	private EditText mEvHpView;
	private EditText mEvAttView;
	private EditText mEvDefView;
	private EditText mEvSpAttView;
	private EditText mEvSpDefView;
	private EditText mEvSpeedView;
	private TextView mStatHpView;
	private TextView mStatAttView;
	private TextView mStatDefView;
	private TextView mStatSpAttView;
	private TextView mStatSpDefView;
	private TextView mStatSpeedView;
	private Spinner mAttack1View;
	private Spinner mAttack2View;
	private Spinner mAttack3View;
	private Spinner mAttack4View;

	private SimpleCursorAdapter mPokemonNameAdapter;
	private AttacksAdapter mAttacksAdapter;

	private String mPokemonName;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		return inflater.inflate(R.layout.edit_team_pokemon_fragment, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setViews();
		if (savedInstanceState == null) {
			if (getArguments() != null) {
				if (getArguments().containsKey(
						EditTeamPokemonActivity.EXTRA_POKEMON)) {
//					mPokemon = new TeamPokemon(getArguments().getBundle(
//							EditTeamPokemonActivity.EXTRA_POKEMON));
					loadPokemonStats();
				}
			}
		} else {
			if (savedInstanceState.containsKey(ARG_POKEMON)) {
//				mPokemon = new TeamPokemon(
//						savedInstanceState.getBundle(ARG_POKEMON));
				loadPokemonStats();
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mPokemon != null) {
			Bundle status = new Bundle();
			mPokemon.saveStatus(status);
			outState.putBundle(ARG_POKEMON, status);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_edit_team_pokemon, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			saveData();
			getActivity().finish();
			return true;
		case R.id.edit_team_pokemon_validate:
			saveData();
			getActivity().finish();
			return true;
		case R.id.edit_team_pokemon_load_pokemon:
			loadPokemonFromFile();
			return true;
		case R.id.edit_team_pokemon_save_pokemon:
			savePokemonToFile();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void saveData() {
		if (mPokemon == null) {
			return;
		}
		Intent intent = new Intent();
		Bundle pokemon = new Bundle();

		mPokemon.saveStatus(pokemon);
		intent.putExtra(EditTeamPokemonActivity.EXTRA_POKEMON, pokemon);
		getActivity().setResult(Activity.RESULT_OK, intent);
		((EditTeamPokemonActivity) getActivity()).registerPokemon(pokemon);

	}

	private void setViews() {
		if (getActivity().findViewById(R.id.edit_team_pokemon_name) == null) {
			return;
		}
		prepareNameViews();
		prepareLevelView();
		prepareNatureView();
		prepareStatsViews();
		prepareAbilityView();
		prepareItemView();
		prepareAttacksViews();
	}

	private void prepareNameViews() {
		mNicknameView = (EditText) getActivity().findViewById(
				R.id.edit_team_pokemon_nickname);
		mNameView = (AutoCompleteTextView) getActivity().findViewById(
				R.id.edit_team_pokemon_name);

		String[] from = { PokeContract.PokemonName.NAME };
		int[] to = { android.R.id.text1 };
		mPokemonNameAdapter = new SimpleCursorAdapter(getActivity(),
				android.R.layout.simple_spinner_dropdown_item, null, from, to,
				0);
		mPokemonNameAdapter.setCursorToStringConverter(this);

		mNameView.setAdapter(mPokemonNameAdapter);
		mNameView.addTextChangedListener(this);
		mNameView.setOnItemClickListener(this);
		mNicknameView.addTextChangedListener(this);
	}

	private void prepareLevelView() {
		mLevelView = (EditText) getActivity().findViewById(
				R.id.edit_team_pokemon_level);
		mLevelView.addTextChangedListener(this);
	}

	private void prepareNatureView() {
		mNatureView = (Spinner) getActivity().findViewById(
				R.id.edit_team_pokemon_nature);

		String[] natures = new String[Natures.NATURES_COUNT];

		for (int i = 0; i < Natures.NATURES_COUNT; i++) {
			natures[i] = getString(Natures.getNatureName(i));
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, android.R.id.text1,
				natures);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		mNatureView.setAdapter(adapter);
		mNatureView.setOnItemSelectedListener(this);
	}

	private void prepareStatsViews() {
		mBaseHpView = (TextView) getActivity().findViewById(
				R.id.edit_team_pokemon_base_stat_hp);
		mBaseAttView = (TextView) getActivity().findViewById(
				R.id.edit_team_pokemon_base_stat_att);
		mBaseDefView = (TextView) getActivity().findViewById(
				R.id.edit_team_pokemon_base_stat_def);
		mBaseSpAttView = (TextView) getActivity().findViewById(
				R.id.edit_team_pokemon_base_stat_spatt);
		mBaseSpDefView = (TextView) getActivity().findViewById(
				R.id.edit_team_pokemon_base_stat_spdef);
		mBaseSpeedView = (TextView) getActivity().findViewById(
				R.id.edit_team_pokemon_base_stat_speed);

		mIvHpView = (EditText) getActivity().findViewById(
				R.id.edit_team_pokemon_iv_hp);
		mIvAttView = (EditText) getActivity().findViewById(
				R.id.edit_team_pokemon_iv_att);
		mIvDefView = (EditText) getActivity().findViewById(
				R.id.edit_team_pokemon_iv_def);
		mIvSpAttView = (EditText) getActivity().findViewById(
				R.id.edit_team_pokemon_iv_spatt);
		mIvSpDefView = (EditText) getActivity().findViewById(
				R.id.edit_team_pokemon_iv_spdef);
		mIvSpeedView = (EditText) getActivity().findViewById(
				R.id.edit_team_pokemon_iv_speed);

		mEvHpView = (EditText) getActivity().findViewById(
				R.id.edit_team_pokemon_ev_hp);
		mEvAttView = (EditText) getActivity().findViewById(
				R.id.edit_team_pokemon_ev_att);
		mEvDefView = (EditText) getActivity().findViewById(
				R.id.edit_team_pokemon_ev_def);
		mEvSpAttView = (EditText) getActivity().findViewById(
				R.id.edit_team_pokemon_ev_spatt);
		mEvSpDefView = (EditText) getActivity().findViewById(
				R.id.edit_team_pokemon_ev_spdef);
		mEvSpeedView = (EditText) getActivity().findViewById(
				R.id.edit_team_pokemon_ev_speed);

		mStatHpView = (TextView) getActivity().findViewById(
				R.id.edit_team_pokemon_stat_hp);
		mStatAttView = (TextView) getActivity().findViewById(
				R.id.edit_team_pokemon_stat_att);
		mStatDefView = (TextView) getActivity().findViewById(
				R.id.edit_team_pokemon_stat_def);
		mStatSpAttView = (TextView) getActivity().findViewById(
				R.id.edit_team_pokemon_stat_spatt);
		mStatSpDefView = (TextView) getActivity().findViewById(
				R.id.edit_team_pokemon_stat_spdef);
		mStatSpeedView = (TextView) getActivity().findViewById(
				R.id.edit_team_pokemon_stat_speed);

		mIvHpView.addTextChangedListener(this);
		mIvAttView.addTextChangedListener(this);
		mIvDefView.addTextChangedListener(this);
		mIvSpAttView.addTextChangedListener(this);
		mIvSpDefView.addTextChangedListener(this);
		mIvSpeedView.addTextChangedListener(this);

		mEvHpView.addTextChangedListener(this);
		mEvAttView.addTextChangedListener(this);
		mEvDefView.addTextChangedListener(this);
		mEvSpAttView.addTextChangedListener(this);
		mEvSpDefView.addTextChangedListener(this);
		mEvSpeedView.addTextChangedListener(this);
	}

	private void prepareAbilityView() {
		mAbilityView = (Spinner) getActivity().findViewById(
				R.id.edit_team_pokemon_ability);

		mAbilityView.setOnItemSelectedListener(this);
	}

	private void prepareItemView() {
		mItemView = (Spinner) getActivity().findViewById(
				R.id.edit_team_pokemon_item);

		mItemView.setOnItemSelectedListener(this);
	}

	private void prepareAttacksViews() {
		mAttack1View = (Spinner) getActivity().findViewById(
				R.id.edit_team_pokemon_attack1);
		mAttack2View = (Spinner) getActivity().findViewById(
				R.id.edit_team_pokemon_attack2);
		mAttack3View = (Spinner) getActivity().findViewById(
				R.id.edit_team_pokemon_attack3);
		mAttack4View = (Spinner) getActivity().findViewById(
				R.id.edit_team_pokemon_attack4);

		mAttacksAdapter = new AttacksAdapter(getActivity(),
				android.R.layout.simple_spinner_item, null,
				new String[] { PokeContract.Attacks._ID },
				new int[] { android.R.id.text1 }, 0);
		mAttacksAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		mAttack1View.setAdapter(mAttacksAdapter);
		mAttack2View.setAdapter(mAttacksAdapter);
		mAttack3View.setAdapter(mAttacksAdapter);
		mAttack4View.setAdapter(mAttacksAdapter);

		mAttack1View.setOnItemSelectedListener(this);
		mAttack2View.setOnItemSelectedListener(this);
		mAttack3View.setOnItemSelectedListener(this);
		mAttack4View.setOnItemSelectedListener(this);
	}

	private void loadPokemonStats() {
		if (mNameView == null) {
			return;
		}
		mNameView.setText(mPokemon.mName);
		mNicknameView.setText(mPokemon.mNickname);

		mBaseHpView.setText("" + mPokemon.mBaseStats[TeamPokemon.STAT_INDEX_HP]);
		mBaseAttView.setText("" + mPokemon.mBaseStats[TeamPokemon.STAT_INDEX_ATT]);
		mBaseDefView.setText("" + mPokemon.mBaseStats[TeamPokemon.STAT_INDEX_DEF]);
		mBaseSpAttView.setText("" + mPokemon.mBaseStats[TeamPokemon.STAT_INDEX_SP_ATT]);
		mBaseSpDefView.setText("" + mPokemon.mBaseStats[TeamPokemon.STAT_INDEX_SP_DEF]);
		mBaseSpeedView.setText("" + mPokemon.mBaseStats[TeamPokemon.STAT_INDEX_SPEED]);

		mLevelView.setText("" + mPokemon.mLevel);

		mIvHpView.setText("" + mPokemon.mIvs[TeamPokemon.STAT_INDEX_HP]);
		mIvAttView.setText("" + mPokemon.mIvs[TeamPokemon.STAT_INDEX_ATT]);
		mIvDefView.setText("" + mPokemon.mIvs[TeamPokemon.STAT_INDEX_DEF]);
		mIvSpAttView.setText("" + mPokemon.mIvs[TeamPokemon.STAT_INDEX_SP_ATT]);
		mIvSpDefView.setText("" + mPokemon.mIvs[TeamPokemon.STAT_INDEX_SP_DEF]);
		mIvSpeedView.setText("" + mPokemon.mIvs[TeamPokemon.STAT_INDEX_SPEED]);
		mEvHpView.setText("" + mPokemon.mEvs[TeamPokemon.STAT_INDEX_HP]);
		mEvAttView.setText("" + mPokemon.mEvs[TeamPokemon.STAT_INDEX_ATT]);
		mEvDefView.setText("" + mPokemon.mEvs[TeamPokemon.STAT_INDEX_DEF]);
		mEvSpAttView.setText("" + mPokemon.mEvs[TeamPokemon.STAT_INDEX_SP_ATT]);
		mEvSpDefView.setText("" + mPokemon.mEvs[TeamPokemon.STAT_INDEX_SP_DEF]);
		mEvSpeedView.setText("" + mPokemon.mEvs[TeamPokemon.STAT_INDEX_SPEED]);

		mPokemon.showStats();

		setStats();

		loadPokemonAbilities();
	}

	private void loadPokemonAbilities() {
		String[] abilities = new String[3];
		abilities[Pokemon.ABILITY_INDEX_1] = mPokemon.mAbilities.get(Pokemon.ABILITY_INDEX_1).mName;
		abilities[Pokemon.ABILITY_INDEX_2] =
                (mPokemon.mAbilities.get(Pokemon.ABILITY_INDEX_2).mId == 0) ? "-"
				: mPokemon.mAbilities.get(Pokemon.ABILITY_INDEX_2).mName;
		abilities[Pokemon.ABILITY_INDEX_DW] =
                (mPokemon.mAbilities.get(Pokemon.ABILITY_INDEX_DW).mId == 0) ? "-"
				: mPokemon.mAbilities.get(Pokemon.ABILITY_INDEX_DW).mName;

		ArrayAdapter<String> pokemonAbilityAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item,
				android.R.id.text1, abilities);
		pokemonAbilityAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		mAbilityView.setAdapter(pokemonAbilityAdapter);
	}

	private void setStats() {
		mStatHpView.setText("" + mPokemon.mStats[TeamPokemon.STAT_INDEX_HP]);
		mStatAttView.setText("" + mPokemon.mStats[TeamPokemon.STAT_INDEX_ATT]);
		mStatDefView.setText("" + mPokemon.mStats[TeamPokemon.STAT_INDEX_DEF]);
		mStatSpAttView.setText("" + mPokemon.mStats[TeamPokemon.STAT_INDEX_SP_ATT]);
		mStatSpDefView.setText("" + mPokemon.mStats[TeamPokemon.STAT_INDEX_SP_DEF]);
		mStatSpeedView.setText("" + mPokemon.mStats[TeamPokemon.STAT_INDEX_SPEED]);
	}

	private void loadPokemonFromFile() {
		Toast.makeText(getActivity(), "THIS IS A PLACEHOLDER",
				Toast.LENGTH_LONG).show();
	}

	private void savePokemonToFile() {
		Toast.makeText(getActivity(), "THIS IS A PLACEHOLDER",
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		Bundle args = new Bundle();
		args.putLong(ARG_POKEMON_ID, id);
		getLoaderManager().restartLoader(LOADER_POKEMON, args, this);
	}

	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view,
			int position, long id) {
		switch (adapterView.getId()) {
		case R.id.edit_team_pokemon_ability:
			if (mPokemon != null) {
				if ((mPokemon.mAbilities.get(Pokemon.ABILITY_INDEX_2).mId == 0)
						&& (position == Pokemon.ABILITY_INDEX_2)) {
					mPokemon.mCurrentAbility = mPokemon.mAbilities.get(Pokemon.ABILITY_INDEX_1);
					mAbilityView.setSelection(Pokemon.ABILITY_INDEX_1);
				} else if ((mPokemon.mAbilities.get(Pokemon.ABILITY_INDEX_DW).mId == 0)
						&& (position == Pokemon.ABILITY_INDEX_DW)) {
					mPokemon.mCurrentAbility = mPokemon.mAbilities.get(Pokemon.ABILITY_INDEX_1);
					mAbilityView.setSelection(Pokemon.ABILITY_INDEX_2);
				} else {
					mPokemon.mCurrentAbility = mPokemon.mAbilities.get(position);
				}
			}
			break;
		case R.id.edit_team_pokemon_attack1:
			break;
		case R.id.edit_team_pokemon_attack2:
			break;
		case R.id.edit_team_pokemon_attack3:
			break;
		case R.id.edit_team_pokemon_attack4:
			break;
		case R.id.edit_team_pokemon_item:
			break;
		case R.id.edit_team_pokemon_nature:
			if (mPokemon != null) {
				mPokemon.mNature = position;
				mPokemon.showStats();
				setStats();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public CharSequence convertToString(Cursor c) {
		return c.getString(c
				.getColumnIndexOrThrow(PokeContract.PokemonName.NAME));
	}

	@Override
	public void afterTextChanged(Editable s) {
		if (s.equals("")) {
			return;
		}
		if (!s.equals("")) {
			if (s.hashCode() == mNameView.getText().hashCode()) {
				mPokemonName = s.toString();
				getLoaderManager().restartLoader(LOADER_NAME_AUTO_COMPLETE,
						null, this);
				return;
			} else if (s.hashCode() == mNicknameView.getText().hashCode()) {
				if (mPokemon != null) {
					mPokemon.mNickname = s.toString();
				}
				return;
			}
			try {
				if (s.hashCode() == mLevelView.getText().hashCode()) {
					if (mPokemon != null) {
						mPokemon.mLevel = Integer.parseInt(s.toString());
					}
				} else if (s.hashCode() == mIvHpView.getText().hashCode()) {
					if (mPokemon != null) {
						mPokemon.mIvs[TeamPokemon.STAT_INDEX_HP] = Integer
								.parseInt(s.toString());
					}
				} else if (s.hashCode() == mIvAttView.getText().hashCode()) {
					if (mPokemon != null) {
						mPokemon.mIvs[TeamPokemon.STAT_INDEX_ATT] = Integer
								.parseInt(s.toString());
					}
				} else if (s.hashCode() == mIvDefView.getText().hashCode()) {
					if (mPokemon != null) {
						mPokemon.mIvs[TeamPokemon.STAT_INDEX_DEF] = Integer
								.parseInt(s.toString());
					}
				} else if (s.hashCode() == mIvSpAttView.getText().hashCode()) {
					if (mPokemon != null) {
						mPokemon.mIvs[TeamPokemon.STAT_INDEX_SP_ATT] = Integer
								.parseInt(s.toString());
					}
				} else if (s.hashCode() == mIvSpDefView.getText().hashCode()) {
					if (mPokemon != null) {
						mPokemon.mIvs[TeamPokemon.STAT_INDEX_SP_DEF] = Integer
								.parseInt(s.toString());
					}
				} else if (s.hashCode() == mIvSpeedView.getText().hashCode()) {
					if (mPokemon != null) {
						mPokemon.mIvs[TeamPokemon.STAT_INDEX_SPEED] = Integer
								.parseInt(s.toString());
					}
				} else if (s.hashCode() == mEvHpView.getText().hashCode()) {
					if (mPokemon != null) {
						mPokemon.mEvs[TeamPokemon.STAT_INDEX_HP] = Integer
								.parseInt(s.toString());
					}
				} else if (s.hashCode() == mEvAttView.getText().hashCode()) {
					if (mPokemon != null) {
						mPokemon.mEvs[TeamPokemon.STAT_INDEX_ATT] = Integer
								.parseInt(s.toString());
					}
				} else if (s.hashCode() == mEvDefView.getText().hashCode()) {
					if (mPokemon != null) {
						mPokemon.mEvs[TeamPokemon.STAT_INDEX_DEF] = Integer
								.parseInt(s.toString());
					}
				} else if (s.hashCode() == mEvSpAttView.getText().hashCode()) {
					if (mPokemon != null) {
						mPokemon.mEvs[TeamPokemon.STAT_INDEX_SP_ATT] = Integer
								.parseInt(s.toString());
					}
				} else if (s.hashCode() == mEvSpDefView.getText().hashCode()) {
					if (mPokemon != null) {
						mPokemon.mEvs[TeamPokemon.STAT_INDEX_SP_DEF] = Integer
								.parseInt(s.toString());
					}
				} else if (s.hashCode() == mEvSpeedView.getText().hashCode()) {
					if (mPokemon != null) {
						mPokemon.mEvs[TeamPokemon.STAT_INDEX_SPEED] = Integer
								.parseInt(s.toString());
					}
				}
				mPokemon.showStats();
				setStats();
			} catch (Exception e) {
				return;
			}
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		switch (id) {
		case LOADER_NAME_AUTO_COMPLETE:
			String[] autoCompleteProjection = { PokeContract.PokemonName._ID,
					PokeContract.PokemonName.NAME };
			String selection = PokeContract.PokemonName.NAME + " LIKE ?";
			String[] selectionArgs = { "%" + mPokemonName + "%" };
			return new CursorLoader(getActivity(),
					PokeContract.PokemonName.CONTENT_POKEMON_NAME,
					autoCompleteProjection, selection, selectionArgs, null);
		case LOADER_POKEMON:
			String pokemonSelection = PokeContract.PokemonName.TABLE_NAME + "."
					+ PokeContract.PokemonName._ID + "=?";
			String[] pokemonSelectionArgs = { Long.toString(args
					.getLong(ARG_POKEMON_ID)) };
			return new CursorLoader(getActivity(),
					PokeContract.Pokedex.CONTENT_POKEDEX, null,
					pokemonSelection, pokemonSelectionArgs, null);
		case LOADER_ATTACKS:
			int form = 0;
			if (args != null && args.containsKey(ARG_POKEMON_ID)) {
				form = 0;
			} else {
				form = mPokemon.mForm;
			}
			String[] attacksProjection = { PokeContract.Attacks.TABLE_NAME
					+ "." + PokeContract.Attacks._ID };
			String attacksSelection = PokeContract.PokemonAttacks.NUMBER
					+ "=? AND " + PokeContract.PokemonAttacks.FORM + "=?";
			String[] attacksSelectionArgs = {
					Integer.toString(mPokemon.mNumber), Integer.toString(form) };
			return new CursorLoader(getActivity(),
					PokeContract.PokemonAttacks.CONTENT_POKEMON_ATTACKS,
					attacksProjection, attacksSelection, attacksSelectionArgs,
					null);
		default:
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		switch (loader.getId()) {
		case LOADER_NAME_AUTO_COMPLETE:
			mPokemonNameAdapter.swapCursor(cursor);
			return;
		case LOADER_POKEMON:
//			mPokemon = new TeamPokemon(cursor);
			getLoaderManager().restartLoader(LOADER_ATTACKS, null, this);
			if (TextUtils.isEmpty(mNicknameView.getText())) {
				mNicknameView.setText(mPokemon.mName);
				mPokemon.mNickname = mNicknameView.getText().toString();
			}
			loadPokemonStats();

			return;
		case LOADER_ATTACKS:
			if (cursor.getCount() == 0) {
				Bundle args = new Bundle();
				args.putBoolean(ARG_POKEMON_ID, true);
				getLoaderManager().restartLoader(LOADER_ATTACKS, args, this);
				return;
			} else {
				mAttacksAdapter.swapCursor(cursor);
			}
			return;
		default:
			return;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		switch (loader.getId()) {
		case LOADER_NAME_AUTO_COMPLETE:
			mPokemonNameAdapter.swapCursor(null);
			return;
		default:
			return;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> adapterView) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	public class AttacksAdapter extends SimpleCursorAdapter {
		public AttacksAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to, int flags) {
			super(context, layout, c, from, to, flags);
		}

		@Override
		public void setViewText(TextView v, String text) {
			v.setText(getResources().getStringArray(R.array.moves)[Integer
					.parseInt(text)]);
		}
	}

	public interface EditTeamPokemonCallback {
		void registerPokemon(Bundle pokemon);
	}
}
