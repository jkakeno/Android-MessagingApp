package com.teamtreehouse.ribbit.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamtreehouse.ribbit.R;
import com.teamtreehouse.ribbit.adapters.UserAdapter;
import com.teamtreehouse.ribbit.models.Query;
import com.teamtreehouse.ribbit.models.User;
import com.teamtreehouse.ribbit.models.callbacks.FindCallback;
import com.teamtreehouse.ribbit.models.callbacks.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class EditFriendsActivity extends Activity{

    protected ArrayList<User> mFriends = new ArrayList<>();
    protected User mCurrentUser;
    protected GridView mGridView;

    public static final String TAG = EditFriendsActivity.class.getSimpleName();

    protected List<User> mUsers;

    Button mConfirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.edit_friends);
        // Show the Up button in the action bar.
        setupActionBar();

        mGridView = (GridView) findViewById(R.id.friendsGrid);
        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        mGridView.setOnItemClickListener(mOnItemClickListener);

        TextView emptyTextView = (TextView) findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);
        mConfirm = (Button) findViewById(R.id.confirm_button);

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "mFriends size in EditFriendsActivity is : "+ mFriends.size());
                Intent intent = new Intent();
                intent.putExtra("FRIEND_LIST", mFriends);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        Log.d(TAG,"onResume");
        super.onResume();

        mCurrentUser = User.getCurrentUser();

        setProgressBarIndeterminateVisibility(true);

        Query<User> query = User.getQuery();
        query.orderByAscending(User.KEY_USERNAME);
        query.setLimit(1000);
        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> users, Exception e) {
                setProgressBarIndeterminateVisibility(false);
                if (e == null) {

                    // Success
                    mUsers = users;
                    UserAdapter adapter = new UserAdapter(EditFriendsActivity.this, mUsers);
                    mGridView.setAdapter(adapter);
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {

        getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG,"onOptionItemSelected");
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
            ImageView checkImageView = (ImageView) view.findViewById(R.id.checkImageView);
            Log.d(TAG,"User check state is: "+mUsers.get(position).state);
            Log.d(TAG,"Item is checked : "+mGridView.isItemChecked(position));
            if (mGridView.isItemChecked(position)) {

                    // add the friend
                    mFriends.add(mUsers.get(position));
                    checkImageView.setVisibility(View.VISIBLE);
                    mUsers.get(position).setCheckState(true);
                    Log.d(TAG, "add user " + mUsers.get(position).getUsername() + " to mFriends");
            } else {

                    // remove the friend
                    mFriends.remove(mUsers.get(position));
                    checkImageView.setVisibility(View.INVISIBLE);
                    mUsers.get(position).setCheckState(false);
                    Log.d(TAG, "remove user " + mUsers.get(position).getUsername() + " from mFriends");
            }

            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(Exception e) {
                    if (e != null) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });

        }
    };

}










