package com.teamtreehouse.ribbit.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.teamtreehouse.ribbit.R;
import com.teamtreehouse.ribbit.adapters.UserAdapter;
import com.teamtreehouse.ribbit.models.User;

import java.util.ArrayList;

/*This class is called from message activity to handle the selection of recipient's emails to
* put in the mEmailEditText edit mTextEditText of the message.*/

public class RecipientEmailActivity extends Activity {

    private static final String TAG = RecipientEmailActivity.class.getSimpleName() ;
    protected GridView mGridView;
    Button mConfirm;
    protected ArrayList<User> mFriends = new ArrayList<>();
    String mEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_friends);
//Get the list of friends.
        mFriends = getIntent().getParcelableArrayListExtra("FRIEND_LIST");
//Make the adapter to use.
        UserAdapter adapter = new UserAdapter(RecipientEmailActivity.this, mFriends);
//Set layout, grid and confirm button in the layout.
        mGridView = (GridView) findViewById(R.id.friendsGrid);
        mConfirm = (Button) findViewById(R.id.confirm_button);
        TextView emptyTextView = (TextView) findViewById(android.R.id.empty);
        mGridView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mGridView.setOnItemClickListener(mOnItemClickListener);
        mGridView.setEmptyView(emptyTextView);
        mGridView.setAdapter(adapter);
/*If the list of friends is not empty mSend the recipient mEmailEditText to message activity.
Otherwise set the confirm button visibility to invisible.*/
        if(mFriends.size()!=0) {
            mConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putExtra("RECIPIENT_EMAIL", mEmail);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }else{
            mConfirm.setVisibility(View.INVISIBLE);
        }
    }

    protected AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            String[]mEmail = new String[mFriends.size()];
            if (mGridView.getCheckedItemCount() > 0){
                mConfirm.setVisibility(View.VISIBLE);
            }else{
                mConfirm.setVisibility(View.INVISIBLE);
            }
                ImageView checkImageView = (ImageView) view.findViewById(R.id.checkImageView);

            if (mGridView.isItemChecked(position)) {
                // add the recipient
                checkImageView.setVisibility(View.VISIBLE);
//                for(int index =0 ; index<=mFriends.size()-1;index++){
//                    mEmail[index] =(mFriends.get(position).getEmail());
                mEmail = mFriends.get(position).getEmail();
//                }
//                Log.d(TAG,"Email: "+ Arrays.toString(mEmail));
            } else {
                // remove the recipient
                checkImageView.setVisibility(View.INVISIBLE);
//                for(int index =0 ; index<=mFriends.size()-1;index++){
//                    mEmail[index] =(mFriends.get(position).getEmail());
                mEmail = mFriends.get(position).getEmail();
//                }
            }
        }
    };
}
