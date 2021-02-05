package com.mrtecks.grocery.homePOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Cat {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("space")
    @Expose
    private Object space;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("tag")
    @Expose
    private String tag;
    @SerializedName("subcat")
    @Expose
    private List<Subcat> subcat = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Object getSpace() {
        return space;
    }

    public void setSpace(Object space) {
        this.space = space;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<Subcat> getSubcat() {
        return subcat;
    }

    public void setSubcat(List<Subcat> subcat) {
        this.subcat = subcat;
    }
}
