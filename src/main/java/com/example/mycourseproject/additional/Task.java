package com.example.mycourseproject.additional;

import lombok.Data;


public class Task {
    private int priceA;
    private int priceB;
    private ModuleListWrapper listWrapper;

    public Task() {
    }

    public Task(int priceA, int priceB, ModuleListWrapper listWrapper) {
        this.priceA = priceA;
        this.priceB = priceB;
        this.listWrapper = listWrapper;
    }

    public int getPriceA() {
        return priceA;
    }

    public void setPriceA(int priceA) {
        this.priceA = priceA;
    }

    public int getPriceB() {
        return priceB;
    }

    public void setPriceB(int priceB) {
        this.priceB = priceB;
    }

    public ModuleListWrapper getListWrapper() {
        return listWrapper;
    }

    public void setListWrapper(ModuleListWrapper listWrapper) {
        this.listWrapper = listWrapper;
    }
}
