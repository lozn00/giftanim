package cn.qssq666.giftmodule.periscope;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LruCache;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

import cn.qssq666.giftmodule.R;
import cn.qssq666.giftmodule.interfacei.GiftModelI;
import cn.qssq666.giftmodule.interfacei.UserInfoI;
import cn.qssq666.giftmodule.ui.StrokeTextView;
import cn.qssq666.giftmodule.util.CountList;
import cn.qssq666.giftmodule.util.DensityUtil;

/**
 * Created by luozheng on 2016/4/13. qssq666.cn
 * <p/>
 * 貌似修复了   存在队列排序的bug依然会超出的bug以及显示重复的bug,   在排队 的队列取出后 就会取出总数如果大于1以设置的毫秒+1秒的方式重复x1 x2 x3  x4 如果是累加的动画，那么 会延迟8秒
 * 貌似有可能产生view没有移除的bug
 * <p>
 * 2017年4月20日 17:47:43
 * 由于我们的项目不再需要直播了，做个纪念送给大家，本代码也是隔了很多个月了，又问题的当然也可以提问 ，我会尽量帮助大家 解决使用过程中遇到的问题。
 * 当同一个礼物同一个userid送出去礼物就会x1 x2 x3
 */
public class GiftAnimLayout extends LinearLayout {
    private static final String TAG = "GiftAnimLayout";
    private ArrayList<ImageView> singInstanceImageView = new ArrayList<>();

    public void setShowDuration(int showDuration) {
        this.showDuration = showDuration;
    }

    /**
     * 由图片和id组成保证唯一 、3秒显示
     */
    public int showDuration = 4200;

    /**
     * @param marginTop 单位 dp
     */
    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }


    public int marginTop = 12;//dp

    /**
     * 单位 dp
     *
     * @param marginLeft
     */
    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
    }

    public int marginLeft = 10;//dp
    /**
     * 这是正在动画中处理的.
     */
    private int mMaxCacheViewCount = 10;
    private LruCache<String, View> atShowMaps = new LruCache<String, View>(mMaxCacheViewCount) {//正在显示的礼物队列
        @Override
        protected int sizeOf(String key, View value) {
            return super.sizeOf(key, value);//默认就是1
        }
    };
    /**
     * 这是新的动画产生了,但是当前超过了最大值 所存放的容器
     */
    // userInfo, giftModel
    private CountList<UserInfoPair> waitList = new CountList<>();


    /**
     * 设置礼物的最大显示数量
     *
     * @param maxShowCount
     */
    public void setMaxShowCount(int maxShowCount) {
        this.maxShowCount = maxShowCount;
    }

    /**
     * 高仿映客直播动画最大显示条目  0则不限制
     */
    private int maxShowCount = 4;
    /**
     * 做单个动画展示用的， 让屏幕始终保持只有一个动画 从垂直中间的最左边到屏幕正中间
     */
    private boolean animationing;//动画中

    public GiftAnimLayout(Context context) {
        super(context);
        init(context);
    }

    public GiftAnimLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GiftAnimLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);//40)DensityUtil.dip2px(context, 2)
        setPadding(0, 0, 0, 0);
