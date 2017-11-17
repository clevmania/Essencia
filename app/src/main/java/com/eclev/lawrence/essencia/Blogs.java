package com.eclev.lawrence.essencia;

/**
 * Created by SYSTEM on 9/27/2017.
 */

public class Blogs {
    private String title;
    private String desc;
    private String imaage;

    public Blogs(){

    }

    public Blogs(String title, String desc, String imaage) {
        this.title = title;
        this.desc = desc;
        this.imaage = imaage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImaage() {
        return imaage;
    }

    public void setImaage(String imaage) {
        this.imaage = imaage;
    }
}
