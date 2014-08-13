package com.pixelus.dashclock.ext.mydevice;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.view.MenuItem;
import com.crashlytics.android.Crashlytics;

public class DeviceInfoExtensionActivity extends PreferenceActivity
    implements SharedPreferences.OnSharedPreferenceChangeListener {

  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getActionBar().setIcon(R.drawable.ic_launcher);
    getActionBar().setDisplayHomeAsUpEnabled(true);

    Crashlytics.start(this);
  }

  @Override
  protected void onPostCreate(final Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    setupSimplePreferencesScreen();
    initSummary(getPreferenceScreen());
  }

  @Override
  public boolean onOptionsItemSelected(final MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      // TODO: if the previous activity on the stack isn't a ConfigurationActivity, launch it.
      finish();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onResume() {
    super.onResume();
    // Set up a listener whenever a key changes
    getPreferenceScreen().getSharedPreferences()
        .registerOnSharedPreferenceChangeListener(this);
  }

  @Override
  protected void onPause() {
    super.onPause();
    // Unregister the listener whenever a key changes
    getPreferenceScreen().getSharedPreferences()
        .unregisterOnSharedPreferenceChangeListener(this);
  }


  private void setupSimplePreferencesScreen() {
    // In the simplified UI, fragments are not used at all and we instead
    // use the older PreferenceActivity APIs.

    // Add 'general' preferences.
    addPreferencesFromResource(R.xml.preferences);
  }

  public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
    updatePrefSummary(findPreference(key));
  }

  private void initSummary(final Preference p) {
    if (p instanceof PreferenceGroup) {
      final PreferenceGroup pGrp = (PreferenceGroup) p;
      for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
        initSummary(pGrp.getPreference(i));
      }
    } else {
      updatePrefSummary(p);
    }
  }

  private void updatePrefSummary(final Preference p) {
    if (p instanceof ListPreference) {
      final ListPreference listPref = (ListPreference) p;
      p.setSummary(listPref.getEntry());
    }
    if (p instanceof EditTextPreference) {
      final EditTextPreference editTextPref = (EditTextPreference) p;
      if (p.getTitle().toString().contains("assword")) {
        p.setSummary("******");
      } else {
        p.setSummary(editTextPref.getText());
      }
    }
    if (p instanceof MultiSelectListPreference) {
      final EditTextPreference editTextPref = (EditTextPreference) p;
      p.setSummary(editTextPref.getText());
    }
  }
}