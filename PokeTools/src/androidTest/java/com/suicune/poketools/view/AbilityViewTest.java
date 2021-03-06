package com.suicune.poketools.view;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.suicune.poketools.R;
import com.suicune.poketools.view.activities.MainActivity;
import com.suicune.poketools.model.Ability;
import com.suicune.poketools.model.factories.AbilityFactory;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class AbilityViewTest {
	AbilityView view;
	MainActivity activity;
	Ability[] abilities;

	@Rule public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);
	AbilityView.OnAbilitySelectedListener listener;

	@Before public void setup() throws Exception {
		abilities = AbilityFactory.getAbilityList(InstrumentationRegistry.getTargetContext(), 6);
	}

	@Test public void testSetAbility() throws Throwable {
		createView();
		view.setAsCurrent(abilities[4]);
		assertEquals(abilities[4], view.getSelectedItem());
	}

	private void createView() throws Throwable {
		rule.runOnUiThread(new Runnable() {
			@Override public void run() {
				view = new AbilityView(InstrumentationRegistry.getTargetContext());
			}
		});
		setupListener();
	}

	private void setupListener() throws Throwable {
		rule.runOnUiThread(new Runnable() {
			@Override public void run() {
				listener = mock(AbilityView.OnAbilitySelectedListener.class);
				view.setup(listener);
			}
		});
	}

	@Test public void withANewSelectionTheCallbackIsCalled() throws Throwable {
		loadView();
		onView(withId(R.id.ability)).perform(click());
		onView(withText(abilities[3].name())).perform(click());
		verify(listener).onAbilitySelected(abilities[3]);
	}

	private void loadView() throws Throwable {
		activity = rule.getActivity();
		view = (AbilityView) activity.findViewById(R.id.ability);
		setupListener();
	}
}