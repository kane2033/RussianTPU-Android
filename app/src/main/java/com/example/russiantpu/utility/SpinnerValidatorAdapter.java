package com.example.russiantpu.utility;


import android.widget.Spinner;

import com.mobsandgeeks.saripaar.adapter.ViewDataAdapter;

public class SpinnerValidatorAdapter implements ViewDataAdapter<Spinner,String> {
    @Override
    public String getData(Spinner view) {
        return view.getSelectedItem() != null ? view.getSelectedItem().toString() : "";
    }
}