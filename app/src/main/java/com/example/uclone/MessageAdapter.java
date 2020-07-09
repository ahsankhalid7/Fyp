package com.example.uclone;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    String seenStatus;
    ValueEventListener valueEventListener;
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.messages_item, viewGroup, false);

        mAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, final int i) {
        final String sender_id = mAuth.getUid();
        final Messages messages = userMessageList.get(i);
        final String fromId = messages.getFrom();


        if (fromId.equals(sender_id)) {
            messageViewHolder.shape.setBackgroundResource(R.drawable.message_shape);
            messageViewHolder.mLinearLayout.setGravity(Gravity.RIGHT);
            messageViewHolder.mText.setTextColor(Color.WHITE);
            messageViewHolder.mTime.setTextColor(Color.WHITE);

            messageViewHolder.mNew.setVisibility(View.VISIBLE);
            if ((userMessageList.size() - 1) == i && MessagingActivity.seenCheck) {

                messageViewHolder.mSatus.setVisibility(View.VISIBLE);
            }
            else
            {
                messageViewHolder.mSatus.setVisibility(View.GONE);
            }


        } else {

            //asdasd
            messageViewHolder.mSatus.setVisibility(View.GONE);
            messageViewHolder.shape.setBackgroundResource(R.drawable.messageshape);
            messageViewHolder.mLinearLayout.setGravity(Gravity.LEFT);
            messageViewHolder.mText.setTextColor(Color.BLACK);
            messageViewHolder.mNew.setVisibility(View.VISIBLE);
            messageViewHolder.mTime.setTextColor(Color.BLACK);
            messageViewHolder.mSatus.setVisibility(View.GONE);
        }

        if ((userMessageList.size() - 1) == i && MessagingActivity.writingCheck) {

            messageViewHolder.writingGif.setVisibility(View.VISIBLE);
        }
        else
        {
            messageViewHolder.writingGif.setVisibility(View.GONE);
        }


        messageViewHolder.mText.setText(messages.getMessage());
        Date date = new Date(messages.getTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
        sinceFriendDate = dateFormat.format(date);
        messageViewHolder.mTime.setText(sinceFriendDate);


//                Messageref.removeEventListener(valueEventListener);

    }


    @Override
    public int getItemCount() {
        return userMessageList.size();
    }

    private List<Messages> userMessageList;
    private FirebaseAuth mAuth;
    private String sinceFriendDate;
    private DatabaseReference Messageref;
    ///asdasdasda


    public MessageAdapter(List<Messages> userMessageList) {
        this.userMessageList = userMessageList;
    }



    public class MessageViewHolder extends RecyclerView.ViewHolder {

        private TextView mText, mTime, mSatus;
        private LinearLayout mLinearLayout, mNew, shape;
        private GifImageView writingGif;

        public MessageViewHolder(View view) {
            super(view);
            mText = view.findViewById(R.id.message_text);
            mLinearLayout = view.findViewById(R.id.text_layout);
            mSatus = view.findViewById(R.id.sms_status);
            mTime = view.findViewById(R.id.message_time);
            shape = view.findViewById(R.id.txt_shape);
            mNew = view.findViewById(R.id.new_layout);
            writingGif = view.findViewById(R.id.writing);
        }


    }


}