//        android:animateLayoutChanges="true"
        LayoutTransition transition = new LayoutTransition();
        transition.setAnimator(LayoutTransition.APPEARING, null);
        transition.setAnimator(LayoutTransition.DISAPPEARING, null);//
        setLayoutTransition(transition);
    }

    /**
     * 永远只显示一个礼物
     *
     * @param context
     * @param giftModel
     */
    public void showSingInstanceNewGift(final Context context, GiftModelI giftModel) {
        final ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
        imageView.setVisibility(View.INVISIBLE);
        addView(imageView, layoutParams);//插入到视图中去
        singInstanceImageView.add(imageView);
        onLoadingPic(context, imageView, giftModel);
    }

    /**
     * 必须实现 否则不显示图片
     *
     * @param context
     * @param imageView
     * @param modelI
     */
    public void onLoadingPic(Context context, ImageView imageView, GiftModelI modelI) {
        Log.e(TAG, "不能加载图片请自定义view并复写方法onLoadingPic " + modelI.getImage());

    }

    public ViewGroup getGiftLayout(Context context) {
        return (ViewGroup) LayoutInflater.from(context).inflate(R.layout.view_live_gift_bar, this, false);
    }

    /**
     * 始终只显示一个。
     *
     * @param context
     */

    public void doAnimationSingInstance(final Context context) {
        final Animation animation = AnimationUtils.loadAnimation(context, R.anim.follow_anim_from_left_to_center);//从左边到中间的动画。
        if (singInstanceImageView.size() <= 0 || animationing) {
            return;
        }
        final ImageView imageView = singInstanceImageView.get(0);
        imageView.setVisibility(View.VISIBLE);
        imageView.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                animationing = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d(TAG, "动画已经结束");
                imageView.clearAnimation();
                GiftAnimLayout.this.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "removeView");
                        animationing = false;
                        singInstanceImageView.remove(imageView);
                        removeView(imageView);
                        doAnimationSingInstance(context);

                    }
                });

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 显示多个礼物  唯一标识 是 giftmodel的url和 用户id
     *
     * @param context
     * @param userInfo
     * @param giftModel
     */
    public void showNewGift(final Context context, UserInfoI userInfo, final GiftModelI giftModel) {
        View view = atShowMaps.get(getKey(userInfo, giftModel));

        if (view == null) {
            synchronized (waitList) {
                if (maxShowCount > 0 && getChildCount() >= maxShowCount) {
                    waitList.addUnique(UserInfoPair.create(userInfo, giftModel));
                    if (giftCallBack != null) {
                        giftCallBack.onAddWaitUnique(giftModel);
                    }
                } else {
                    if (giftCallBack != null) {
                        giftCallBack.onAddNewGift(giftModel);
                    }
                    productAndShow(context, userInfo, giftModel);
                }

            }
        } else {
            Log.d(TAG, "发现已存在图片" + giftModel.getImage() + ",加持显示中。");
            continueShow(context, userInfo, giftModel, view);
        }
    }

    private void continueShow(Context context, UserInfoI userInfo, GiftModelI giftModel, View view) {
        ViewGroup viewGroup = (ViewGroup) view;
        StrokeTextView tvValue = (StrokeTextView) viewGroup.findViewById(R.id.tv_value);
        tvValue.setValue(giftCallBack == null ? 1 + tvValue.getValue() : giftCallBack.onRequestShowGiftCount(giftModel, tvValue));
        tvValue.setText("X" + tvValue.getValue());//默认就是1只是第一次没显示出啦i
        final Animation animation = AnimationUtils.loadAnimation(context, R.anim.scale_anim);//字体动画
        tvValue.startAnimation(animation);
        Object tag = view.getTag(R.id.gift_anim_end_tag);
        if (tag != null && tag instanceof GiftEndRunnable) {
            if (giftCallBack != null) {
                giftCallBack.onConvertGiftAnim(giftModel);
            }
            view.clearAnimation();
            GiftEndRunnable runnable = (GiftEndRunnable) tag;//少创建了一个runnable对象。。
            boolean removeResult = view.removeCallbacks(runnable);//移除原来的动画
            view.postDelayed(runnable, showDuration);
            Log.d(TAG, "终于找到图片了,删除原来延时，重新延迟中,是否移除原来的runnable成功：" + removeResult);
        } else {
            startEndAnimRunnable(view, getKey(userInfo, giftModel), giftModel);
//            atShowMaps.remove(getKey(userInfo,giftModel));
            Log.d(TAG, "无法加持动画了 可能还在显示的过程中的动画 但是在这个过程的动画没法延长时间呀" + tag);
        }
        return;
    }

    public final String TAG_TIME = "TIME";

    /**
     * @param context
     * @param userInfo
     * @param giftModel
     */
    private void productAndShow(final Context context, final UserInfoI userInfo, final GiftModelI giftModel) {

        final ViewGroup viewGroup = getGiftLayout(context);
        atShowMaps.put(getKey(userInfo, giftModel), viewGroup);
        @SuppressLint("WrongViewCast") StrokeTextView tvValue = (StrokeTextView) viewGroup.findViewById(R.id.tv_value);
        final ImageView imageView = (ImageView) viewGroup.findViewById(R.id.iv_gift);
        ((TextView) viewGroup.findViewById(R.id.tv_nickname)).setText("" + userInfo.getName());
        if (userInfo.getFace() != null) {
            ImageLoader.getInstance().displayImage(userInfo.getFace().toString(), ((ImageView) viewGroup.findViewById(R.id.iv_head)));
        }
        viewGroup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onGiftBarClick != null) {
                    onGiftBarClick.onClick(userInfo);
                    Log.d(TAG, "点击了 某个送花item" + userInfo.getName());
                }
            }
        });
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LayoutParams layoutParams = (LayoutParams) viewGroup.getLayoutParams();
        layoutParams.gravity = Gravity.LEFT;
        layoutParams.leftMargin = DensityUtil.dip2px(context, marginLeft);
        layoutParams.topMargin = DensityUtil.dip2px(context, marginTop);
        viewGroup.setVisibility(INVISIBLE);
        addView(viewGroup, 0, layoutParams);//插入到视图中去

