package com.example.tasks;

import org.springframework.stereotype.Component;

@Component
public class FlagHolder{
    private boolean flag = false;

    public void enableFlag() {
        this.flag = true;
    }

    public boolean isFlagEnabled() {
        return flag;
    }
}
