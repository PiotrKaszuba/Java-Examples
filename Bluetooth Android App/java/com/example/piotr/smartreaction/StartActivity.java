package com.example.piotr.smartreaction;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;




public class StartActivity extends AppCompatActivity {

    private BluetoothSocket socket;
    private OutputStream out;
    private InputStream in;

    private TextView info_test;
    private TextView finish_text;
    private TextView check_text;
    private Button finish_button;
    private TextView mid_text;
    private Button mid_button;
    private Button go;
    private Button finish_test;
    private RadioButton only_reac;
    private RadioButton finish;
    private RadioButton mid;
    private MyApplication x;

    public void receive()
    {

        Intent inte = new Intent(StartActivity.this, MyService.class);

        StartActivity.this.startService(inte);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        x = (MyApplication)getApplication();


        info_test = (TextView) findViewById(R.id.textView6);
        finish_text = (TextView) findViewById(R.id.run_text);
        check_text = (TextView) findViewById(R.id.textView7);
        finish_button= (Button) findViewById(R.id.finish_test_button);
        mid_text = (TextView) findViewById(R.id.mid_text);
        mid_button= (Button) findViewById(R.id.mid_test_button);
        go = (Button) findViewById(R.id.button2);
        finish_test = (Button) findViewById(R.id.finish_test_button);
        only_reac = (RadioButton) findViewById(R.id.only_reaction_button);
        finish = (RadioButton) findViewById(R.id.finish_button);
        mid = (RadioButton) findViewById(R.id.midgate_button);

        MyReceiver my_rec = new MyReceiver(this);
        IntentFilter filter = new IntentFilter(MyReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(my_rec, filter);

       switch(x.get_check()){
           case 0:
               only_reac.setChecked(true);
               break;
           case 1:
               finish.setChecked(true);
               break;
           case 2:
               mid.setChecked(true);
               break;
       }

        if (finish.isChecked())
        {



            finish_text.setVisibility(View.VISIBLE);
            finish_button.setVisibility(View.VISIBLE);
            info_test.setVisibility(View.VISIBLE);
        }
        if (mid.isChecked())
        {

            finish_text.setVisibility(View.VISIBLE);
            mid_text.setVisibility(View.VISIBLE);
            finish_button.setVisibility(View.VISIBLE);
            mid_button.setVisibility(View.VISIBLE);
            info_test.setVisibility(View.VISIBLE);
        }



        go.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                try {

                   if(only_reac.isChecked())
                       x.write("r");
                   else
                        if(finish.isChecked())
                            x.write("s");
                        else
                            if(mid.isChecked())
                                x.write("a");




                    check_text.setText("");
                    receive();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });



        finish_test.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                try {
                    x.write("c");

                    check_text.setText("");
                    receive();

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });


        mid_button.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                try {
                    x.write("d");

                    check_text.setText("");
                    receive();

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });





        only_reac.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                    finish_text.setVisibility(View.INVISIBLE);
                    mid_text.setVisibility(View.INVISIBLE);
                    finish_button.setVisibility(View.INVISIBLE);
                    mid_button.setVisibility(View.INVISIBLE);
                    info_test.setVisibility(View.INVISIBLE);
                    x.set_check((char)0);





            }
        });




        finish.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {



                finish_text.setVisibility(View.VISIBLE);
                mid_text.setVisibility(View.INVISIBLE);
                finish_button.setVisibility(View.VISIBLE);
                mid_button.setVisibility(View.INVISIBLE);
                info_test.setVisibility(View.VISIBLE);
                x.set_check((char)1);




            }
        });


        mid.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {



                finish_text.setVisibility(View.VISIBLE);
                mid_text.setVisibility(View.VISIBLE);
                finish_button.setVisibility(View.VISIBLE);
                mid_button.setVisibility(View.VISIBLE);
                info_test.setVisibility(View.VISIBLE);
                x.set_check((char)2);




            }
        });





    }


}
