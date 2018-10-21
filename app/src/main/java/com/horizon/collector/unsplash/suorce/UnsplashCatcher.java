
package com.horizon.collector.unsplash.suorce;


import android.text.TextUtils;

import com.horizon.collector.common.http.HttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UnsplashCatcher {

    public static List<UPhoto> getPhotos(String keyword, int page) throws IOException {
        List<UPhoto> list = new ArrayList<>(100);
        int start = 10 * (page -1);
        int end = 10 * page;
        for(int i = start + 1; i <= end; i++ ){
            String url = "https://api.unsplash.com/search/photos" +
                    "?client_id=fa60305aa82e74134cabc7093ef54c8e2c370c47e73152f72371c828daedfcd7" +
                    "&per_page=20&page=" + i + "&query=" + keyword;
            String content = HttpClient.request(url);
            list.addAll(extractPhotos(content));
        }
        return list;
    }

    private static List<UPhoto> extractPhotos(String str) {
        List<UPhoto> photoList = new ArrayList<>(30);

        int start, end = 0;
        while (true) {
            start = str.indexOf("id\":", end);
            if (start < 0) break;
            end = str.indexOf('"', start + 5);
            String id = str.substring(start + 5, end);

            start = str.indexOf("width", end + 10);
            if (start < 0) break;
            end = str.indexOf(',', start + 7);
            String width = str.substring(start + 7, end);

            start = str.indexOf('h', end);
            if (start < 0) break;
            end = str.indexOf(',', start + 8);
            String height = str.substring(start + 8, end);

            start = str.indexOf("raw\":", end + 10);
            if (start < 0) break;
            start = str.indexOf("com/", start);
            if (start < 0) break;
            end = str.indexOf('"', start + 4);
            String name = str.substring(start + 4, end);

            if (isInteger(width) && isInteger(height)) {
                UPhoto photoVo = new UPhoto(id, Integer.parseInt(width), Integer.parseInt(height), name);
                photoList.add(photoVo);
            }

            end += 300;
        }

        return photoList;
    }

    private static boolean isInteger(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        int n = str.length();
        for (int i = 0; i < n; i++) {
            char ch = str.charAt(i);
            if (ch < '0' || ch > '9') {
                return false;
            }
        }
        return true;
    }
}
