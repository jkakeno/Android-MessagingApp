package com.teamtreehouse.ribbit.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamtreehouse.ribbit.R;
import com.teamtreehouse.ribbit.models.Message;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*Elapse time format resources:
* https://stackoverflow.com/questions/2179644/how-to-calculate-elapsed-time-from-now-with-joda-time
* https://github.com/dlew/joda-time-android*/

public class MessageAdapter extends ArrayAdapter<Message>  {

    private static final String TAG = MessageAdapter.class.getSimpleName();
    protected Context mContext;
    protected ArrayList<Message> mMessages;

    public MessageAdapter(Context context, List<Message> messages) {
        super(context, R.layout.message_item, messages);
        mContext = context;

        // Create a full copy of mMessages
        mMessages = new ArrayList<Message>();
        for (Message msg : messages) {
            mMessages.add(msg);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
/*Commenting out the if statement fixes the scroll NullPointerException issue*/
//        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);
            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.messageIcon);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.senderLabel);
            holder.timeLabel = (TextView) convertView.findViewById(R.id.timeLabel);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }

        Message message = mMessages.get(position);
//Add elapse time formatted
        Date createdAt = message.getCreatedAt();
        long now = new Date().getTime();
        long created = createdAt.getTime();
        Period period = new Period(created,now);
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendDays().appendSuffix(" days ")
                .appendHours().appendSuffix(" hr ")
                .appendMinutes().appendSuffix(" min ")
                .appendSeconds().appendSuffix(" sec ago")
                .printZeroNever()
                .toFormatter();
        String elapsed = formatter.print(period);
        Log.d(TAG, elapsed);

        holder.timeLabel.setText(elapsed);

        if (message.getString(Message.KEY_FILE_TYPE).equals(Message.TYPE_IMAGE)) {
            holder.iconImageView.setImageResource(R.drawable.ic_picture);
        } else {
            holder.iconImageView.setImageResource(R.drawable.ic_video);
        }
        holder.nameLabel.setText(message.getString(Message.KEY_SENDER_NAME));

        return convertView;
    }


    private static class ViewHolder {
        ImageView iconImageView;
        TextView nameLabel;
        TextView timeLabel;
    }

    public void refill(List<Message> messages) {
        mMessages.clear();
        mMessages.addAll(messages);
        notifyDataSetChanged();
    }
}






