package com.example.android.vozmail.api.model;


import com.squareup.moshi.Json;

public class Labels {

    @Json(name = "labels")
    labelName labelName;


    public String getLabelname() {
        return labelName.getName();
    }
}
