package com.suicune.poketools.view.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.suicune.poketools.R;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class MainNavigationDrawerFragment extends Fragment {

	// Remember the position of the selected item.
	private static final String STATE_SELECTED_POSITION =
			"selected_main_navigation_drawer_position";

	// Per the design guidelines, you should show the drawer on launch until the user manually
	// expands it. This shared preference tracks this.
	private static final String PREF_USER_LEARNED_DRAWER = "main_navigation_drawer_learned";

	// A pointer to the current listener instance (the Activity).
	private NavigationDrawerCallbacks listener;

	// Helper component that ties the action bar to the navigation drawer.
	private ActionBarDrawerToggle drawerToggle;

	private DrawerLayout drawerLayout;
	private ListView drawerListView;
	private View fragmentContainerView;

	private int currentSelectedPosition;
	private boolean userLearnedDrawer;

	private SharedPreferences prefs;

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Read in the flag indicating whether or not the user has demonstrated awareness of the
		// drawer. See PREF_USER_LEARNED_DRAWER for details.
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		userLearnedDrawer = prefs.getBoolean(PREF_USER_LEARNED_DRAWER, false);

		currentSelectedPosition = prefs.getInt(STATE_SELECTED_POSITION, -1);

		// Select either the default item (0) or the last selected item.
		selectItem(currentSelectedPosition);
	}

	@Override public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Indicate that this fragment would like to influence the set of actions in the action bar.
		setHasOptionsMenu(true);
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
									   Bundle savedInstanceState) {
		drawerListView =
				(ListView) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
		drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (currentSelectedPosition == position) {
					if (drawerLayout != null) {
						drawerLayout.closeDrawer(fragmentContainerView);
					}
				} else {
					selectItem(position);
				}
			}
		});
		drawerListView.setAdapter(
				new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,
						android.R.id.text1,
						new String[]{getString(R.string.team_builder_fragment_title),
									 getString(R.string.damage_calc_fragment_title),
									 getString(R.string.iv_breeder_calc_fragment_title),
									 getString(R.string.iv_calc_fragment_title),}));
		drawerListView.setItemChecked(currentSelectedPosition, true);
		return drawerListView;
	}

	public boolean isDrawerOpen() {
		return drawerLayout != null && drawerLayout.isDrawerOpen(fragmentContainerView);
	}

	/**
	 * Users of this fragment must call this method to set up the navigation drawer interactions.
	 *
	 * @param fragmentId   The android:id of this fragment in its activity's layout.
	 * @param drawerLayout The DrawerLayout containing this fragment's UI.
	 */
	public void setUp(int fragmentId, DrawerLayout drawerLayout) {
		fragmentContainerView = getActivity().findViewById(fragmentId);
		this.drawerLayout = drawerLayout;

		// set a custom shadow that overlays the main content when the drawer opens
		this.drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.main_toolbar);
		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		drawerToggle = new ActionBarDrawerToggle(getActivity(),
				MainNavigationDrawerFragment.this.drawerLayout, toolbar,
				R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
			@Override public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (isAdded()) {
					getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
				}
			}

			@Override public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (isAdded()) {
					getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
					if (!userLearnedDrawer) {
						userLearnedDrawer = true;
						prefs.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
					}
				}
			}
		};

		// If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
		// per the navigation drawer design guidelines.
		if (!userLearnedDrawer) {
			this.drawerLayout.openDrawer(fragmentContainerView);
		}

		// Defer code dependent on restoration of previous instance state.
		this.drawerLayout.post(new Runnable() {
			@Override public void run() {
				drawerToggle.syncState();
			}
		});

		this.drawerLayout.setDrawerListener(drawerToggle);
	}

	private void selectItem(int position) {
		currentSelectedPosition = position;
		if (drawerListView != null) {
			drawerListView.setItemChecked(position, true);
		}
		if (drawerLayout != null) {
			drawerLayout.closeDrawer(fragmentContainerView);
		}
		if (listener != null) {
			if(position == 0) {
				listener.onTeamBuilderRequested();
			} else {
				listener.onNavigationDrawerItemSelected(position);
			}
		}
		prefs.edit().putInt(STATE_SELECTED_POSITION, currentSelectedPosition).apply();
	}

	@Override public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (NavigationDrawerCallbacks) activity;
	}

	@Override public void onDetach() {
		super.onDetach();
		listener = null;
	}

	@Override public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SELECTED_POSITION, currentSelectedPosition);
	}

	@Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// If the drawer is open, show the global app actions in the action bar. See also
		// showGlobalContextActionBar, which controls the top-left area of the action bar.
		if (drawerLayout != null && isDrawerOpen()) {
			inflater.inflate(R.menu.global, menu);
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_example:
				Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public interface NavigationDrawerCallbacks {
		void onTeamBuilderRequested();

		void onNavigationDrawerItemSelected(int position);
	}
}