//        layoutParams.gravity = Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL;
        ImageLoader.getInstance().displayImage(giftModel.getImage(), imageView, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                Log.d(TAG, "图片查询完成");
//                animation.set
                doShowGiftAnim(getKey(userInfo, giftModel), giftModel, viewGroup);

            }
        });
    }


    public static String getKey(UserInfoI userInfo, GiftModelI giftModel) {
        return giftModel.getImage() + userInfo.getUserId();
    }

    /**
     * 进行礼物显示从屏幕最左边到 一定的距离 并  产生透明动画
     *
     * @param key       将会被移除的key {@link GiftAnimLayout#getKey(UserInfoI, GiftModelI)}
     * @param giftModel
     * @param view
     */
    public void doShowGiftAnim(final String key, final GiftModelI giftModel, final View view) {
//        final Animation animation = AnimationUtils.loadAnimation(context, R.anim.follow_anim_from_left_vertical_centerr);

        ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "translationX",
                -100, 0);//
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "alpha", 0, 1);
        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(500);
        animSet.setInterpolator(new AccelerateInterpolator());
        animSet.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                /*    int endTime = duration;
                                    if (view.getTag() != null && TAG_TIME.equals(view.getTag())) {
                                        endTime = showDuration + duration;//时间翻倍
                                    }*/
                                    Log.d(TAG, "动画已经结束");
                                    view.clearAnimation();//然后进行移除view的动画。
                                    startEndAnimRunnable(view, key, giftModel);
                                }


                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            }

        );
        view.setVisibility(View.VISIBLE);
        //两个动画同时执行
        animSet.playTogether(anim1, anim2);
        animSet.start();

    }

    /**
     * 开始结束的计时，计时完毕后会产生动画
     *
     * @param view
     * @param key
     * @param giftModel
     */
    private void startEndAnimRunnable(View view, String key, GiftModelI giftModel) {
        GiftEndRunnable giftRunnable = new GiftEndRunnable(view, key);
        giftRunnable.setGiftModel(giftModel);
        view.setTag(R.id.gift_anim_end_tag, giftRunnable);
        view.postDelayed(giftRunnable, showDuration);
    }

    /**
     * 礼物结束的 raunnable
     **/
    public class GiftEndRunnable implements Runnable


    {
        private final String key;
        private final View view;

        public void setGiftModel(GiftModelI giftModel) {
            this.giftModel = giftModel;
        }

        GiftModelI giftModel;

        public GiftEndRunnable(View view, String key) {
            this.view = view;
            this.key = key;

        }

        @Override
        public void run() {
            Log.d(TAG, "已移除动画图片" + key);


            //进行移除view的动画
            final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.follow_anim_from_left_to_right_hidden);////follow_anim_from_left_to_right_hidden follow_anim_from_left_vertical_hidden
            animation.setAnimationListener(new Animation.AnimationListener() {
                                               @Override
                                               public void onAnimationStart(Animation animation) {

                                               }

                                               @Override
                                               public void onAnimationEnd(Animation animation) {
                                                   atShowMaps.remove(key);

                                                   if (giftCallBack != null) {
                                                       giftCallBack.onGiftAnimOver(giftModel);
                                                   }
                                                   doGiftEndRemoveLogic(view);

                                               }

                                               @Override
                                               public void onAnimationRepeat(Animation animation) {

                                               }
                                           }

            );
            view.clearAnimation();
            view.startAnimation(animation);
        }
    }

    public interface GiftCallBack {
        void onGiftAnimOver(GiftModelI giftModel);

        void onConvertGiftAnim(GiftModelI giftModel);

        void onAddNewGift(GiftModelI giftModel);

        void onAddWaitUnique(GiftModelI giftModel);

        /**
         * 如果礼物的值是从其他地方维护的，
         *
         * @param modelI
         * @param tvValue
         * @return
         */
        int onRequestShowGiftCount(GiftModelI modelI, StrokeTextView tvValue);
    }

    public void setGiftCallBack(GiftCallBack giftCallBack) {
        this.giftCallBack = giftCallBack;
    }

    GiftCallBack giftCallBack;

    private void doGiftEndRemoveLogic(final View view) {

        GiftAnimLayout.this.post(new Runnable() {
                                     @Override
                                     public void run() {
                                         view.findViewById(R.id.tv_value).clearAnimation();
                                         Log.w(TAG, "已移除view:");
                                         view.clearAnimation();
                                         view.setTag(R.id.gift_anim_end_tag, null);//释放内存
                                         removeView(view);
                                         synchronized (waitList) {//判断排队的队列。
                                             if (waitList.size() > 0 && getChildCount() <= maxShowCount) {//如果有最大限制的总数了就不应该超过。
                                                 final UserInfoPair userInfoGiftModelPair = waitList.get(0);
                                                 final Integer count = waitList.getCount(userInfoGiftModelPair);
                                                 waitList.remove(0);

//                                                 final UserInfoI userInfo = userInfoGiftModelPair.first;
//                                                 final GiftModelI giftModel = userInfoGiftModelPair.second;
                                                 Log.d(TAG, "重新取出队列中的礼物 此礼物排队总数：" + count);
                                                 post(new LoopRunnable(getContext(), userInfoGiftModelPair, count));
                                             }

                                         }
                                     }
                                 }

        );
    }

    class LoopRunnable implements Runnable {
        public LoopRunnable(Context context, UserInfoPair userInfoGiftModelPair, int count) {
            this.context = context;
            this.userInfoGiftModelPair = userInfoGiftModelPair;
            this.count = count;
        }

        Context context;
        UserInfoPair userInfoGiftModelPair;
        int count;

        @Override
        public void run() {
            UserInfoI userInfo = userInfoGiftModelPair.first;
            GiftModelI giftModel = userInfoGiftModelPair.second;
            Log.d(TAG, "本礼物被重复排队总数：" + waitList.getCount(userInfoGiftModelPair));
            showNewGift(context, userInfo, giftModel);
   /*         if (count > 0) {
                Log.d(TAG, "总数：" + waitList.getCount(userInfoGiftModelPair));
//                waitList.setCount(userInfoGiftModelPair, count - 1);
                Log.d(TAG, "修改后总数：" + waitList.getCount(userInfoGiftModelPair));
                showNewGift(context, userInfo, giftModel);
                --count;
                postDelayed(this, 400);
            } else {
                Log.d(TAG, "count:" + count);
            }
        }*/
        }
    }

    public void setOnGiftBarClick(OnGiftBarClick onGiftBarClick) {
        this.onGiftBarClick = onGiftBarClick;
    }

    OnGiftBarClick onGiftBarClick;

    public interface OnGiftBarClick {
        void onClick(UserInfoI userInfo);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        GiftAnimLayout.this.removeCallbacks(null);
    }
}
