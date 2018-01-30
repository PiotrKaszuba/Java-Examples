package com.example.piotr.smartreaction;

import android.app.Application;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Piotr on 2017-05-30.
 */
public class MyApplication extends Application {


    private OutputStream out;
    private InputStream in;
    private BluetoothSocket socket = null;
    private int a;
    private int b;
    private int c;
    private char check =1;
    public void set_a(int a) {this.a =a;}
    public void set_b(int a) {this.b =a;}
    public void set_c(int a) {this.c =a;}
    public void set_check(char a) {this.check =a;}
    public int get_a()
    {
        return a;
    }

    public int get_b()
    {
        return b;
    }

    public int get_c()
    {
        return c;
    }

    public char get_check()
    {
        return check;
    }

    public void set_socket(BluetoothSocket s){
        socket = s;
    }

    public void set_out(OutputStream o){
        out = o;
    }

    public void set_in(InputStream i){
       in = i;
    }
    public BluetoothSocket get_socket() {
        return socket;
    }

    public InputStream get_in() {
        return in;
    }
    public OutputStream get_out() {
        return out;
    }





    public void write(String s) throws IOException {
        out.write(s.getBytes());
    }
}