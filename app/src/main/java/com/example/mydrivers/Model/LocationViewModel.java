package com.example.mydrivers.Model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LocationViewModel extends ViewModel {
    private final MutableLiveData<String> location = new MutableLiveData<>();

    public void setLocation(String location) {
        this.location.setValue(location);
    }

    public MutableLiveData<String> getLocation() {
        return location;
    }
}
