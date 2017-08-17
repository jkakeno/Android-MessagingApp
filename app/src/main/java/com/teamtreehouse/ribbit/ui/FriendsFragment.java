package com.teamtreehouse.ribbit.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.teamtreehouse.ribbit.R;
import com.teamtreehouse.ribbit.models.User;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {

    public static final String TAG = FriendsFragment.class.getSimpleName();

    protected ArrayList<User> mFriendsRelation;
    protected User mCurrentUser;
    protected List<User> mFriends;
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
//        mFriendsRelation = mCurrentUser.getRelation(User.KEY_FRIENDS_RELATION); //<-- Returns a list of users which size is ramdom between 1 and the number of MockUsers. Follow User.getRelation and MockRelations.getUserRelations
        mFriendsRelation = new ArrayList<>();
//        mFriendsRelation.add(User.getCurrentUser()); //Just to try currently only adds current user at onResume to the FriendFragment
//TODO: Need to find out how to get the mFriendRelation list from EditFriendsActivity
        Bundle bundle = getArguments();
//        mFriendsRelation = bundle.getParcelableArrayList("FRIEND_LIST");
        Log.d(TAG, "mFriendRelation size: "+mFriendsRelation.size());

        getActivity().setProgressBarIndeterminateVisibility(true);

//
//        Query<User> query = Relation.getQuery();
//        query.addAscendingOrder(User.KEY_USER_ID);
//        query.findInBackground(new FindCallback<User>() {
//            @Override
//            public void done(List<User> friends, Exception e) {
//                getActivity().setProgressBarIndeterminateVisibility(false);
//
//                if (e == null) {
//                    mFriends = friends;     //<-- mFriends should be filled according to EditFriendsActivity
//
//                    String[] usernames = new String[mFriends.size()];
//                    Log.d(TAG, String.valueOf(mFriends.size()));
//                    int i = 0;
//                    for (User user : mFriends) {
//                        usernames[i] = user.getUsername();
//                        i++;
//                    }
//                    if (mGridView.getAdapter() == null) {
//                        Log.d(TAG,"adapter is null");
//                        UserAdapter adapter = new UserAdapter(getActivity(), mFriends); //<-- CHECK list of user get added to FriendsFragment unconditionaly always the same users mFriends
//                        mGridView.setAdapter(adapter);
//                    } else {
//                        Log.d(TAG,"adapter is not null");
//                        ((UserAdapter) mGridView.getAdapter()).refill(mFriends);
//                    }
//                } else {
//                    Log.e(TAG, e.getMessage());
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                    builder.setMessage(e.getMessage())
//                            .setTitle(R.string.error_title)
//                            .setPositiveButton(android.R.string.ok, null);
//                    AlertDialog dialog = builder.create();
//                    dialog.show();
//                }
//            }
//        });
    }

}
