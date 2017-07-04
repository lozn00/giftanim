package cn.qssq666.giftmodule.interfacei;

/**
 * Created by 情随事迁(qssq666@foxmail.com) on 2017/4/20.
 */

public interface GiftModelI {


    String getImage();

    int getId();


    String getTitle();


    String getUnit();

    /**
     * @return 如果一直返回0 代表内部维护 x1 x2但是这无法解决这个问题  ，比如ios那边不做这个 用户在x了20的情况下新用户依然不能连贯了因为对方是x20了你看到的还是x1，
     */
    int getShowcount();


}
