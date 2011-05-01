package com.hackathon;

import android.util.Log;
import android.view.View;


public class OnViewClickListenerWrapper implements View.OnClickListener {
    private Command command;
    private Object data;

    public OnViewClickListenerWrapper(Command command) {
        this.command = command;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public void onClick(View view) {
        //  view is the button
//        Log.i("VideoPlayList", "OnViewClickListenerWrapper view=" + view.toString());
        command.execute(view, data);
    }
}
