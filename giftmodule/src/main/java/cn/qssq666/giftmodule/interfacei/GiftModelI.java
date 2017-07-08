package cn.qssq666.giftmodule.interfacei;

/**
 * Created by 情随事迁(qssq666@foxmail.com) on 2017/4/20.
 */

public interface GiftModelI {
    /**
     * 为了和通用的图片adapter不冲突，改名字比较重要。 比如列表是image,而显示的时候咋需要image,就尴尬了。
     *
     * @return
     */

    String getGiftImage();

    int getId();


    String getTitle();


    String getUnit();

    /**
     * @return 如果一直返回0 代表内部维护 x1 x2但是这无法解决这个问题  ，比如ios那边不做这个 用户在x了20的情况下新用户依然不能连贯了因为对方是x20了你看到的还是x1，
     */
    int getShowcount();


}
