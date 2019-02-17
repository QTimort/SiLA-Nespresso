package fr.diguiet.nespresso.server.nespresso.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
class Image {
    @SerializedName("altText")
    @Expose
    private Object altText;
    @SerializedName("url")
    @Expose
    private String url;
}