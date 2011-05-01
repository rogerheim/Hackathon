package com.hackathon;

public interface Command {
    public void execute();

    public static final Command NOOP = new Command() {
        @Override
        public void execute() {}
    };
}
