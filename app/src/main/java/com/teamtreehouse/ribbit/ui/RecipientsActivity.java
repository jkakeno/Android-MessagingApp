package com.teamtreehouse.ribbit.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.teamtreehouse.ribbit.R;
import com.teamtreehouse.ribbit.adapters.UserAdapter;
import com.teamtreehouse.ribbit.models.Message;
import com.teamtreehouse.ribbit.models.MessageFile;
import com.teamtreehouse.ribbit.models.Query;
import com.teamtreehouse.ribbit.models.Relation;
import com.teamtreehouse.ribbit.models.User;
import com.teamtreehouse.ribbit.models.callbacks.FindCallback;
import com.teamtreehouse.ribbit.models.callbacks.SaveCallback;
import com.teamtreehouse.ribbit.utils.FileHelper;

import java.util.ArrayList;
import java.util.List;

/*This class is called from main to handle the recipient selection
when selecting a image or video file to mSend.*/

public class RecipientsActivity extends Activity {

    public static final String TAG = RecipientsActivity.class.getSimpleName();
    protected User mCurrentUser;
    protected ArrayList<User> mFriends = new ArrayList<>();
    protected MenuItem mSendMenuItem;
    protected Uri mMediaUri;
    protected String mFileType;
    protected GridView mGridView;
    Button mSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.user_grid);
// Show the Up button in the action bar.
        setupActionBar();
//Set up the grid view
        mGridView = (GridView) findViewById(R.id.friendsGrid);
        mGridView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mGridView.setOnItemClickListener(mOnItemClickListener);
        TextView emptyTextView = (TextView) findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);
//Get access to the media URI (camera), the file type and the list of friends to mSend the message.
        mMediaUri = getIntent().getData();
        mFileType = getIntent().getExtras().getString(Message.KEY_FILE_TYPE);
        mFriends = getIntent().getParcelableArrayListExtra("FRIEND_LIST");

        Log.d(TAG,"Media URI is: "+mMediaUri);
        Log.d(TAG,"File type is: "+mFileType);
    }

    @Override
    public void onResume() {
        Log.d(TAG,"onResume");
        super.onResume();
        setProgressBarIndeterminateVisibility(true);

//Set the mSend button.
        mSend = (Button) findViewById(R.id.send_btn);
        mSend.setVisibility(View.INVISIBLE);

//Get the current user (loged in user) and the relation with the other users.
        mCurrentUser = User.getCurrentUser();
        Relation relation = new Relation();

//Create a query of users but get the query from relations, which excludes the current user.
        Query<User> query = relation.getQuery();
        query.addAscendingOrder(User.KEY_USERNAME);
        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> friends, Exception e) {
                setProgressBarIndeterminateVisibility(false);

                if (e == null) {
//Log the list of friends.
                    String[] friendsnames = new String[mFriends.size()];
                    int i = 0;
                    for (User user : mFriends) {
                        friendsnames[i] = user.getUsername();
                        Log.d(TAG,"Friends name: "+friendsnames[i]);
                        i++;
                    }
//Set the user adapter with the list of friends.
                    if (mGridView.getAdapter() == null) {
                        Log.d(TAG, "mFriends size is : "+ mFriends.size());
                        UserAdapter adapter = new UserAdapter(RecipientsActivity.this, mFriends);
                        mGridView.setAdapter(adapter);
                    } else {
                        ((UserAdapter) mGridView.getAdapter()).refill(mFriends);
                    }
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
//Set the action when mSend button is pressed.
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//Create the message.
                Message message = createMessage();
                if (message == null) {
                    // error
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(R.string.error_selecting_file)
                            .setTitle(R.string.error_selecting_file_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
//Send the message.
                    send(message);
//Finish this activity.
                    finish();
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
//This menu is not used as it is replaced with a mSend button at the bottom of the layout.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG,"onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.recipients, menu);
        mSendMenuItem = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG,"onOptionsItemSelected");
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
//This menu is not used as it is replaced with a mSend button at the bottom of the layout.
            case R.id.action_send:
                Message message = createMessage();
                if (message == null) {
                    // error
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.error_selecting_file)
                            .setTitle(R.string.error_selecting_file_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    send(message);
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected Message createMessage() {
//Create a message object and put the mSend id, sender name, recipient id and file type in it.
        Message message = new Message(Message.class.getSimpleName());
        message.put(Message.KEY_SENDER_ID, User.getCurrentUser().getObjectId());
        message.put(Message.KEY_SENDER_NAME, User.getCurrentUser().getUsername());
        message.put(Message.KEY_RECIPIENT_IDS, getRecipientIds());
        message.put(Message.KEY_FILE_TYPE, mFileType);
//Get the media file URI to be sent.
        byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);
        Log.i(TAG, String.valueOf(mMediaUri));

        if (fileBytes == null) {
            return null;
        } else {
//Reduce the file size to be sent.
            if (mFileType.equals(Message.TYPE_IMAGE)) {
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            }
//Create a message file object and put it in the message object.
            String fileName = FileHelper.getFileName(this, mMediaUri, mFileType);
            MessageFile file = new MessageFile(fileName, fileBytes, mMediaUri);
            message.put(Message.KEY_FILE, file);
//Return the message obeject created.
            return message;
        }
    }

    protected ArrayList<String> getRecipientIds() {
//Create a list of recipients
        ArrayList<String> recipientIds = new ArrayList<String>();
        for (int i = 0; i < mGridView.getCount(); i++) {
//If grid of the recipient is checked add to the recipient list
            if (mGridView.isItemChecked(i)) {
//                recipientIds.add(mFriends.get(i).getObjectId());      //<--Can't get object id from in this class for some reason. getObjectId() is always null. getting object id from mFriends in EditFriendsActivity is successfull though.
                recipientIds.add(mFriends.get(i).getUsername());        //<--Workaround to create a message, get user name instead of id.
            }
        }
//Return the recipient list
        return recipientIds;
    }
//This method pretends to mSend a message
    protected void send(Message message) {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(Exception e) {
/*When the call back response is ok display a toast that the message is sent.
Otherwise display a error alert.*/
                if (e == null) {
                    // success!
                    Toast.makeText(RecipientsActivity.this, R.string.success_message, Toast.LENGTH_LONG).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(R.string.error_sending_message)
                            .setTitle(R.string.error_selecting_file_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    protected OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//Inflate the check image view.
            ImageView checkImageView = (ImageView) view.findViewById(R.id.checkImageView);
//If at least one grid is checked display the mSend button.
            if (mGridView.getCheckedItemCount() > 0) {
//                mSendMenuItem.setVisible(true);   //<--replaced with mSend button below.
                mSend.setVisibility(View.VISIBLE);
            } else {
//                mSendMenuItem.setVisible(false);  //<--replaced with mSend button below.
                mSend.setVisibility(View.INVISIBLE);
            }
//Set check image view visible on the grid checked.
            if (mGridView.isItemChecked(position)) {
                checkImageView.setVisibility(View.VISIBLE);
            } else {
                checkImageView.setVisibility(View.INVISIBLE);
            }
        }
    };
}






