/*
 *
 *                     .::::.
 *                   .::::::::.
 *                  :::::::::::  by qssq666@foxmail.com
 *              ..:::::::::::'
 *            '::::::::::::'
 *              .::::::::::
 *         '::::::::::::::..
 *              ..::::::::::::.
 *            ``::::::::::::::::
 *             ::::``:::::::::'        .:::.
 *            ::::'   ':::::'       .::::::::.
 *          .::::'      ::::     .:::::::'::::.
 *         .:::'       :::::  .:::::::::' ':::::.
 *        .::'        :::::.:::::::::'      ':::::.
 *       .::'         ::::::::::::::'         ``::::.
 *   ...:::           ::::::::::::'              ``::.
 *  ```` ':.          ':::::::::'                  ::::..
 *                     '.:::::'                    ':'````..
 *
 */

package cn.qssq666.giftanim;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.qssq666.giftmodule.R;
import cn.qssq666.giftmodule.bean.GiftDemoModel;
import cn.qssq666.giftmodule.interfacei.GiftModelI;
import cn.qssq666.giftmodule.interfacei.UserInfoI;
import cn.qssq666.giftmodule.periscope.GiftAnimLayout;
import cn.qssq666.giftmodule.ui.StrokeTextView;

/**
 * 不设置本方法则内部维护一系列
 * Created by qssq on 2017/7/7 qssq666@foxmail.com
 */

public class GiftBarAdapter implements GiftAnimLayout.GiftCallBack {


    private static final String TAG = "GiftBarAdapter";

    @Override
    public void onGiftAnimOver(GiftModelI giftModel) {
        ((GiftDemoModel) giftModel).setShowcount(0);//如果是0则会直接设置为1的
        Log.w(TAG, "onGiftAnimOver:" + giftModel);
    }

    @Override
    public void onConvertGiftAnim(GiftModelI giftModel) {
//                ((GiftModel) giftModel).setShowcount(1);
        Log.w(TAG, "onConvertGiftAnim:" + giftModel);

    }

    @Override
    public void onAddNewGift(GiftModelI giftModel) {
        Log.w(TAG, "onAddNewGift:" + giftModel);

    }

    @Override
    public void onAddWaitUnique(GiftModelI giftModel) {
        Log.w(TAG, "onAddWaitUnique:" + giftModel);

    }

    @Override
    public int onRequestShowGiftCount(GiftModelI modelI, StrokeTextView tvValue) {
        int showcount = ((GiftDemoModel) modelI).getShowcount();//如果一直返回0还是由内部支持
//                showcount = showcount == 0 ? tvValue.getValue() + 1 : showcount;
        Log.w(TAG, "onRequestShowGiftCount :showCount:" + showcount);
//                showcount = showcount == 0 ? tvValue.getValue() + 1 : showcount;
        return showcount;
    }

    @Override
    public ViewGroup getGiftLayout(GiftAnimLayout giftAnimLayout) {
        //返回null表示内部维护。
        return (ViewGroup) LayoutInflater.from(giftAnimLayout.getContext()).inflate(R.layout.view_live_gift_bar_prescro, giftAnimLayout, false);
    }

    /**
     * 返回false表明内部进行绑定图片 比如给礼物的动画支持
     *
     * @param userInfo
     * @param giftModel
     * @param giftHolder
     * @return
     */
    @Override
    public boolean onBindPic(UserInfoI userInfo, GiftModelI giftModel, GiftAnimLayout.GiftHolder giftHolder) {
        ImageLoader.getInstance().displayImage(userInfo.getFace(), giftHolder.ivFace);
        Uri uri = Uri.parse("" + giftModel.getGiftImage());
   /*
        com.facebook.drawee.view.SimpleDraweeView
                draweeView = (SimpleDraweeView) giftHolder.ivGift;
        draweeView.setAspectRatio(1);
        draweeView.setImageURI(uri);
*/


        SimpleDraweeView draweeView = (SimpleDraweeView) giftHolder.ivGift;
        draweeView.setAspectRatio(1);
        DraweeController draweeController =
                Fresco.newDraweeControllerBuilder()
                        .setUri(uri)
                        .setAutoPlayAnimations(true) // 设置加载图片完成后是否直接进行播放
                        .build();
        draweeView.setController(draweeController);
        return true;
    }
}
