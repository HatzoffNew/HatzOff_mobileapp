package com.aliyahatzoff.Models;

public class Interest {
    String interest;
    boolean isselected;

    public Interest(String interest, boolean isselected) {
        this.interest = interest;
        this.isselected = isselected;
    }

    public String getInterest() {
        return interest;
    }

    public boolean isIsselected() {
        return isselected;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public void setIsselected(boolean isselected) {
        this.isselected = isselected;
    }
}
