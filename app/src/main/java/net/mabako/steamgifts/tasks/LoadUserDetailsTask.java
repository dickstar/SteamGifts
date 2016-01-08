package net.mabako.steamgifts.tasks;

import android.os.AsyncTask;
import android.util.Log;

import net.mabako.steamgifts.data.Giveaway;
import net.mabako.steamgifts.fragments.UserDetailFragment;
import net.mabako.steamgifts.web.WebUserData;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;

public class LoadUserDetailsTask extends AsyncTask<Void, Void, List<Giveaway>> {
    private static final String TAG = LoadUserDetailsTask.class.getSimpleName();

    private final UserDetailFragment.UserGiveawayListFragment fragment;
    private final String path;
    private final int page;

    public LoadUserDetailsTask(UserDetailFragment.UserGiveawayListFragment fragment, String path, int page) {
        this.fragment = fragment;
        this.path = path;
        this.page = page;
    }

    @Override
    protected List<Giveaway> doInBackground(Void... params) {
        Log.d(TAG, "Fetching giveaways for user " + path + " on page " + page);

        try {
            // Fetch the Giveaway page
            Connection connection = Jsoup.connect("http://www.steamgifts.com/user/" + path + "/search");
            connection.data("page", Integer.toString(page));
            if (WebUserData.getCurrent().isLoggedIn())
                connection.cookie("PHPSESSID", WebUserData.getCurrent().getSessionId());

            connection.followRedirects(false);
            Connection.Response response = connection.execute();
            Document document = response.parse();

            if (response.statusCode() == 200) {

                WebUserData.extract(document);

                // Parse all rows of giveaways
                return Utils.loadGiveawaysFromList(document);
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching URL", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Giveaway> result) {
        super.onPostExecute(result);
        fragment.addItems(result, page == 1);
    }
}
