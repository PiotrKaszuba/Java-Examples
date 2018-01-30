package com.example.piotr.smartreaction;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

/**
 * Created by Piotr on 2017-05-30.
 */
public class MyReceiver extends BroadcastReceiver{

    private Activity act;
    public static final String ACTION_RESP =
            "Message";
    public MyReceiver(Activity x)
    {
        super();
        act = x;

    }




    public void another_receive()
    {

        Intent inte = new Intent(act, MyService.class);
        act.startService(inte);
    }




    public float reaction_decode(float b, float c)
    {
        return b/1000+c/10;
    }



    public float run_decode(float b, float c)
    {
        return b/100+c;
    }
    @Override
    public void onReceive(Context context, Intent intent) {

        MyApplication x = (MyApplication)act.getApplication();





        if(x.get_a() == 3 || x.get_a() == 4)
        {


            TextView text = (TextView) act.findViewById(R.id.reaction_text);
            text.setText("Reaction time: " + reaction_decode(x.get_b(),x.get_c()));

            if(x.get_a() == 4)
                another_receive();
        }
        else
        if(x.get_a() == 1)
        {

            TextView text = (TextView) act.findViewById(R.id.run_text);
            text.setText("Run time: " + run_decode(x.get_b(),x.get_c()));
        }
        else
        if(x.get_a() == 5)
        {

            TextView text = (TextView) act.findViewById(R.id.mid_text);
            text.setText("Mid time: " + run_decode(x.get_b(),x.get_c()));
            another_receive();
        }
        else
        if(x.get_a() == 2)
        {
            TextView text = (TextView) act.findViewById(R.id.textView7);
            float value = run_decode(x.get_b(),x.get_c());
            if(value < 0.2)
                text.setText("Too fast, photocells may not see each other");
            else
                if(value >= 10)
                    text.setText("Cannot detect passing, radio or photocell might not work");

                else
                    text.setText("Photocell detection is OK");

        }
        else
        if(x.get_a() == (int) 'F')
        {
            TextView text = (TextView) act.findViewById(R.id.reaction_text);
            text.setText("Reaction time: FALSE START! -" + reaction_decode(x.get_b(),x.get_c()));
        }

    }
}
