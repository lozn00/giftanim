package cn.qssq666.giftmodule.bean;


import cn.qssq666.giftmodule.interfacei.UserInfoI;

/**
 * Created by 情随事迁(qssq666@foxmail.com) on 2017/4/20.
 */

public class UserDemoInfo implements UserInfoI {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    String userId;

    public String getFace() {
        return portraitUri;
    }

    public void setPortraitUri(String portraitUri) {
        this.portraitUri = portraitUri;
    }

    String portraitUri;
}
