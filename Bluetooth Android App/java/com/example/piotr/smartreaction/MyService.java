package com.example.piotr.smartreaction;

import android.app.IntentService;
import android.content.Intent;

import java.io.IOException;

/**
 * Created by Piotr on 2017-05-30.
 */
public class MyService extends IntentService {
    public MyService() {
        super("MyService");
    }

    int a;
    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();

         MyApplication x=(MyApplication)getApplication();

        try {
            while(true) {
                a = x.get_in().read();
                if (a == (int) 'x') {


                    x.set_a(x.get_in().read());
                    x.set_b(x.get_in().read());
                    x.set_c(x.get_in().read());
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(MyReceiver.ACTION_RESP);
                    broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    sendBroadcast(broadcastIntent);
                    return;
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
