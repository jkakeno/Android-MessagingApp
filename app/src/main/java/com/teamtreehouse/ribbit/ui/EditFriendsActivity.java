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

/*This class is called from main to handle the selection of users to be included in the friends list.*/

public class EditFriendsActivity extends Activity{
    public static final String TAG = EditFriendsActivity.class.getSimpleName();

    protected ArrayList<User> mFriends = new ArrayList<>();
    protected User mCurrentUser;
    protected GridView mGridView;
    protected List<User> mUsers;

    Button mConfirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);
/*Request rotating circle in top right corner in Actionbar
which represent some background action to be included.*/
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.edit_friends);
// Show the Up button in the action bar.
        setupActionBar();
//Set up the grid view
        mGridView = (GridView) findViewById(R.id.friendsGrid);
        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        mGridView.setOnItemClickListener(mOnItemClickListener);
        TextView emptyTextView = (TextView) findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);
//Set up confirm button
        mConfirm = (Button) findViewById(R.id.confirm_button);
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//Return the result back to main activity by storing the list of friends in FRIEND_LIST.
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
//Create a query (list) of users. (Use a query as a way of storing data).
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
//Set the user adapter with the list of users
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
//Inflate the check image view
            ImageView checkImageView = (ImageView) view.findViewById(R.id.checkImageView);
/*If the user is not checked.
NOTE: That the confition below points to a specific user defined by position of the grid clicked,
since the list of user have been populated to the grid view each user occupies a specific grid.*/
            if (!mUsers.get(position).isChecked) {
//Add a user to the list of friends
                    mFriends.add(mUsers.get(position));
//Set the check image visible
                    checkImageView.setVisibility(View.VISIBLE);
//Set the user check state to true
                    mUsers.get(position).setCheckState(true);
            } else {
//Remove a user from the list of friends
                    mFriends.remove(mUsers.get(position));
//Set the check image invisible
                    checkImageView.setVisibility(View.INVISIBLE);
//Set the user check state to false
                    mUsers.get(position).setCheckState(false);
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










