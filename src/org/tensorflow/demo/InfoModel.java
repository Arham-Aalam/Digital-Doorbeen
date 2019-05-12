package org.tensorflow.demo;

/**
 * Created by Arham on 29/04/2019.
 */

public class InfoModel {
    String title, extracter;

    public InfoModel(String title, String extracter) {
        this.title = title;
        this.extracter = extracter;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExtracter() {
        return extracter;
    }

    public void setExtracter(String extracter) {
        this.extracter = extracter;
    }
}
