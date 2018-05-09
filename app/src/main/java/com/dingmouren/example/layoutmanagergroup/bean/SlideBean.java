package com.dingmouren.example.layoutmanagergroup.bean;


/**
 * Created by 钉某人
 * github: https://github.com/DingMouRen
 * email: naildingmouren@gmail.com
 */


public class SlideBean {
    private int mItemBg;
    private String mTitle;
    private int mUserIcon;
    private String mUserSay;

    public SlideBean(int mItemBg, String mTitle, int mUserIcon, String mUserSay) {
        this.mItemBg = mItemBg;
        this.mTitle = mTitle;
        this.mUserIcon = mUserIcon;
        this.mUserSay = mUserSay;
    }

    public int getItemBg() {
        return mItemBg;
    }

    public void setItemBg(int mItemBg) {
        this.mItemBg = mItemBg;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public int getUserIcon() {
        return mUserIcon;
    }

    public void setUserIcon(int mUserIcon) {
        this.mUserIcon = mUserIcon;
    }

    public String getUserSay() {
        return mUserSay;
    }

    public void setUserSay(String mUserSay) {
        this.mUserSay = mUserSay;
    }
}
