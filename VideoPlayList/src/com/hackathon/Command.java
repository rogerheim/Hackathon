package com.hackathon;

import android.view.View;

public interface Command {
    public void execute(View view, Object data);

    public static final Command NOOP = new Command() {
//        @Override
        public void execute(View view, Object data) {}
    };
}
