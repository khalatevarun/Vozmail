package com.example.android.vozmail.api.model;

import com.squareup.moshi.Json;

public class labelName {

    @Json(name = "name")
    String name;

    public String getName() {
        return name;
    }
}
