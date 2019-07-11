package scout.uw.edu.uwscout.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.basecamp.turbolinks.TurbolinksAdapter;
import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;

import scout.uw.edu.uwscout.DetailActivity;
import scout.uw.edu.uwscout.MainActivity;
import scout.uw.edu.uwscout.R;

/**
 * Created by adikumar on 3/21/18.
 *
 * Allows ViewPager to directly display views instead of Fragments
 */

public class ScoutPagerAdapter extends PagerAdapter {

    private Context mContext;
    private Resources res;
    private UserPreferences userPreferences;
    private TurbolinksView[] views;
    private ScoutTurbolinksAdapter[] adapters;

    public ScoutPagerAdapter (Context context) {
        mContext = context;
        res = mContext.getResources();
        userPreferences = ((MainActivity)context).getUserPreferences();

        views = new TurbolinksView[4];
        views[0] = (TurbolinksView) ((Activity)mContext).findViewById(R.id.discover_view);
        views[1] = (TurbolinksView) ((Activity)mContext).findViewById(R.id.food_view);
        views[2] = (TurbolinksView) ((Activity)mContext).findViewById(R.id.study_view);
        views[3] = (TurbolinksView) ((Activity)mContext).findViewById(R.id.tech_view);

        adapters = new ScoutTurbolinksAdapter[4];
        for (int i = 0; i < views.length; i++) {
            adapters[i] = new ScoutTurbolinksAdapter(mContext, userPreferences, views[i]);
        }
    }

    /**
     * Determines whther the given object belongs to the given view
     * @param view the View to be checked for association with the given Object
     * @param obj the Object to be checked for association with the given View
     * @return a boolean indicating whether or not the object is associated with the view
     */
    @Override
    public boolean isViewFromObject (View view, Object obj) {
        return view == obj;
    }

    /**
     * determines how many views there are to be paged through
     * @return the number of views
     */
    @Override
    public int getCount () {
        return res.getStringArray(R.array.views).length;
    }

    /**
     * instantiates the view at the given position withing the given container
     * @param container the ViewGroup in which the View should be instantiated
     * @param position an int indicating the index of the view to be instantiated
     * @return the View being added to the group
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        String url = userPreferences.getTabURL(position);

        if (adapters[position] == null) {
            adapters[position] = new ScoutTurbolinksAdapter(mContext, userPreferences, views[position]);
        }
        adapters[position].getSession().visit(url);

        return views[position];
    }

    /**
     * called whenever a view is set to be destroyed.
     * @param container the ViewGroup the view is a member of
     * @param position an int representing the View's position
     * @param object the View to be deleted
     */
    @Override
    public void destroyItem (ViewGroup container, int position, Object object) {
    }

    //TurboLinks Adapter


    public void reloadViews (boolean force) {
        int i = 0;
        for (ScoutTurbolinksAdapter adapter : adapters) {
            adapter.reloadView(i, force);
            i++;
        }
    }

    @Override
    public String getPageTitle (int tab) {
        return adapters[tab].getSession().getWebView().getTitle();
    }
}
