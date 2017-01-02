package kr.co.wegeneration.realshare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;
/**
 * Created by User on 2015-09-24.
 */
public class NetworkReceiver extends BroadcastReceiver
{

    final String TAG= "NetworkReceiver.java";

    @Override
    public void onReceive(Context context, Intent intent){
        String IntentAction = intent.getAction();
        Log.i(TAG, "Intent Action ==" +IntentAction);

        if(IntentAction.equals(ConnectivityManager.CONNECTIVITY_ACTION) || IntentAction.equals(WifiManager.WIFI_STATE_CHANGED_ACTION))
        {
            int iResult = getConnectivityStatus(context);
            if(iResult== 2)
            {
                Toast.makeText(context, "Mobile Connect", Toast.LENGTH_SHORT).show();
            }
            else if (iResult==1)
            {
                Toast.makeText(context, "Wifi Connect", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(context, "Not Connect", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public int getConnectivityStatus(Context context){
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activityNetwork = cm.getActiveNetworkInfo();
        if(null != activityNetwork){
            if(activityNetwork.getType() == ConnectivityManager.TYPE_WIFI)
            {
                if(activityNetwork.getState()== NetworkInfo.State.CONNECTED)
                {
                    return 1;
                }
                else if( activityNetwork.getState() == NetworkInfo.State.CONNECTING)
                {
                    Log.v(TAG, "Wifi Connecting");
                }
                else if( activityNetwork.getState() == NetworkInfo.State.DISCONNECTED)
                {
                    Log.v(TAG, "Wifi DISCONNECTED");
                }
                else if( activityNetwork.getState() == NetworkInfo.State.DISCONNECTING)
                {
                    Log.v(TAG, "Wifi SUSPENDED");
                }
                else if( activityNetwork.getState() == NetworkInfo.State.SUSPENDED)
                {
                    Log.v(TAG, "Wifi SUSPENDED");
                }
                else if( activityNetwork.getState() == NetworkInfo.State.UNKNOWN)
                {
                    Log.v(TAG, "Wifi UNKNOWN");
                }
                return 1;
            }

            if(activityNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                if(activityNetwork.getState()== NetworkInfo.State.CONNECTED)
                {
                    return 1;
                }
                else if( activityNetwork.getState() == NetworkInfo.State.CONNECTING)
                {
                    Log.v(TAG, "Mobile Connecting");
                }
                else if( activityNetwork.getState() == NetworkInfo.State.DISCONNECTED)
                {
                    Log.v(TAG, "Mobile DISCONNECTED");
                }
                else if( activityNetwork.getState() == NetworkInfo.State.DISCONNECTING)
                {
                    Log.v(TAG, "Mobile SUSPENDED");
                }
                else if( activityNetwork.getState() == NetworkInfo.State.SUSPENDED)
                {
                    Log.v(TAG, "Mobile SUSPENDED");
                }
                else if( activityNetwork.getState() == NetworkInfo.State.UNKNOWN)
                {
                    Log.v(TAG, "Mobile UNKNOWN");
                }
                return 2;
            }
        }
        else {}
        return 3;
    }
}
