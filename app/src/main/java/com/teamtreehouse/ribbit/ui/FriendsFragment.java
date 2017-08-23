package com.teamtreehouse.ribbit.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        View rootView = inflater.inflate(R.layout.user_grid, container, false);

        mGridView = (GridView) rootView.findViewById(R.id.friendsGrid);

        TextView emptyTextView = (TextView) rootView.findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);

        return rootView;
    }

    @Override
    public void onResume() {
        Log.d(TAG,"onResume");
        super.onResume();

        mCurrentUser = User.getCurrentUser();

        getActivity().setProgressBarIndeterminateVisibility(true);

        Relation relation = new Relation();
        Query<User> query = relation.getQuery();
        query.addAscendingOrder(User.KEY_USER_ID);
        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> friends, Exception e) {
                getActivity().setProgressBarIndeterminateVisibility(false);

                if (e == null) {
                        Log.d(TAG, "mFriends size in FriendsFragment is : "+ mFriends.size());
                        UserAdapter adapter = new UserAdapter(getActivity(), mFriends); //<-- CHECK list of user get added to FriendsFragment unconditionaly always the same users mFriends
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
