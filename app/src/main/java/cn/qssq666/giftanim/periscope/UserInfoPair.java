package cn.qssq666.giftanim.periscope;

import android.support.v4.util.Pair;
import android.util.Log;

import cn.qssq666.giftanim.bean.GiftModel;
import cn.qssq666.giftanim.interfacei.UserInfoI;


/**
 * Created by luozheng on 2016/5/3.  qssq.space
 * 和官方的区别在于 只比对第二个参数
 */
public class UserInfoPair extends Pair<UserInfoI, GiftModel> {


    private static final String TAG = "UserInfoPair";

    /**
     * Constructor for a Pair.
     *
     * @param first  the first object in the Pair
     * @param second the second object in the pair
     */
    public UserInfoPair(UserInfoI first, GiftModel second) {
        super(first, second);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserInfoPair)) {
            return false;
        }
        UserInfoPair pPair = (UserInfoPair) o;
        Log.i(TAG, "pPair:" + pPair + ",THIS:" + this);
        if (GiftAnimLayout.getKey(this.first, this.second).equalsIgnoreCase(GiftAnimLayout.getKey(pPair.first, pPair.second))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "UserInfoPair{}" + this.first.toString() + "," + this.second.toString();
    }

    public static UserInfoPair create(UserInfoI a, GiftModel b) {
        return new UserInfoPair(a, b);
    }

    @Override
    public int hashCode() {
        return this.first.getUserId().hashCode() + this.second.hashCode();
    }

}
