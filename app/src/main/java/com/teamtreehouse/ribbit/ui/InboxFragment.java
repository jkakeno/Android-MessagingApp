package com.teamtreehouse.ribbit.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.teamtreehouse.ribbit.R;
import com.teamtreehouse.ribbit.adapters.MessageAdapter;
import com.teamtreehouse.ribbit.mockdata.MockUsers;
import com.teamtreehouse.ribbit.models.Message;
import com.teamtreehouse.ribbit.models.MessageFile;
import com.teamtreehouse.ribbit.models.Query;
import com.teamtreehouse.ribbit.models.User;
import com.teamtreehouse.ribbit.models.callbacks.FindCallback;

import java.util.List;
import java.util.Random;

public class InboxFragment extends ListFragment {

    private static final String TAG = InboxFragment.class.getSimpleName();
    protected List<Message> mMessages;
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        Log.d(TAG,"onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.swipeRefresh1,
                R.color.swipeRefresh2,
                R.color.swipeRefresh3,
                R.color.swipeRefresh4);

//        retrieveMessages();

        return rootView;
    }

    @Override
    public void onResume() {
        Log.d(TAG,"onResume");
        super.onResume();
        getActivity().setProgressBarIndeterminateVisibility(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG,"onViewCreated");
        retrieveMessages();
    }

    private void retrieveMessages() {
        Log.d(TAG,"retrieveMessages");
        Query<Message> query = Message.getQuery();
        int randomUserIndex = new Random().nextInt(MockUsers.testUsers.size());
        User user = MockUsers.testUsers.get(randomUserIndex);
        query.whereEqualTo(Message.KEY_RECIPIENT_IDS, user.getObjectId());
        query.addDescendingOrder(Message.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> messages, Exception e) {
                getActivity().setProgressBarIndeterminateVisibility(false);

                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                if (e == null) {
                    // We found messages!

                    mMessages = messages;
                    Log.d(TAG,"Found " + mMessages.size() + " messages");

                    String[] senderNames = new String[mMessages.size()];
                    int i = 0;
                    for (Message message : mMessages) {
                        senderNames[i] = message.getString(Message.KEY_SENDER_NAME);
                        i++;
                        Log.d(TAG, "Message from: " + message.getString(Message.KEY_SENDER_NAME));
                    }

                    MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);
                    setListAdapter(adapter);

/*Doesn't display messages*/
//                    if (getListView().getAdapter() == null) {
//                        Log.d(TAG, "List view adapter is " + getListView().getAdapter() + " so create adapter and fill it with " + mMessages.size() + " messages");
//                        MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);
//                        setListAdapter(adapter);
//                    }else{
//                        Log.d(TAG, "Refill the adapter with " + mMessages.size() + " messages");
//                        ((MessageAdapter) getListView().getAdapter()).refill(mMessages);
//                    }
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG,"onListItemClick");
        super.onListItemClick(l, v, position, id);

        Message message = mMessages.get(position);
        String messageType = message.getString(Message.KEY_FILE_TYPE);
        MessageFile file = message.getFile(Message.KEY_FILE);
        Uri fileUri = file.getUri();

        if (messageType.equals(Message.TYPE_IMAGE)) {
            // view the image
            Intent intent = new Intent(getActivity(), ViewImageActivity.class);
            intent.setData(fileUri);
            startActivity(intent);
        } else {
            // view the video
            Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
            intent.setDataAndType(fileUri, "video/*");
            startActivity(intent);
        }

        // Delete it!
        List<String> ids = message.getList(Message.KEY_RECIPIENT_IDS);

        if (ids.size() == 1) {
            // last recipient - delete the whole thing!
            message.deleteInBackground();
        }
        else {
            // remove the recipient
            message.removeRecipient(User.getCurrentUser().getObjectId());
        }
    }

    protected OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh() {
            retrieveMessages();
        }
    };
}








