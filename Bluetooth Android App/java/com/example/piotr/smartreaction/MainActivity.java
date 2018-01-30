package com.example.piotr.smartreaction;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import android.widget.AdapterView;
import android.os.ParcelUuid;



public class MainActivity extends AppCompatActivity {

    private BluetoothSocket socket;
    private List<BluetoothDevice> dev;
    private TextView err;
    private ImageButton arrow;
    private MyApplication x;
    public void choose() {
        final ListView list = (ListView) findViewById(R.id.listView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
                err.setText("");

                ParcelUuid[] uuids = dev.get(index).getUuids();
                try {
                    if(x.get_socket()!=null) {
                        x.get_socket().close();
                        arrow.setVisibility(View.INVISIBLE);
                    }
                    socket = dev.get(index).createRfcommSocketToServiceRecord(uuids[0].getUuid());

                    x.set_socket(socket);
                    x.get_socket().connect();

                    x.set_in(x.get_socket().getInputStream());
                    x.set_out(x.get_socket().getOutputStream());

                    err.setText("Connection established with " + dev.get(index).getName());
                    arrow.setVisibility(View.VISIBLE);
                }
                catch (IOException e)
                {
                   err.setText("Connection error. Make sure you chose appropriate device and its pairing.");
                }


            }
        });
    }








    private void refresh_button()
    {
        final Button clickButton = (Button) findViewById(R.id.button);
        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MyApplication x = (MyApplication)getApplication();
                err.setText("");
                try{
                    if(x.get_socket() != null)
                    x.get_socket().close();
                    arrow.setVisibility(View.INVISIBLE);
                    init();

                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
                init_list();




            }
        });




       arrow.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                    try {
                        x.write("i");
                        Intent myIntent = new Intent(MainActivity.this, StartActivity.class);
                        MainActivity.this.startActivity(myIntent);
                    }
                    catch(IOException e){
                        err.setText("Communication error. Refresh and try again.");
                        arrow.setVisibility(View.INVISIBLE);
                        e.printStackTrace();

                    }




            }
        });
    }


    private void init() throws IOException {
        dev = new ArrayList<BluetoothDevice>();

        BluetoothAdapter bluadapt = BluetoothAdapter.getDefaultAdapter();
        if(bluadapt != null) {
            if(bluadapt.isEnabled()){
               Set<BluetoothDevice> bondedDevice = bluadapt.getBondedDevices();
                Iterator<BluetoothDevice> it = bondedDevice.iterator();
                if(bondedDevice.size() > 0 ) {

                    while(it.hasNext()) {
                       dev.add(it.next());

                   }


                }
                else
                err.setText("No devices paired.");
            }
            else
                err.setText("Bluetooth is disabled");
        }

    }

    private void init_list()
    {
        ListView list;
        ArrayAdapter<String> adapter;

        list = (ListView) findViewById(R.id.listView);

        String devices[];

        ArrayList<String> devs = new ArrayList<String>();
        Iterator<BluetoothDevice> it = dev.iterator();
        while(it.hasNext())
            devs.add(it.next().getName());



        adapter = new ArrayAdapter<String>(this, R.layout.layout, devs);

        list.setAdapter(adapter);


    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        x = (MyApplication)getApplication();









        arrow = (ImageButton) findViewById(R.id.imageButton);
        err = (TextView) findViewById(R.id.textView2);
        err.setText("");



        refresh_button();
        choose();
        try{
            init();
            if(x.get_out() != null)x.write("z");
            else  arrow.setVisibility(View.INVISIBLE);
        }
        catch(IOException e)
        {
            arrow.setVisibility(View.INVISIBLE);
            e.printStackTrace();
        }


        init_list();


    }
}
