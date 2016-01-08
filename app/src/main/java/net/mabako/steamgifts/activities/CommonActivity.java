package net.mabako.steamgifts.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import net.mabako.steamgifts.R;
import net.mabako.steamgifts.data.BasicDiscussion;
import net.mabako.steamgifts.data.BasicGiveaway;
import net.mabako.steamgifts.fragments.DiscussionDetailFragment;
import net.mabako.steamgifts.fragments.GiveawayDetailFragment;
import net.mabako.steamgifts.fragments.IFragmentNotifications;
import net.mabako.steamgifts.web.WebUserData;

public class CommonActivity extends BaseActivity {
    private static final String TAG = CommonActivity.class.getSimpleName();
    private static final String FRAGMENT_TAG = "Fragment Root";

    public static final int REQUEST_LOGIN = 3;
    public static final int REQUEST_LOGIN_PASSIVE = 4;

    public static final int RESPONSE_LOGIN_SUCCESSFUL = 5;

    public void requestLogin() {
        startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_LOGIN);
    }

    protected void loadFragment(Fragment fragment) {
        super.loadFragment(R.id.fragment_container, fragment, FRAGMENT_TAG);

        // Update the title.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            Log.v(TAG, "Current Fragment is a " + fragment.getClass().getName());
            if (fragment instanceof IFragmentNotifications) {
                String title = null;
                int resource = ((IFragmentNotifications) fragment).getTitleResource();
                String extra = ((IFragmentNotifications) fragment).getExtraTitle();

                if (resource != 0) {
                    if (extra != null && !extra.isEmpty())
                        title = String.format("%s: %s", extra, getString(resource));
                    else
                        title = getString(resource);
                } else
                    title = extra;

                actionBar.setTitle(title);
                Log.v(TAG, "Setting Toolbar title to " + title);
            } else
                actionBar.setTitle(R.string.app_name);
        }
    }

    public Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOGIN:
            case REQUEST_LOGIN_PASSIVE:
                // Do not show an explicit notification.
                if (resultCode == RESPONSE_LOGIN_SUCCESSFUL && WebUserData.getCurrent().isLoggedIn())
                    onAccountChange();

                // Pass on the result.
                setResult(resultCode);

                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            event.startTracking();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Always-available "Go to ..." menu by long-pressing back.
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        // TODO allow this to be changed to normal overflow menus in the settings.
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            final CharSequence[] strings = new CharSequence[]{getString(R.string.go_to_giveaway), getString(R.string.go_to_discussion)};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.go_to);
            builder.setItems(strings, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, final int dialogSelected) {
                    final View view = getLayoutInflater().inflate(R.layout.go_to_dialog, null);

                    AlertDialog.Builder builder = new AlertDialog.Builder(CommonActivity.this);
                    builder.setTitle(R.string.go_to);
                    builder.setMessage(strings[dialogSelected]);
                    builder.setView(view);
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    });
                    final AlertDialog dialog = builder.create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String target = ((EditText) view.findViewById(R.id.edit_text)).getText().toString();
                            if (target != null && target.length() == 5) {
                                Intent intent = new Intent(CommonActivity.this, DetailActivity.class);
                                if (dialogSelected == 0) {
                                    intent.putExtra(GiveawayDetailFragment.ARG_GIVEAWAY, new BasicGiveaway(target));
                                } else {
                                    intent.putExtra(DiscussionDetailFragment.ARG_DISCUSSION, new BasicDiscussion(target));
                                }
                                startActivity(intent);

                                dialog.dismiss();
                            }
                        }
                    });

                }
            });
            builder.show();
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }
}
