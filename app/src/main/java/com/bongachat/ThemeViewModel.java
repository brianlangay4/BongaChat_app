package com.bongachat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ThemeViewModel extends ViewModel {

    private MutableLiveData<String> selectedThemeLiveData = new MutableLiveData<>();

    public LiveData<String> getSelectedThemeLiveData() {
        return selectedThemeLiveData;
    }

    public void setSelectedTheme(String theme) {
        selectedThemeLiveData.setValue(theme);
    }
}
