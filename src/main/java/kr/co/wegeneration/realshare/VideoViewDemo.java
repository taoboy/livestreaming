package kr.co.wegeneration.realshare;

/**
 * Created by User on 2015-09-28.
 */
/*
 * Copyright (C) 2013 yixia.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import io.vov.vitamio.MediaMetadataRetriever;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import android.graphics.Bitmap;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import kr.co.wegeneration.realshare.NetController.ParseNetController;
import kr.co.wegeneration.realshare.chat.ChatListAdapter;
import kr.co.wegeneration.realshare.common.Define;

import android.content.Context;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class VideoViewDemo extends ListActivity {

    private static final String FIREBASE_URL = "https://liveo-27.firebaseio.com";
    private Firebase mFirebaseRef;
    public static Context thisContext;
    private String path = "";
    private String roomId = "";
    private ChatListAdapter mChatListAdapter;
    private ValueEventListener mConnectedListener;
    private VideoView mVideoView;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        if (!LibsChecker.checkVitamioLibs(this))
            return;
        thisContext = this;

        Log.d("VideoPath : ", path);
        io.vov.vitamio.MediaMetadataRetriever retriever = new io.vov.vitamio.MediaMetadataRetriever(this);

        setContentView(R.layout.activity_videoview);
        mVideoView = (VideoView) findViewById(R.id.surface_view);


        Intent intent = getIntent();
        if( intent != null ) {
            path           = intent.getStringExtra(Define.VIDEO_PATH);
            roomId         =  intent.getStringExtra(Define.ROOM_ID);
        }


        if (path == "") {
            // Tell the user to provide a media file URL/path.
            Toast.makeText(VideoViewDemo.this, "Video Path is not existed", Toast.LENGTH_LONG).show();
            finish();
            return;
        } else {


            mVideoView.setVideoPath(path);
            mVideoView.setMediaController(new MediaController(this));
            mVideoView.requestFocus();
            mVideoView.setVideoQuality(4);
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    // optional need Vitamio 4.0
                    mediaPlayer.setPlaybackSpeed(1.0f);

                }
            });
            mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {

                    return false;
                }
            });
            mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {


                }
            });

            Firebase.setAndroidContext(this);
            mFirebaseRef         = new Firebase(FIREBASE_URL).child("chat").child(roomId).child("messageList");

            final ListView listView = getListView();
            // Tell our list adapter that we only want 50 messages at a time
            mChatListAdapter = new ChatListAdapter(mFirebaseRef.limit(50), this, R.layout.chat_message, roomId, getAssets());
            listView.setSelector(R.drawable.list_selector);
            //listView.setClickable(false);
            listView.setAdapter(mChatListAdapter);

            mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    listView.setSelection(mChatListAdapter.getCount() - 1);
                }
            });


            // Finally, a little indication of connection status
            mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean connected = (Boolean) dataSnapshot.getValue();
                    if (connected) {
                        Toast.makeText(VideoViewDemo.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(VideoViewDemo.this, "Disconnected from Firebase", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    // No-op
                }
            });

        }

    }


    @Override
    public void onDestroy() {
        Log.d("VideoView", "onDestroy");
        super.onDestroy();
        overridePendingTransition(R.anim.animation_on, R.anim.animation_off);
        DisconnectFirebase();
        mVideoView =null;
    }

    @Override
    public void onPause(){
        super.onPause();
        ParseNetController.ActiveOnOff(thisContext, "n");
    }

    @Override
    public void onResume(){
        super.onResume();
        ParseNetController.ActiveOnOff(thisContext, "y");
    }

    private void DisconnectFirebase(){

        mChatListAdapter.notifyDataSetChanged();
        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mChatListAdapter.cleanup();
    }
}