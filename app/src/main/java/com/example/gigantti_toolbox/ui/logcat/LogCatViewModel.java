package com.example.gigantti_toolbox.ui.logcat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LogCatViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public LogCatViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}