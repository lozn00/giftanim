package cn.qssq666.giftanim.bean;

/**
 * Created by 情随事迁(qssq666@foxmail.com) on 2017/4/20.
 */

public class GiftModel {
    public GiftModel(int money, String img) {
        this.money = money;
        this.image = img;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }


    public String getImage() {
        return image;
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
    public String image;
    public String unit;

    public String getUnit() {
        return unit;
    }

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

        GiftModel giftModel = (GiftModel) o;

        return id == giftModel.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
