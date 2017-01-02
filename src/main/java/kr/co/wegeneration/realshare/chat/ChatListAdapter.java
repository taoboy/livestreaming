package kr.co.wegeneration.realshare.chat;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Query;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

import kr.co.wegeneration.realshare.R;
import kr.co.wegeneration.realshare.widget.MLRoundedImageView;

/**
 * @author greg
 * @since 6/21/13
 *
 * This class is an example of how to use FirebaseListAdapter. It uses the <code>Chat</code> class to encapsulate the
 * data for each individual chat message
 */
public class ChatListAdapter extends FirebaseListAdapter<Chat> {

    // The mUsername for this client. We use this to indicate which messages originated from this user
    private String mUsername;
    private AssetManager asset;

    ViewHolder holder;

    public ChatListAdapter(Query ref, Activity activity, int layout, String mUsername, AssetManager masset) {
        super(ref, Chat.class, layout, activity);
        this.asset     = masset;
        this.mUsername = mUsername;
    }


    /**
     * Bind an instance of the <code>Chat</code> class to our view. This method is called by <code>FirebaseListAdapter</code>
     * when there is a data change, and we are given an instance of a View that corresponds to the layout that we passed
     * to the constructor, as well as a single <code>Chat</code> instance that represents the current data to bind.
     *
     * @param view A view instance corresponding to the layout we passed to the constructor.
     * @param chat An instance representing the current state of a chat message
     */
    private boolean voiceplaying = false;
    private android.media.MediaPlayer mPlayer = null;



    android.media.MediaPlayer.OnCompletionListener mCompleteListener =
            new android.media.MediaPlayer.OnCompletionListener() {
                public void onCompletion(android.media.MediaPlayer arg0) {
                    Log.d("MediaPlayer", "Ended");
                    //holder.play.setBackgroundResource(R.drawable.bg_msg_from);
                    deletePlayer();
                }
            };


    public void deletePlayer() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void onPlayWeb(String url) {
        deletePlayer();
        LoadMedia(url);
        Log.d("ChatListAdapter", "Started_test : " + url );
    }

    public boolean LoadMedia(String filePath) {
        try {
            if(voiceplaying==false) {
                mPlayer = new android.media.MediaPlayer();

                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                //FileInputStream fis = new FileInputStream(filePath);
                //FileDescriptor fd = fis.getFD();
                mPlayer.setDataSource(filePath);
                //mPlayer.setDataSource(fd);
                mPlayer.setOnCompletionListener(mCompleteListener);
                mPlayer.prepare();
                mPlayer.start();
                //holder.play.setBackgroundResource(R.color.lblFromJoin);
                voiceplaying = true;
            }
            else{
                voiceplaying = false;
                //holder.play.setBackgroundResource(R.drawable.bg_msg_from);
                deletePlayer();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static class ViewHolder
    {
        TextView authorText;
        ImageView play;
        TextView msg;
    }

    @Override
    protected void populateView(View view, final Chat chat) {


        //if(view == null ){

            holder = new ViewHolder();

            view.setTag(holder);
            holder.authorText         = (TextView)view.findViewById(R.id.author);
            holder.play                = (ImageView)view.findViewById(R.id.playback);
            holder.msg                 = (TextView)view.findViewById(R.id.message);
/*        }
        else{
            holder = (ViewHolder)view.getTag();
        }*/

        // Map a Chat object to an entry in our listview
        Typeface typeface = Typeface.createFromAsset(asset, "BRI293.TTF");
        String author = chat.getAuthor();
        holder.authorText.setTypeface(typeface);
        holder.authorText.setText(author );
        holder.authorText.setTextColor(Color.WHITE);

        String MessageType = chat.getMessageType();

        if(MessageType.equals("Voice")){
            holder.play.setVisibility(View.VISIBLE);
            holder.play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPlayWeb(chat.getVoicePath());
                }
            });

            holder.authorText.setBackgroundResource(R.drawable.bg_msg_author);
            holder.msg.setVisibility(View.GONE);

        }
        else if(MessageType.equals("Event")){

            holder.msg.setText(chat.getMessage());
            holder.msg.setTypeface(typeface);
            holder.msg.setVisibility(View.VISIBLE);
            holder.authorText.setBackgroundResource(R.color.lblFromJoin);
            holder.msg.setBackgroundResource(R.color.lblFromJoin);
            holder.play.setVisibility(View.GONE);
        }
        else {
            holder.msg.setText(chat.getMessage());
            holder.msg.setTypeface(typeface);
            holder.msg.setVisibility(View.VISIBLE);
            holder.authorText.setBackgroundResource(R.drawable.bg_msg_author);
            holder.msg.setBackgroundResource(R.drawable.bg_msg_from);
            holder.play.setVisibility(View.GONE);
        }
    }
}
