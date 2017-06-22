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
import java.util.HashMap;

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
public  class GiftAnimLayout extends LinearLayout {
    private static final String TAG = "GfitLayout";
    private ArrayList<ImageView> singInstanceImageView = new ArrayList<>();
    /**
     * 由图片和id组成保证唯一 、3秒显示
     */
    public int duration = 4000;
    public int marginTop = 5;
    public int marginLeft = 10;
    /**
     * 这是正在动画中处理的.
     */
    private HashMap<String, View> sortMap = new HashMap<>();
    /**
     * 这是新的动画产生了,但是当前超过了最大值 所存放的容器
     */
    // userInfo, giftModel
    private CountList<UserInfoPair> waitList = new CountList<>();
    private int mMaxCount = 10;

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
        setMaxShowCount(mMaxCount);
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

    public void doAnimationSingInstance(final Context context) {
        final Animation animation = AnimationUtils.loadAnimation(context, R.anim.follow_anim_from_left_to_center);
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
                Log.i(TAG, "动画已经结束");
                imageView.clearAnimation();
                GiftAnimLayout.this.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "removeView");
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


    public void showNewGift(final Context context, UserInfoI userInfo, final GiftModelI giftModel) {
        View view = sortMap.get(getKey(userInfo, giftModel));
        if (view == null) {
            if (maxShowCount > 0 && getChildCount() >= maxShowCount) {
                waitList.addUnique(UserInfoPair.create(userInfo, giftModel));
            } else {
                productAndShow(context, userInfo, giftModel);
            }
        } else {
            Log.i(TAG, "发现已存在图片" + giftModel.getImage() + ",加持显示中。");
            continueShow(context, userInfo, giftModel, view);
        }
    }

    private void continueShow(Context context, UserInfoI userInfo, GiftModelI giftModel, View view) {
        ViewGroup viewGroup = (ViewGroup) view;
        StrokeTextView tvValue = (StrokeTextView) viewGroup.findViewById(R.id.tv_value);
        tvValue.setValue(tvValue.getValue() + 1);
        tvValue.setText("X" + tvValue.getValue());//默认就是1只是第一次没显示出啦i
        final Animation animation = AnimationUtils.loadAnimation(context, R.anim.scale_anim);
        tvValue.startAnimation(animation);
        Object tag = view.getTag();
        if (tag != null && tag instanceof Runnable) {
            Runnable runnable = (Runnable) tag;
            view.removeCallbacks(runnable);
            view.postDelayed(runnable, duration + 1000);
            Log.i(TAG, "终于找到图片了,删除原来延时，重新延迟中");
        } else {
            view.setTag(TAG_TIME);
//            sortMap.remove(getKey(userInfo,giftModel));
            Log.i(TAG, "无法加持动画了 可能还在显示的过程中的动画 但是在这个过程的动画没法延长时间呀" + tag);
        }
    }

    public final String TAG_TIME = "TIME";

    /**
     * 有bug作废了
     *
     * @param context
     * @param userInfo
     * @param giftModel
     */
    @Deprecated
    public void productAndShow(final Context context, final UserInfoI userInfo, final GiftModelI giftModel) {

        final ViewGroup viewGroup = getGiftLayout(context);
        @SuppressLint("WrongViewCast") StrokeTextView tvValue = (StrokeTextView) viewGroup.findViewById(R.id.tv_value);
        final ImageView imageView = (ImageView) viewGroup.findViewById(R.id.iv_gift);
        ((TextView) viewGroup.findViewById(R.id.tv_nickname)).setText("" + userInfo.getName());
        if (userInfo.getPortraitUri() != null) {
            ImageLoader.getInstance().displayImage(userInfo.getPortraitUri().toString(), ((ImageView) viewGroup.findViewById(R.id.iv_head)));
        }
        viewGroup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onGiftBarClick != null) {
                    onGiftBarClick.onClick(userInfo);
                    Log.i(TAG, "点击了 某个送花item" + userInfo.getName());
                }
            }
        });
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LayoutParams layoutParams = (LayoutParams) viewGroup.getLayoutParams();
        layoutParams.gravity = Gravity.LEFT;
        layoutParams.leftMargin = DensityUtil.dip2px(context, marginLeft);
        layoutParams.bottomMargin = DensityUtil.dip2px(context, marginTop);
        viewGroup.setVisibility(INVISIBLE);
        addView(viewGroup, 0, layoutParams);//插入到视图中去
        sortMap.put(getKey(userInfo, giftModel), viewGroup);
//        layoutParams.gravity = Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL;
        ImageLoader.getInstance().displayImage(giftModel.getImage(), imageView, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                Log.i(TAG, "图片查询完成");
//                animation.set
                doAnimationOnLoadImgFinish(getKey(userInfo, giftModel), viewGroup, context);

            }
        });
    }



    public static String getKey(UserInfoI userInfo, GiftModelI giftModel) {
        return giftModel.getImage() + userInfo.getUserId();
    }

    /**
     * @param key     将会被移除的key
     * @param view
     * @param context
     */
    public void doAnimationOnLoadImgFinish(final String key, final View view, final Context context) {
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
                                    int endTime = duration;
                                    if (view.getTag() != null && TAG_TIME.equals(view.getTag())) {
                                        endTime = duration + duration;//时间翻倍
                                    }
                                    Log.i(TAG, "动画已经结束");
                                    view.clearAnimation();
                                    Runnable runnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.i(TAG, "已移除动画图片" + key);
                                            view.setTag(null);
                                            sortMap.remove(key);

                                            final Animation animation = AnimationUtils.loadAnimation(context, R.anim.follow_anim_from_left_vertical_hidden);
                                            animation.setAnimationListener(new Animation.AnimationListener() {
                                                                               @Override
                                                                               public void onAnimationStart(Animation animation) {

                                                                               }

                                                                               @Override
                                                                               public void onAnimationEnd(Animation animation) {
                                                                                   doEnd(view, context);

                                                                               }

                                                                               @Override
                                                                               public void onAnimationRepeat(Animation animation) {

                                                                               }
                                                                           }

                                            );
                                            view.startAnimation(animation);
                                        }
                                    };

                                    view.setTag(runnable);
                                    view.postDelayed(runnable, endTime);
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

    private void doEnd(final View view, final Context context) {
        GiftAnimLayout.this.post(new Runnable() {
                                     @Override
                                     public void run() {
                                         view.findViewById(R.id.tv_value).clearAnimation();
                                         view.clearAnimation();
                                         removeView(view);
                                         synchronized (GiftAnimLayout.class) {
                                             if (waitList.size() > 0 && getChildCount() <= maxShowCount) {//如果有最大限制的总数了就不应该超过。
                                                 final UserInfoPair userInfoGiftModelPair = waitList.get(0);
                                                 final Integer count = waitList.getCount(userInfoGiftModelPair);
                                                 waitList.remove(0);
                                                 final UserInfoI userInfo = userInfoGiftModelPair.first;
                                                 final GiftModelI giftModel = userInfoGiftModelPair.second;
                                                 Log.i(TAG, "总数：" + count);
                                                 post(new LoopRunnable(context, userInfoGiftModelPair, count));
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
            if (count > 0) {
                Log.i(TAG, "总数：" + waitList.getCount(userInfoGiftModelPair));
//                waitList.setCount(userInfoGiftModelPair, count - 1);
                Log.i(TAG, "修改后总数：" + waitList.getCount(userInfoGiftModelPair));
                showNewGift(context, userInfo, giftModel);
                --count;
                postDelayed(this, 400);
            } else {
                Log.i(TAG, "count:" + count);
            }
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
