package com.teamtreehouse.ribbit.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.teamtreehouse.ribbit.R;
import com.teamtreehouse.ribbit.adapters.MessageAdapter;
import com.teamtreehouse.ribbit.mockdata.MockUsers;
import com.teamtreehouse.ribbit.models.Message;
import com.teamtreehouse.ribbit.models.MessageFile;
import com.teamtreehouse.ribbit.models.Query;
import com.teamtreehouse.ribbit.models.User;
import com.teamtreehouse.ribbit.models.callbacks.FindCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class InboxFragment extends ListFragment {

    private static final String TAG = InboxFragment.class.getSimpleName();
    protected List<Message> mMessages;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    ImageButton mFab;
    protected ArrayList<User> mFriends = new ArrayList<>();
    MessageAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        Log.d(TAG,"onCreateView");
//Inflate the root view for this fragment
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
//Set the swipe refresh layout in the root view
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.swipeRefresh1,
                R.color.swipeRefresh2,
                R.color.swipeRefresh3,
                R.color.swipeRefresh4);
//Set the fab button
        mFab = (ImageButton) rootView.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"Create message");
//Get the friend list stored in the variable (mFriends) of the activity this fragment is
//associated with and pass it to message activity.
                Intent intent = new Intent(getActivity(),MessageActivity.class);
                intent.putExtra("FRIEND_LIST", mFriends);
                startActivity(intent);
            }
        });
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

        int randomUserIndex = new Random().nextInt(MockUsers.testUsers.size());
        User user = MockUsers.testUsers.get(randomUserIndex);
//Create a query (list) of messages. (Use a query as a way of storing data).
        Query<Message> query = Message.getQuery();
        query.whereEqualTo(Message.KEY_RECIPIENT_IDS, user.getObjectId());
        query.addDescendingOrder(Message.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> messages, Exception e) {
                getActivity().setProgressBarIndeterminateVisibility(false);
//Set the swipe refresh layout.
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                if (e == null) {
// We found messages!
                    mMessages = messages;
                    Log.d(TAG,"Found " + mMessages.size() + " messages");
//Log the message id and sender name for all received messages.
                    String[] senderNames = new String[mMessages.size()];
                    int i = 0;
                    for (Message message : mMessages) {
                        senderNames[i] = message.getString(Message.KEY_SENDER_NAME);
                        i++;
                        Log.d(TAG, "Message from: " + message.getString(Message.KEY_SENDER_NAME)+ " Message Id: "+message.getId());
                    }
//Set the message adapter, pass the message list.
                    adapter = new MessageAdapter(getListView().getContext(), mMessages);
                    setListAdapter(adapter);
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG,"onListItemClick");
        super.onListItemClick(l, v, position, id);
/*Get a specific message when clicked the position on the list view.
Use this specific message to get message type and file.*/
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
    }

    protected OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh() {
            retrieveMessages();
        }
    };

//Delete a message on long click
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Would you like to delete this message from your inbox?")
                        .setTitle("Delete Message")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mMessages.remove(mMessages.get(position));
                                setListAdapter(adapter);
                                Log.d(TAG,"Number of messages: "+mMessages.size());
                            }
                        })
                        .setNegativeButton(android.R.string.cancel,null);
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
    }
}








