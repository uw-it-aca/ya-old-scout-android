package scout.uw.edu.uwscout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;

import com.basecamp.turbolinks.TurbolinksAdapter;
import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;

;

/**
 * Created by adikumar on 3/23/18.
 */

public class DetailActivity extends ScoutActivity implements TurbolinksAdapter {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);
        setTitle("");

        Bundle extras = getIntent().getExtras();
        String url = extras.getString("INTENT_URL");

        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        TurbolinksView turbolinksView = (TurbolinksView) findViewById(R.id.turbolinks_detail);

        Log.d("DetailActivity", "onCreate Called with intent url: " + url);
        turbolinksSession.activity(this)
                .adapter(this)
                .view(turbolinksView)
                .visit(url);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(false);

        return super.onPrepareOptionsMenu(menu);
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
        // set appbar title after the turbolinks visit
        this.setTitle(TurbolinksSession.getDefault(this).getWebView().getTitle());
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.d("DetailActivity", "visitCompleted Called");
    }

    /**
     * called when user visits a link from within a turbolinks view
     * @param location the url the user is visiting
     * @param action String representing whether this is a forward or backward navigation
     */
    @Override
    public void visitProposedToLocationWithAction(String location, String action) {
        Log.d("DetailActivity", "visitProposedToLocationWithAction Called");
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(location));
        startActivity(browserIntent);
    }
}
