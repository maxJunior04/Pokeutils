package com.suicune.poketools.view.fragments.teambuilder;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.suicune.poketools.R;
import com.suicune.poketools.model.Pokemon;
import com.suicune.poketools.model.Stats;
import com.suicune.poketools.model.factories.PokemonFactory;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link com.suicune.poketools.view.fragments.teambuilder.TeamMemberFragment.OnTeamMemberChangedListener}
 * interface.
 */
public class TeamMemberFragment extends Fragment {
	private static final String ARG_POSITION = "position";
	private static final String ARG_POKEMON = "pokemon";

	private OnTeamMemberChangedListener mCallbacks;

	//BaseStats Views
	private HashMap<Stats.Stat, TextView> mBaseStatsViews;

	private Pokemon mPokemon;

	private int mPosition;

	public static TeamMemberFragment newInstance(int position) {
		TeamMemberFragment fragment = new TeamMemberFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_POSITION, position);
		fragment.setArguments(args);
		return fragment;
	}

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public TeamMemberFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallbacks = (OnTeamMemberChangedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException("Activity must implement OnTeamMemberChangedListener.");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mPosition = getArguments().getInt(ARG_POSITION);
		}
		try {
			if (savedInstanceState != null && savedInstanceState.containsKey(ARG_POKEMON)) {
				restoreValues(savedInstanceState.getBundle(ARG_POKEMON));
			}
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
	}

	@Override public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBundle(ARG_POKEMON, saveChanges());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_team_builder_member, container, false);
		prepareNameAutoComplete(view);
		prepareBaseStatsViews(view);
		if (mPokemon != null) {
			updatePokemon();
		}
		return view;
	}

	private void prepareNameAutoComplete(View view) {
		AutoCompleteTextView nameTextView = (AutoCompleteTextView) view
				.findViewById(R.id.team_builder_member_name_autocomplete);
		final String[] objects = getResources().getStringArray(R.array.pokemon_names);
		final List<String> names = Pokemon.parseAllNames(objects);
		ArrayAdapter<String> adapter =
				new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line,
						names);
		nameTextView.setAdapter(adapter);
		nameTextView.setThreshold(1);
		nameTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override public boolean onEditorAction(TextView textView, int actionId,
													KeyEvent keyEvent) {
				switch (actionId) {
					case EditorInfo.IME_ACTION_DONE:
					case EditorInfo.IME_ACTION_GO:
					case EditorInfo.IME_NULL:
						selectPokemon(textView.getText().toString());

						InputMethodManager ime = (InputMethodManager) getActivity()
								.getSystemService(Context.INPUT_METHOD_SERVICE);
						ime.hideSoftInputFromWindow(textView.getWindowToken(), 0);
						return true;
					default:
						return false;
				}
			}
		});
		nameTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> adapterView, View view, int position,
											  long id) {
				selectPokemon(((TextView) view).getText().toString());
			}
		});
	}

	private void selectPokemon(String name) {
		final String[] objects = getResources().getStringArray(R.array.pokemon_names);
		int dexNumber = 0;
		int form = 0;
		for (int i = 0; i < objects.length; i++) {
			if (objects[i].contains(name)) {
				dexNumber = i;
				form = Pokemon.getForm(objects[i], name);
			}
		}
		try {
			Bundle bundle = saveChanges();
			mPokemon = PokemonFactory
					.createPokemon(getActivity(), 6, dexNumber, form, Pokemon.DEFAULT_LEVEL);
			if (bundle != null) {
				restoreValues(bundle);
			}
			updatePokemon();
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
	}

	private void prepareBaseStatsViews(View v) {
		mBaseStatsViews = new HashMap<>();
		mBaseStatsViews
				.put(Stats.Stat.HP, (TextView) v.findViewById(R.id.base_hp));
		mBaseStatsViews.put(Stats.Stat.ATTACK,
				(TextView) v.findViewById(R.id.base_attack));
		mBaseStatsViews.put(Stats.Stat.DEFENSE,
				(TextView) v.findViewById(R.id.base_defense));
		mBaseStatsViews.put(Stats.Stat.SPECIAL_ATTACK,
				(TextView) v.findViewById(R.id.base_special_attack));
		mBaseStatsViews.put(Stats.Stat.SPECIAL_DEFENSE,
				(TextView) v.findViewById(R.id.base_special_defense));
		mBaseStatsViews.put(Stats.Stat.SPEED,
				(TextView) v.findViewById(R.id.base_speed));
	}

	private void restoreValues(Bundle bundle) throws IOException, JSONException {
		Pokemon pokemon = PokemonFactory.createFromBundle(getActivity(), bundle);
		if (pokemon != null) {
			mPokemon = pokemon;
			updatePokemon();
		}
	}

	private Bundle saveChanges() {
		if (mPokemon != null) {
			return mPokemon.save();
		} else {
			return null;
		}
	}

	public void updatePokemon() {
		updateBaseStatsViews();
		mCallbacks.onTeamMemberChanged(mPosition, mPokemon);
	}

	private void updateBaseStatsViews() {
		for (Stats.Stat stat : mPokemon.stats().base().keySet()) {
			mBaseStatsViews.get(stat).setText("" + mPokemon.stats().base().get(stat));
		}
	}

	public interface OnTeamMemberChangedListener {
		void onTeamMemberChanged(int position, Pokemon pokemon);
	}
}
