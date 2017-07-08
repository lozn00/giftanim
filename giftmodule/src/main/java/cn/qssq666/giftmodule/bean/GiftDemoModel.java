package cn.qssq666.giftmodule.bean;


import cn.qssq666.giftmodule.interfacei.GiftModelI;

/**
 * Created by 情随事迁(qssq666@foxmail.com) on 2017/4/20.
 */

public class GiftDemoModel implements GiftModelI {
    public GiftDemoModel() {
    }

    public GiftDemoModel(String image) {
        this.image = image;
    }

    public GiftDemoModel(int money, String img) {
        this.money = money;
        this.image = img;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }


    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String getGiftImage() {
        return getImage();
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int money;
    public String title;
    public int id;

    public String getImage() {
        return image;
    }

    public String image;
    public String unit;

    public String getUnit() {
        return unit;
    }

    public int getShowcount() {
        return showcount;
    }

    public void setShowcount(int showcount) {
        this.showcount = showcount;
    }

    public int showcount;

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "GiftModel{" +
                "money=" + money +
                ", title='" + title + '\'' +
                ", id=" + id +
                ", image='" + image + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GiftDemoModel giftModel = (GiftDemoModel) o;

        return id == giftModel.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
