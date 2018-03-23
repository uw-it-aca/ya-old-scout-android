package scout.uw.edu.scout_android_poc.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.basecamp.turbolinks.TurbolinksAdapter;
import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;

import scout.uw.edu.scout_android_poc.DetailActivity;
import scout.uw.edu.scout_android_poc.MainActivity;
import scout.uw.edu.scout_android_poc.R;
import scout.uw.edu.scout_android_poc.services.TurbolinksSessionManager;

/**
 * Created by adikumar on 3/21/18.
 *
 * Allows ViewPager to directly display views instead of Fragments
 */

public class ScoutPagerAdapter extends PagerAdapter implements TurbolinksAdapter {

    private Context mContext;
    Resources res;
    TurbolinksSessionManager sessionManager;
    TurbolinksView[] views;

    public ScoutPagerAdapter (Context context) {
        mContext = context;
        res = mContext.getResources();
        sessionManager = new TurbolinksSessionManager();
        views = new TurbolinksView[4];
        views[0] = ((Activity)mContext).findViewById(R.id.discover_view);
        views[1] = ((Activity)mContext).findViewById(R.id.food_view);
        views[2] = ((Activity)mContext).findViewById(R.id.study_view);
        views[3] = ((Activity)mContext).findViewById(R.id.tech_view);
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
        String url = getUrl(position);

        TurbolinksSession session = sessionManager.getSession(url, mContext);

        TurbolinksView view = views[position];

        session.activity((Activity)mContext)
                .adapter(this)
                .view(view)
                .visit(url);

        //session.setDebugLoggingEnabled(true);

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
        //container.removeView((View) object);
    }

    /**
     * gets the URL for a given view
     * @param position an int representing the View's position
     * @return a String containing the url to be loaded by the view.
     */
    private String getUrl(int position){
        String url = res.getString(R.string.baseUrl);
        url += res.getStringArray(R.array.campusUrls)[0];
        url += res.getStringArray(R.array.baseUrls)[position];
        return url;
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
}
