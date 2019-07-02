package scout.uw.edu.uwscout.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.basecamp.turbolinks.TurbolinksAdapter;
import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;

import scout.uw.edu.uwscout.DetailActivity;

public class ScoutTurbolinksAdapter implements TurbolinksAdapter {

    private Context mContext;
    UserPreferences userPreferences;
    TurbolinksView mView;
    TurbolinksSession mSession;

    public ScoutTurbolinksAdapter(Context context, UserPreferences prefs, TurbolinksView view) {
        this.mContext = context;
        this.userPreferences = prefs;
        this.mView = view;
        this.mSession = TurbolinksSession.getNew(mContext);
        mSession.addJavascriptInterface(this, "scoutBridge");

        mSession.setDebugLoggingEnabled(true);

        // affects CustomViewPagers
        mSession.setPullToRefreshEnabled(false);

        mSession.activity((Activity)mContext)
                .adapter(this)
                .view(view);
    }

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

    public void reloadView (int tab) {
        String url = userPreferences.getTabURL(tab);
        if(!url.equals(mSession.getWebView().getUrl())) {
            mSession.resetToColdBoot();
        }
        //Log.d("LOADING", url);
        //session.getWebView().clearCache(false);
        mSession.activity((Activity)mContext)
                .adapter(this)
                .view(mView)
                .visit(url);
    }

    public TurbolinksSession getSession() {
        return mSession;
    }

    @android.webkit.JavascriptInterface
    public boolean setParams(String s) {
        Location loc = this.userPreferences.getLocation();
        mSession.runJavascript(
                "Geolocation.getNativeLocation",
                "" + loc.getLatitude(),
                "" + loc.getLongitude()
        );
        return true;
    }
}
