package com.teamtreehouse.ribbit.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.teamtreehouse.ribbit.R;
import com.teamtreehouse.ribbit.adapters.UserAdapter;
import com.teamtreehouse.ribbit.models.Query;
import com.teamtreehouse.ribbit.models.Relation;
import com.teamtreehouse.ribbit.models.User;
import com.teamtreehouse.ribbit.models.callbacks.FindCallback;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {

    public static final String TAG = FriendsFragment.class.getSimpleName();
    protected User mCurrentUser;
    protected ArrayList<User> mFriends = new ArrayList<>();
    protected GridView mGridView;
    Button mSend;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
//Inflate the root view for this fragment
        View rootView = inflater.inflate(R.layout.user_grid, container, false);

//Set the grid.
        mGridView = (GridView) rootView.findViewById(R.id.friendsGrid);
        TextView emptyTextView = (TextView) rootView.findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);

/*Since this activity is reusing a layout which has a mSend button,
but the mSend button is not needed for this activity make the button invisible.*/
        mSend = (Button) rootView.findViewById(R.id.send_btn);
        mSend.setVisibility(View.GONE);

        return rootView;
    }

    @Override
    public void onResume() {
        Log.d(TAG,"onResume");
        super.onResume();
        getActivity().setProgressBarIndeterminateVisibility(true);

//Get the current user (loged in user) and the relation with the other users.
        mCurrentUser = User.getCurrentUser();
        Relation relation = new Relation();

//Create a query of users but get the query from relations, which excludes the current user.
        Query<User> query = relation.getQuery();
        query.addAscendingOrder(User.KEY_USER_ID);
        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> friends, Exception e) {
                getActivity().setProgressBarIndeterminateVisibility(false);
                if (e == null) {
//Set the user adapter, pass the friend list.
                        UserAdapter adapter = new UserAdapter(getActivity(), mFriends);
                        mGridView.setAdapter(adapter);
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

}
