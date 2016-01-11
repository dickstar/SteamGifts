package net.mabako.steamgifts.fragments.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import net.mabako.steamgifts.R;
import net.mabako.steamgifts.activities.DetailActivity;
import net.mabako.steamgifts.adapters.EndlessAdapter;
import net.mabako.steamgifts.adapters.MessageAdapter;
import net.mabako.steamgifts.data.Comment;
import net.mabako.steamgifts.fragments.ListFragment;
import net.mabako.steamgifts.fragments.UserDetailFragment;
import net.mabako.steamgifts.fragments.interfaces.IActivityTitle;
import net.mabako.steamgifts.fragments.interfaces.ICommentableFragment;
import net.mabako.steamgifts.tasks.LoadMessagesTask;
import net.mabako.steamgifts.web.SteamGiftsUserData;

import java.io.Serializable;

public class MessageListFragment extends ListFragment<MessageAdapter> implements IActivityTitle, ICommentableFragment {
    public MessageListFragment() {
        allowSearch = false;
        loadItemsInitially = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    protected MessageAdapter createAdapter(RecyclerView listView) {
        return new MessageAdapter(listView, this, new EndlessAdapter.OnLoadListener() {
            @Override
            public void onLoad(int page) {
                fetchItems(page);
            }
        });
    }

    @Override
    protected void fetchItems(int page) {
        new LoadMessagesTask(this, page).execute();
    }

    @Override
    protected Serializable getType() {
        return null;
    }

    @Override
    public int getTitleResource() {
        return R.string.user_tab_notifications;
    }

    @Override
    public String getExtraTitle() {
        return null;
    }

    @Override
    public void showProfile(String user) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(UserDetailFragment.ARG_USER, user);
        getActivity().startActivity(intent);
    }

    @Override
    public void requestComment(Comment parentComment) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.messages_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.user:
                showProfile(SteamGiftsUserData.getCurrent().getName());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
