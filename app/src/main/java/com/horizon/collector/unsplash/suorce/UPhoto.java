
package com.horizon.collector.unsplash.suorce;


public class UPhoto {
    private static final String BASE_URL = "https://images.unsplash.com/";
    private static final String COMMON_PARAMS = "?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&fit=max&w=";

    public final String id;
    private final String rawURL;
    public final int width;
    public final int height;
    //public final String name;

    public UPhoto(String id, int width, int height, String name) {
        this.id = id;
        this.rawURL = BASE_URL + name;
        this.width = width;
        this.height = height;
        //this.name = name;
    }

    public String getRawURL() {
        return rawURL;
    }

    public String getPhotoURL(int width) {
        return rawURL + COMMON_PARAMS + width;
    }


}
