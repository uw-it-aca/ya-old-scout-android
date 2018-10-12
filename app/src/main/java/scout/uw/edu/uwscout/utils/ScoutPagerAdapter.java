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

public class ScoutPagerAdapter extends PagerAdapter implements TurbolinksAdapter {

    private Context mContext;
    private Resources res;
    private TurbolinksSession[] sessions;
    private UserPreferences userPreferences;
    private TurbolinksView[] views;

    public ScoutPagerAdapter (Context context) {
        mContext = context;
        res = mContext.getResources();
        userPreferences = ((MainActivity)context).getUserPreferences();

        views = new TurbolinksView[4];
        views[0] = ((Activity)mContext).findViewById(R.id.discover_view);
        views[1] = ((Activity)mContext).findViewById(R.id.food_view);
        views[2] = ((Activity)mContext).findViewById(R.id.study_view);
        views[3] = ((Activity)mContext).findViewById(R.id.tech_view);

        sessions = new TurbolinksSession[4];
        for(int i = 0; i < sessions.length; i++) {
            sessions[i] = TurbolinksSession.getNew(mContext);
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

        TurbolinksSession session = sessions[position];
        TurbolinksView view = views[position];

        session.setDebugLoggingEnabled(true);

        session.activity((Activity)mContext)
                .adapter(this)
                .view(view)
                .visit(url);

        return view;
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

    @Override
    public void onPageFinished() {
    }

    @Override
    public void onReceivedError(int errorCode) {

    }

    @Override
    public void pageInvalidated() {
        Log.d("Invalidated!","");
    }

    @Override
    public void requestFailedWithStatusCode(int statusCode) {

    }

    @Override
    public void visitCompleted() {

    }

    /**
     * called when user visits a link from within a turbolinks view
     * @param location the url the user is visiting
     * @param action String representing whether this is a forward or backward navigation
     */
    @Override
    public void visitProposedToLocationWithAction(String location, String action) {
        Intent intent = new Intent(mContext, DetailActivity.class);
        intent.putExtra("INTENT_URL", location);
        mContext.startActivity(intent);
    }

    public void reloadViews () {
        for (int i = 0; i < views.length; i++) {
            String url = userPreferences.getTabURL(i);
            TurbolinksSession session = sessions[i];
            if(!url.equals(session.getWebView().getUrl())) {
                session.resetToColdBoot();
            }
            //Log.d("LOADING", url);
            //session.getWebView().clearCache(false);
            session.activity((Activity)mContext)
                    .adapter(this)
                    .view(views[i])
                    .visit(url);
        }
    }

    @Override
    public String getPageTitle (int tab) {
        return sessions[tab].getWebView().getTitle();
    }
}
