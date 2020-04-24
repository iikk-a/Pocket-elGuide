package com.example.gigantti_toolbox.ui.SQLite;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SQLiteViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SQLiteViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}