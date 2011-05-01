package com.hackathon;

import android.util.Log;
import android.view.View;


public class OnViewClickListenerWrapper implements View.OnClickListener {
    private Command command;

    public OnViewClickListenerWrapper(Command command) {
        this.command = command;
    }

    @Override
    public void onClick(View view) {
        //  view is the button
        Log.i("VideoPlayList", "OnViewClickListenerWrapper view=" + view.toString());
        command.execute();
    }
}
