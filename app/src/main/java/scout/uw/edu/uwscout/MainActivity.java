package scout.uw.edu.uwscout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import butterknife.BindArray;
import butterknife.BindString;
import scout.uw.edu.uwscout.utils.CustomViewPager;
import scout.uw.edu.uwscout.utils.ScoutPagerAdapter;
import scout.uw.edu.uwscout.utils.UserPreferences;

public class MainActivity extends ScoutActivity {

    private AHBottomNavigation bottomNavigationView;
    private AHBottomNavigationAdapter navAdapter;
    @BindArray(R.array.scout_tabs) String[] scoutTabs;
    @BindString(R.string.help_email)
    String helpEmail;
    @BindString(R.string.help_subject)
    String helpSubject;
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

        viewPager = (CustomViewPager) findViewById(R.id.viewpager);
        pagerAdapter = new ScoutPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setPagingEnabled(false);

        String[] viewNames = getResources().getStringArray(R.array.views);

        bottomNavigationView = (AHBottomNavigation) findViewById(R.id.navigation);
        AHBottomNavigationItem discButt = new AHBottomNavigationItem(viewNames[0],
                R.drawable.ic_home_24px);
        AHBottomNavigationItem foodButt = new AHBottomNavigationItem(viewNames[1],
                R.drawable.ic_restaurant_black_24dp);
        AHBottomNavigationItem studyButt = new AHBottomNavigationItem(viewNames[2],
                R.drawable.ic_local_library_black_24dp);
        AHBottomNavigationItem techButt = new AHBottomNavigationItem(viewNames[3],
                R.drawable.ic_laptop);
        bottomNavigationView.addItem(discButt);
        bottomNavigationView.addItem(foodButt);
        bottomNavigationView.addItem(studyButt);
        bottomNavigationView.addItem(techButt);
        bottomNavigationView.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigationView.setAccentColor(R.color.colorBottomNavigationPrimary);

        navAdapter = new AHBottomNavigationAdapter(this, R.menu.bottom_navigation_menu);
        bottomNavigationView.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                //replace campus button with filter
                if (position == 0) {
                    mMenu.getItem(0).setVisible(true);
                    mMenu.getItem(1).setVisible(false);
                } else {
                    mMenu.getItem(0).setVisible(false);
                    mMenu.getItem(1).setVisible(true);
                }
                viewPager.setCurrentItem(position);
                //bottomNavigationView.setCurrentItem(position);
                setTitle(pagerAdapter.getPageTitle(viewPager.getCurrentItem()));

                return true;
            }
        });

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
        pagerAdapter.reloadViews();

        super.onResume();
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
