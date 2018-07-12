package scout.uw.edu.scout_android_poc;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import butterknife.BindArray;
import butterknife.BindString;
import scout.uw.edu.scout_android_poc.utils.CustomViewPager;
import scout.uw.edu.scout_android_poc.utils.ScoutPagerAdapter;
import scout.uw.edu.scout_android_poc.utils.UserPreferences;

public class MainActivity extends ScoutActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationViewEx bottomNavigationView;
    @BindArray(R.array.scout_tabs) String[] scoutTabs;
    @BindString(R.string.help_email) String helpEmail;
    @BindString(R.string.help_subject) String helpSubject;
    @BindArray(R.array.views) String[] scoutTabTitles;
    private ScoutPagerAdapter pagerAdapter;
    private CustomViewPager viewPager;
    private int campusIndex = -1;
    private Menu mMenu;

    /**
     * sets up viewpager and bottom nav. Runs when the activity is created
     *
     * @param savedInstanceState a Bundle containing the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewpager);
        pagerAdapter = new ScoutPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setPagingEnabled(false);

        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.enableShiftingMode(false);
        bottomNavigationView.enableItemShiftingMode(false);
        bottomNavigationView.enableAnimation(false);

        if (!userPreferences.hasUserOpenedApp()) {
            showCampusChooser();
        } else {
            campusIndex = userPreferences.getCampusSelectedIndex();
        }

    }

    /**
     * shows/hides the campus chooser and filter icons from the action bar when the bar is created
     *
     * @param menu a Menu containing a reference to the action bar
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mMenu = menu;

        if(bottomNavigationView.getCurrentItem() == 0) {
            mMenu.getItem(0).setVisible(true);
            mMenu.getItem(1).setVisible(false);
        } else {
            mMenu.getItem(0).setVisible(false);
            mMenu.getItem(1).setVisible(true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * reloads all of the pages when the app is resumed
     */
    @Override
    protected void onResume() {
        super.onResume();

        pagerAdapter.reloadViews();
    }

    /**
     * updates viewpager when a bottom navigation button is clicked
     *
     * @param item the MenuItem that was clicked
     * @return whetehr the item should be selected or not
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //replace campus button with filter
        mMenu.getItem(0).setVisible(false);
        mMenu.getItem(1).setVisible(true);

        switch (item.getItemId()) {
            case R.id.ic_arrow:
                viewPager.setCurrentItem(0, false);
                bottomNavigationView.getMenu().getItem(0).setChecked(true);

                // replace filter button with campus
                mMenu.getItem(0).setVisible(true);
                mMenu.getItem(1).setVisible(false);
                break;
            case R.id.ic_android:
                viewPager.setCurrentItem(1, false);
                bottomNavigationView.getMenu().getItem(1).setChecked(true);
                break;
            case R.id.ic_books:
                viewPager.setCurrentItem(2, false);
                bottomNavigationView.getMenu().getItem(2).setChecked(true);
                break;
            case R.id.ic_center_focus:
                viewPager.setCurrentItem(3, false);
                bottomNavigationView.getMenu().getItem(3).setChecked(true);
                break;
        }

        setTitle(pagerAdapter.getPageTitle(viewPager.getCurrentItem()));

        return false;
    }

    /**
     * gets the user preferences instance (there is only one - it follows the singleton pattern)
     *
     * @return the userPreferences instance
     */
    public UserPreferences getUserPreferences () {
        return userPreferences;
    }

    /**
     * handles option item clicks in the actionbar
     *
     * @param item the MenuItem that was selected
     * @return a boolean indicating whether or not the click shill be handles here
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        if (id == R.id.action_filter) {
            Intent filterIntent = new Intent(this, FilterActivity.class);
            int tab = viewPager.getCurrentItem();
            Log.d("TAB","" + tab);
            String[] split_url = userPreferences.getTabURL(tab).split("\\?");
            String raw_url = split_url[0];
            String params = "";
            if (split_url.length > 1) {
                params = split_url[1];
            }
            filterIntent.putExtra("INTENT_URL", raw_url + "filter/?" + params);
            filterIntent.putExtra("FILTER_TYPE", tab);
            startActivity(filterIntent);
        } else if (id == R.id.action_help) {
            String mailto = "mailto:" + helpEmail + "?subject=" + helpSubject;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mailto));
            startActivity(browserIntent);
        } else if (id == R.id.action_campus) {
            showCampusChooser();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *  displays a dialog that allows the user to select their campus
     */
    private void showCampusChooser() {
        int campusIndexSelected = userPreferences.getCampusSelectedIndex();
        new MaterialDialog.Builder(this)
                .title(R.string.choose_campus)
                .items(R.array.pref_campus_list_titles)
                .itemsCallbackSingleChoice(campusIndexSelected, campusChoiceCallback)
                .show();
    }

    /**
     * sts up location listeners when permissions are granted
     *
     * @param requestCode the request code of the permissions request
     * @param permissions an array of Strings containing the permissions granted
     * @param grantResults an array of ints corresponding to the grant results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 0) {
            userPreferences.getLocationManager().setUpLocationListeners();
        }
    }

    /**
     * Callback called by the the campus chooser
     */
    private MaterialDialog.ListCallbackSingleChoice campusChoiceCallback =
            new MaterialDialog.ListCallbackSingleChoice() {

                @Override
                public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                    if (which != -1) {
                        if (campusIndex != which) {
                            userPreferences.deleteFilters();
                            campusIndex = which;
                            userPreferences.setCampusByIndex(which);

                            Log.d("CAMPUS CHANGED", "Reloading tabs");

                            pagerAdapter.reloadViews();
                        }
                    }

                    dialog.dismiss();
                    return true;
                }
            };

}
