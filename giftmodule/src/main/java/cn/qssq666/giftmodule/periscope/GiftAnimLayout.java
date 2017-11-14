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

package cn.qssq666.giftmodule.periscope;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.util.Pair;
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

import java.util.ArrayList;
import java.util.List;

import cn.qssq666.giftmodule.R;
import cn.qssq666.giftmodule.holder.BaseViewHolderI;
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
    private final static String READ_ME_FIELD = "作用参见set本字段的使用方法";
    private static final String TAG = "GiftAnimLayout";

    //    Handler handler = new Handler(Looper.getMainLooper());
    private ArrayList<ImageView> singInstanceImageView = new ArrayList<>();

    /**
     * 在需求为超过指定数必须让新的马上显示而不添加等待的解决方案    有2种 一种就是马上移除并添加动画 第二种 就是 动画移除，动画移除会因为动画的隐藏时间，这种逻辑会超过最大显示数允许超过最大显示数的
     *
     * @param mMustAnimHide
     */
    public void setMustAnimHide(boolean mMustAnimHide) {
        this.mMustAnimHide = mMustAnimHide;
    }

    /**
     * {@link GiftAnimLayout#READ_ME_FIELD}
     */
    private boolean mMustAnimHide = true;

    /**
     * 允许发生交叉动画的总数差值 也就是说如果 x 1 x2 x3 这样的东西
     * 比如 一个礼物产生了，而且这个礼物本来存在的，但是这个礼物不是最底部的，如果这个礼物的总数连续数大于最底部的 多少 就发生一次交替，这里就是设置必须大于多少才能 发生交叉动画。
     * 如果要让他永远不发生变化 那么就设置 {@link Integer#MAX_VALUE} 的最大值或你自己填写也行. 只不过直接设置效率高一点 不需要判断了
     *
     * @param mAcrossDValue
     */
    public void setAcrossDValue(int mAcrossDValue) {

        this.mAcrossDValue = mAcrossDValue;
    }

    /**
     * 允许显示交叉动画bug也就是出现很多很多的时候交叉来不及了，就需要加入队列等待，否则会出现一些不优雅的情况，但是假如等待则导致 某需求 超过最大数必须移除，那么这个功能就必须打开了，否则梦点击可能出现超过最大值而且还要多很多个。
     *
     * @param allowAcrossAnimBug
     */
    public void setAllowAcrossAnimBug(boolean allowAcrossAnimBug) {
        this.allowAcrossAnimBug = allowAcrossAnimBug;
    }

    private boolean allowAcrossAnimBug = true;
    /**
     * 允许发生交叉动画的总数差值
     */
    private int mAcrossDValue = 1;
    private boolean mLayoutAniming;
    /**
     * 用来记录布局动画的结束时间 结束之后时间上海有一段时间需要错开 否则返回点击就出毛病了。
     */
    private long mCurrentLayoutAnimEndTime;
    //如果过短会导致重复执行 礼物过多的时候出现一点小毛病。  多个礼物交叉位置的时候 如果出现了一点小问题 那么数值再改大一点哈。
    private long mLayoutAnimTime = 600;

    /**
     * 默认是 飞快的从左边到右边移除掉,你可以更改为另外一个默认为从左边到右边消失的动画
     *
     * @param hidenAnim 传递 R.anim资源  默认可以不传递
     * @R.anim.follow_anim_from_left_vertical_hidden,R.anim.follow_anim_from_left_to_right_hidden}
     */
    public void setHidenAnim(int hidenAnim) {
        this.mHidenAnim = hidenAnim;
    }

    /**
     * @param showAnim
     * @deprecated 这里不是通过xml加载的 因此没卵用了。
     */
    public void setShowAnim(int showAnim) {
        this.mSHowAnim = showAnim;
    }

    /**
     *
     */
    private int mSHowAnim = R.anim.follow_anim_from_left_to_center;
    private int mHidenAnim = R.anim.follow_anim_from_left_to_right_hidden;


    /**
     * 单位 毫秒
     *
     * @param showDuration
     */
    public void setShowDuration(int showDuration) {
        this.showDuration = showDuration;
    }

    /**
     * 、3秒显示
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
     * 单位 dp 举例左边的举例
     *
     * @param marginLeft
     */
    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
    }

    public int marginLeft = 10;//dp
    /**
     * 这是代表当前正在显示并缓存的. 这个和那个没关系似乎这个是作废的。设置
     */
    private LruCache<String, Pair<GiftHolder, GiftEndRunnable>> atShowMaps = new LruCache<String, Pair<GiftHolder, GiftEndRunnable>>(100) {//这顶多100个。

        @Override
        protected int sizeOf(String key, Pair<GiftHolder, GiftEndRunnable> value) {
            return super.sizeOf(key, value);//默认就是1
        }
    };

    /**
     * 用于建设Layout.from infalte的次数， 当每一轮动画结束了，这里面都应该换成了几个。不缓存设置为0
     *
     * @param cacheViewCount
     */
    public void setCacheVewCount(int cacheViewCount) {
        this.mCacheVewCount = cacheViewCount;
    }

    /**
     * 如果开启了只显示指定总数不进行队列的话,那么缓存的个数估计也就只需要几个来着 最多显示4个，那么当一个view结束了,新的进来应该就能从里面找出来。
     */
    private int mCacheVewCount = 8;
    private List<ViewGroup> mCacheViews = new ArrayList<>();


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
     * 这个作废了。。
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


    @Override
    public void startViewTransition(View view) {
        super.startViewTransition(view);
        mLayoutAniming = true;
//        Log.w(TAG, "布局动画开始");
    }

    @Override
    public void endViewTransition(View view) {
        super.endViewTransition(view);
        mLayoutAniming = false;
        mCurrentLayoutAnimEndTime = System.currentTimeMillis();
//        Log.w(TAG, "布局动画完毕");
    }

    /**
     * 必须实现 否则不显示图片
     *
     * @param context
     * @param imageView
     * @param modelI
     */
    private void onLoadingPic(Context context, ImageView imageView, GiftModelI modelI) {
        Log.e(TAG, "不能加载图片请自定义view并复写方法onLoadingPic " + modelI.getGiftImage());

    }

    public ViewGroup getGiftLayout(Context context) {

        if (mCacheViews.size() > 0) {
            synchronized (mCacheViews) {
                ViewGroup viewGroup = mCacheViews.get(0);
                mCacheViews.remove(viewGroup);
                if (viewGroup.getParent() != null) {//TODO出现了一点毛病，竟然有概率出现已经被添加的情况了，所以加个判断暂时减轻了这个问题 不过 为什么锁都锁不住呢
                    return viewGroup;
                }
            }
        }
        ViewGroup giftLayout = null;
        if (giftCallBack != null) {
            giftLayout = giftCallBack.getGiftLayout(this);
        }
        if (giftLayout == null) {

            return (ViewGroup) LayoutInflater.from(context).inflate(R.layout.view_live_gift_bar, this, false);
        } else {
            return giftLayout;
        }
    }


    /**
     * 始终只显示一个。
     *
     * @param context
     */

    public void doAnimationSingInstance(final Context context) {
        final Animation animation = AnimationUtils.loadAnimation(context, mSHowAnim);//从左边到中间的动画。
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
    public void showNewGift(final Context context, final UserInfoI userInfo, final GiftModelI giftModel) {
        //这里的时间判断 是解决 队列重新调整 礼物过来 礼物动画还没有执行完毕那么会出现一些比较丑陋的情况 ，把时间差调节越大那么久出现尴尬的情况几率就越少。
        long l = System.currentTimeMillis();
        Log.w(TAG, "布局动画执行时间:" + (l - mCurrentLayoutAnimEndTime) + "," + l);
        if (!allowAcrossAnimBug) {

            if (mLayoutAniming || System.currentTimeMillis() - mCurrentLayoutAnimEndTime < mLayoutAnimTime) {
                Log.w(TAG, "布局动画尚未结束 进行循环等待中..");
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mLayoutAniming || System.currentTimeMillis() - mCurrentLayoutAnimEndTime < mLayoutAnimTime) {
                            postDelayed(this, 200);
                        } else {
                            Log.w(TAG, "布局动画等待结束了开始新的礼物动画.");
                            showNewGift(context, userInfo, giftModel);
                        }
                    }
                }, 200);
                return;
            }
        }

        Pair<GiftHolder, GiftEndRunnable> pair = atShowMaps.get(getKey(userInfo, giftModel));

        if (pair == null) {
            synchronized (waitList) {
                if (maxShowCount > 0 && getChildCount() >= maxShowCount) {
                    if (mThanNotAddQueueClearFirst) {
                        View firstView = getChildAt(0);
                        Pair<GiftHolder, GiftEndRunnable> pairFind = findShowPairByView(firstView);
                        if (pairFind != null) {
                            GiftAnimLayout.this.removeCallbacks(pairFind.second);
                            if (mMustAnimHide) {
                                pairFind.second.run();
                            } else {
                                doGiftEndRemoveLogic(pairFind.first, findKeyByView(firstView));

                            }
                        }
                        productAndShow(context, userInfo, giftModel);
                    } else {


                        if (!thanMaxShowClearShowTime) {
                            //超过了赶快改第一个显示view的时间为马上消失
                            View childAt = getChildAt(0);
                            String tag = findKeyByView(childAt);
                            Pair<GiftHolder, GiftEndRunnable> giftHolderGiftEndRunnablePair = atShowMaps.get(tag);
                            Log.w(TAG, "尝试加速消失 " + tag + "," + giftHolderGiftEndRunnablePair);
                            if (giftHolderGiftEndRunnablePair != null) {
                                GiftAnimLayout.this.removeCallbacks(giftHolderGiftEndRunnablePair.second);
                                GiftAnimLayout.this.post(giftHolderGiftEndRunnablePair.second);
                            }

                        }

                        waitList.addUnique(UserInfoPair.create(userInfo, giftModel));
                        if (giftCallBack != null) {
                            giftCallBack.onAddWaitUnique(giftModel);
                        }

                    }

                } else {
                    if (giftCallBack != null) {
                        giftCallBack.onAddNewGift(giftModel);
                    }
                    productAndShow(context, userInfo, giftModel);
                }

            }
        } else {
            Log.d(TAG, "发现已存在图片" + giftModel.getGiftImage() + ",加持显示中。");
            continueShow(context, userInfo, giftModel, pair);
        }
    }

    private String findKeyByView(View firstView) {
        return firstView == null ? null : (String) firstView.getTag(R.id.gift_bar_view_key);
    }

    /**
     * 清除时间等待比如需要4200毫秒才能消失现在是加快他的消失这个功能然并卵
     * 超过了最大显示是否等待 等待的意思是让每一个view显示的时间都市公平的， 比如有人一直送礼物那么他是没法顶下去的，只有当礼物总数小于了指定数的时候才开始插入因为显示屏超过了最大值被等待队列的礼物，如果传递false,
     * 那么尽可能的执行完动画，直接把显示礼物时间调零并马上执行隐藏移除动画。
     *
     * @param thanMaxShowClearFirst
     */
    public void setThanMaxShowClearZero(boolean thanMaxShowClearFirst) {
        this.thanMaxShowClearShowTime = thanMaxShowClearFirst;
    }

    private boolean thanMaxShowClearShowTime = true;

    /**
     * 超过最大显示数就把第一个index 0去掉，但是会出现动画消失有毛病的问题.  y因此 这个方法为true后通常配合MustAnimHide结合使用 因为 如果让新的一直显示不队列等待就需要让第一个移除这个移除过程是比较长的，那么肯定会超过所谓的最大显示数了。要测试是否正常 就提供一个随机 来进行测试。
     * 这个方法为true后通常配合MustAnimHide setAllowAcrossAnimBug结合使用 因为 如果让新的一直显示不队列等待就需要让第一个移除这个移除过程是比较长的，那么肯定会超过所谓的最大显示数了。另外布局动画时间等待交叉动画太快有bug,但是这里必须移除等待否则会出现毛病。
     *
     * @param clearFirstOnThanMaxShow
     */
    public void setThanQueueClearFirstAndNotAddQueue(boolean clearFirstOnThanMaxShow) {
        this.mThanNotAddQueueClearFirst = clearFirstOnThanMaxShow;
    }

    private boolean mThanNotAddQueueClearFirst = true;

    private void acrossAnim(final View view, final View to, float startX, final float startY, float endX, final float endY, final int i) {
//        view.setY(startY);//动画完成后该回去
        removeView(view);
        addView(view);

//        to.setY(endY);
        removeView(to);
        addView(to, i);
    /*    Log.w(TAG, "view,TAG" + view.getTag(R.id.across_ing) + "," + this.hashCode());
        if (view.getTag(R.id.across_ing) != null) {
            Log.w(TAG, "动画中");
            return;
        }
        if (to.getTag(R.id.across_ing) != null) {
            Log.w(TAG, "动画中");
            return;
        }
   *//*     if (view.getAnimation() != null && !view.getAnimation().hasEnded()) {
            return;
        }
        if (to.getAnimation() != null && !to.getAnimation().hasEnded()) {
            return;
        }
        view.clearAnimation();
        to.clearAnimation();*//*
        Log.w(TAG, "starty:" + startY + ",endY:" + endY);
        // 属性动画移动
        ObjectAnimator y = ObjectAnimator.ofFloat(view, "y", endY);
//        ObjectAnimator x = ObjectAnimator.ofFloat(view, "x", endX);
//        ObjectAnimator x1 = ObjectAnimator.ofFloat(to, "x", startX);
        ObjectAnimator x2 = ObjectAnimator.ofFloat(to, "y", startY);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(300);
        animatorSet.playTogether(y, x2);//2geview进行交叉 endXhe endY是作用在前者
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.clearAnimation();
                view.setY(startY);//动画完成后该回去
                removeView(view);
                addView(view);

                to.setY(endY);
                removeView(to);
                addView(to, i);

                Log.w(TAG, "交叉动画结束了," + view);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
      *//*          view.setTag(R.id.across_ing, null);
                to.setTag(R.id.across_ing, null);*//*
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        view.setTag(R.id.across_ing, true);
        to.setTag(R.id.across_ing, true);
        animatorSet.start();*/

    }

    private void acrossRemoveAndToAddAnim(View view, View toView, int i) {
        acrossAnim(view, toView, view.getX(), view.getY(), toView.getX(), toView.getY(), i);


      /*  Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.across_anim);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);*/
//        this.removeView(view);
//        this.addView(giftHolder.getItemView());
    }

    private void continueShow(Context context, UserInfoI userInfo, GiftModelI giftModel, Pair<GiftHolder, GiftEndRunnable> pair) {

        GiftHolder giftHolder = pair.first;

        pair.first.tvValue.setValue(getGiftCount(pair.first.tvValue, giftModel));
        giftHolder.tvValue.setText("X" + giftHolder.tvValue.getValue());//默认就是1只是第一次没显示出啦i
        final Animation animation = AnimationUtils.loadAnimation(context, R.anim.scale_anim);//字体动画
        giftHolder.tvValue.startAnimation(animation);
        this.removeCallbacks(pair.second);
        pair.first.getItemView().clearAnimation();

        postDelayExecuteRunnable(pair.second, showDuration);

        int childCount = this.getChildCount();
        int i = this.indexOfChild(giftHolder.getItemView());
        if (i != childCount - 1) {

            View childAt = this.getChildAt(childCount - 1);

            if (mAcrossDValue != Integer.MAX_VALUE) {

                Pair<GiftHolder, GiftEndRunnable> findPair = findShowPairByView(childAt);
                if (findPair == null || findPair.first == null) {
                    Log.w(TAG, "找不到holder无法产生交替动画" + findPair);
                    return;
                }
                int bottomGiftCount = getGiftCount(findPair.first.tvValue, findPair.second.giftModel);
                int currentGiftCount = getGiftCount(giftHolder.tvValue, giftModel);
                int dValue = currentGiftCount - bottomGiftCount;//如果是整数 例如 本来底部是40  我这里  是 60 那么差值是20 那么如果我设置的是5 就发生一次那么这里就应该发生交换了，同样如果比底部的还小是负数那么不会发生。
                if (dValue >= mAcrossDValue) {
                    acrossRemoveAndToAddAnim(giftHolder.getItemView(), childAt, i);
                } else {
                    Log.w(TAG, "没有超过差值 直接交换" + giftHolder.getItemView().getY());
                }
            } else {
                acrossRemoveAndToAddAnim(giftHolder.getItemView(), childAt, i);
            }


       /* if (tag != null && tag instanceof GiftEndRunnable) {
            if (giftCallBack != null) {
                giftCallBack.onFindExistGiftAnim(giftModel);
            }
            pair.clearAnimation();
            GiftEndRunnable runnable = (GiftEndRunnable) tag;//少创建了一个runnable对象。。

            postDelayExecuteRunnable(runnable, showDuration);
            Log.d(TAG, "终于找到图片了,删除原来延时，重新延迟中：");
        } else {
            startEndAnimRunnable(pair, getKey(userInfo, giftModel), giftModel);
//            atShowMaps.remove(getKey(userInfo,giftModel));
            Log.w(TAG, "警告 出现 队列异常 问题 显示集合中还存在，但是并没有找到定时关闭runnable" + tag + ",gift:" + giftModel.getTitle());
        }*/
        }
    }


    private Pair<GiftHolder, GiftEndRunnable> findShowPairByView(View view) {
        String tag = findKeyByView(view);
        if (tag == null) {
            return null;
        }
        Pair<GiftHolder, GiftEndRunnable> pair = atShowMaps.get(tag);
        return pair;
    }

    public final String TAG_TIME = "TIME";

    /**
     * @param group
     * @return
     */
    private Pair<GiftHolder, GiftEndRunnable> createGiftViewPair(ViewGroup group, String
            key, GiftModelI model) {
        GiftHolder giftHolder = new GiftHolder(group);
        GiftEndRunnable endRunnable = new GiftEndRunnable(giftHolder, key);
        endRunnable.setGiftModel(model);
        return Pair.create(giftHolder, endRunnable);
    }

    private void productAndShow(final Context context, final UserInfoI userInfo, final GiftModelI giftModel) {

        final ViewGroup viewGroup = getGiftLayout(context);
        viewGroup.setVisibility(INVISIBLE);
        LayoutParams layoutParams = (LayoutParams) viewGroup.getLayoutParams();
        layoutParams.gravity = Gravity.LEFT;
        layoutParams.leftMargin = DensityUtil.dip2px(context, marginLeft);
        layoutParams.topMargin = DensityUtil.dip2px(context, marginTop);
        addView(viewGroup, layoutParams);//插入到视图中去
        String key = getKey(userInfo, giftModel);
        viewGroup.setTag(R.id.gift_bar_view_key, key);
        Pair<GiftHolder, GiftEndRunnable> giftViewPair = createGiftViewPair(viewGroup, key, giftModel);
        atShowMaps.put(key, giftViewPair);
        giftViewPair.first.tvName.setText("" + userInfo.getName());
        giftViewPair.first.tvValue.setValue(getGiftCount(giftViewPair.first.tvValue, giftModel));
        giftViewPair.first.ivFace.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onGiftBarFaceClick != null) {
                    onGiftBarFaceClick.onClick(userInfo);
                    Log.w(TAG, "点击了 某个送花item" + userInfo.getName());
                }
            }
        });
        giftViewPair.first.ivGift.setScaleType(ImageView.ScaleType.FIT_CENTER);

        if (giftCallBack == null || giftCallBack.onBindPic(userInfo, giftModel, giftViewPair.first) == false) {

            ImageLoader.getInstance().displayImage(userInfo.getFace(), giftViewPair.first.ivFace);
            ImageLoader.getInstance().displayImage(giftModel.getGiftImage(), giftViewPair.first.ivGift);
        }

        doShowGiftAnim(getKey(userInfo, giftModel), giftModel, giftViewPair);
    }


    public String getKey(UserInfoI userInfo, GiftModelI giftModel) {
        if (keyGenerationRuleHolder != null) {
            return keyGenerationRuleHolder.onRequestGenerationRule(giftModel, userInfo);
        }
        return generateKeyByUserAndGiftModel(userInfo, giftModel);
    }

    public static String generateKeyByUserAndGiftModel(UserInfoI userInfo, GiftModelI giftModel) {
        return giftModel.getGiftImage() + userInfo.getUserId();
    }

    /**
     * 进行礼物显示从屏幕最左边到 一定的距离 并  产生透明动画
     *
     * @param key       将会被移除的key {@link GiftAnimLayout#getKey(UserInfoI, GiftModelI)}
     * @param giftModel
     * @param pair
     */
    private void doShowGiftAnim(final String key, final GiftModelI giftModel, final Pair<GiftHolder, GiftEndRunnable> pair) {
//        final Animation animation = AnimationUtils.loadAnimation(context, R.anim.follow_anim_from_left_vertical_centerr);

        ObjectAnimator anim1 = ObjectAnimator.ofFloat(pair.first.getItemView(), "translationX",
                -100, 0);//
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(pair.first.getItemView(), "alpha", 0.5f, 1);
        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
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
                                    Log.d(TAG, "showAnim动画已经结束");
                                    pair.first.getItemView().clearAnimation();//然后进行移除view的动画。
                                    postDelayExecuteRunnable(pair.second, showDuration);
                                }


                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            }

        );
        pair.first.getItemView().setVisibility(View.VISIBLE);
        //两个动画同时执行
        animSet.playTogether(anim1, anim2);
        animSet.start();

    }

    /**
     * 开始结束的计时，计时完毕后会产生动画
     */
  /*  private void startEndAnimRunnable(GiftHolder view, String key, GiftModelI giftModel) {
        GiftEndRunnable giftRunnable = new GiftEndRunnable(view, key);
        giftRunnable.setGiftModel(giftModel);
        postDelayExecuteRunnable(giftRunnable, showDuration);
    }
*/
    public void postDelayExecuteRunnable(Runnable runnable, int time) {
        this.removeCallbacks(runnable);
        this.postDelayed(runnable, time);
    }
//    HashMap<>


    public int getGiftCount(StrokeTextView strokeTextView, GiftModelI model) {
        return giftCallBack == null || model.getShowcount() == 0 ? 1+strokeTextView.getValue() : giftCallBack.onRequestShowGiftCount(model, strokeTextView);
    }

    /**
     * 礼物结束的 raunnable
     **/
    public class GiftEndRunnable implements Runnable


    {
        private final String key;
        private final GiftHolder giftHolder;

        public void setGiftModel(GiftModelI giftModel) {
            this.giftModel = giftModel;
        }

        GiftModelI giftModel;

        public GiftEndRunnable(GiftHolder giftHolder, String key) {
            this.giftHolder = giftHolder;
            this.key = key;

        }

        @Override
        public void run() {
            Log.d(TAG, "GiftEndRunnable计时已完成,开始收尾处理,已移除动画图片" + key);
            //进行移除view的动画
            final Animation animation = AnimationUtils.loadAnimation(getContext(), mHidenAnim);////follow_anim_from_left_to_right_hidden follow_anim_from_left_vertical_hidden
            giftHolder.getItemView().clearAnimation();
            animation.setAnimationListener(new Animation.AnimationListener() {
                                               @Override
                                               public void onAnimationStart(Animation animation) {

                                               }

                                               @Override
                                               public void onAnimationEnd(Animation animation) {


                                                   doGiftEndRemoveLogic(giftHolder, key);

                                                   if (giftCallBack != null) {
                                                       giftCallBack.onGiftAnimOver(giftModel);
                                                   }
                                               }

                                               @Override
                                               public void onAnimationRepeat(Animation animation) {

                                               }
                                           }

            );
            giftHolder.getItemView().startAnimation(animation);
        }
    }

    /**
     * giftLayout你可以返回空 onBindPic你也可以返回空。
     */
    public interface GiftCallBack {
        void onGiftAnimOver(GiftModelI giftModel);

        /**
         * 如果正在显示 也就是在atShow集合裏面就hui会回调。
         *
         * @param giftModel
         */
        void onFindExistGiftAnim(GiftModelI giftModel);

        void onAddNewGift(GiftModelI giftModel);

        /**
         * 如果调用了 {@link GiftAnimLayout#setThanMaxShowClearZero(boolean)} 为false那么这个永远不会执行。
         *
         * @param giftModel
         */
        void onAddWaitUnique(GiftModelI giftModel);

        /**
         * 如果礼物的值是从其他地方维护的，
         *
         * @param modelI
         * @param tvValue
         * @return
         */
        int onRequestShowGiftCount(GiftModelI modelI, StrokeTextView tvValue);

        /**
         * 默认布局则可以不写，如果返回不为空，但是id必须和提供的一样必须一样否则无法进行findbyid
         *
         * @param giftAnimLayout
         * @return
         */
        ViewGroup getGiftLayout(GiftAnimLayout giftAnimLayout);

        /**
         * 是否自己绑定图片，自己绑定则返回false
         *
         * @param userInfo
         * @param giftModel
         * @param giftHolder
         * @return
         */
        boolean onBindPic(UserInfoI userInfo, GiftModelI giftModel, GiftHolder giftHolder);
    }

    /**
     * 默认布局的构造
     */
    public static abstract class DefaultCallBack implements GiftCallBack {
        /**
         * 默认布局
         *
         * @param giftAnimLayout
         * @return
         */
        @Override
        public ViewGroup getGiftLayout(GiftAnimLayout giftAnimLayout) {
            return null;
        }

        /**
         * 自己维护
         *
         * @param modelI
         * @param tvValue
         * @return
         */
        @Override
        public int onRequestShowGiftCount(GiftModelI modelI, StrokeTextView tvValue) {
            return 0;
        }

        @Override
        public boolean onBindPic(UserInfoI userInfo, GiftModelI giftModel, GiftHolder first) {
            return false;//自己的Imageloader绑定吧。
        }
    }

    public void setGiftAdapterAndCallBack(GiftCallBack giftCallBack) {
        this.giftCallBack = giftCallBack;
    }

    GiftCallBack giftCallBack;

    private void doGiftEndRemoveLogic(final GiftHolder giftHolder, final String key) {

        this.post(new Runnable() {
                      @Override
                      public void run() {
                          giftHolder.tvValue.clearAnimation();
                          Log.w(TAG, "已移除view:");
                          giftHolder.getItemView().clearAnimation();
                          View itemView = giftHolder.getItemView();
                          removeView(itemView);
                          synchronized (mCacheViews) {
                              if (mCacheViews.size() < mCacheVewCount) {
                                  giftHolder.tvValue.setValue(1);
                                  mCacheViews.add((ViewGroup) itemView);
                              }
                          }

//                          requestLayout();
                          atShowMaps.remove(key);
                          synchronized (waitList) {//判断排队的队列。
                              if (waitList.size() > 0 && getChildCount() <= maxShowCount) {
                                  /**
                                   *
                                   * 如果当前的孩子小于了最大显示总数了，而且队列中还有 就 执行。移除动画。但是如果不想排队只想一直显示4个怎么办，调用方法 {@link GiftAnimLayout#setonl}
                                   */
                                  final UserInfoPair userInfoGiftModelPair = waitList.get(0);
                                  final Integer count = waitList.getCount(userInfoGiftModelPair);
                                  waitList.remove(0);
                                  Log.d(TAG, "重新取出队列中的礼物 此礼物排队总数：" + count);
                                  post(new LoopRunnable(getContext(), userInfoGiftModelPair, count));
                              }

                          }
                      }
                  }

        );
    }

    /**
     * 不应该叫循环runnable,而应该叫继续完成未完成的任务runnble
     */
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

    public void setOnGiftBarFaceClick(OnGiftBarFaceClick onGiftBarClick) {
        this.onGiftBarFaceClick = onGiftBarClick;
    }

    OnGiftBarFaceClick onGiftBarFaceClick;

    public interface OnGiftBarFaceClick {
        void onClick(UserInfoI userInfo);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public static class GiftHolder implements BaseViewHolderI {
        public final View itemView;
        public final StrokeTextView tvValue;
        public final TextView tvName;
        public final ImageView ivFace;
        public final ImageView ivGift;

        public GiftHolder(View view) {
            this.itemView = view;
            tvValue = ((StrokeTextView) view.findViewById(R.id.tv_value));
            tvName = ((TextView) view.findViewById(R.id.tv_nickname));
            ivFace = ((ImageView) view.findViewById(R.id.iv_head));
            ivGift = ((ImageView) view.findViewById(R.id.iv_gift));

        }

        @Override
        public View getItemView() {
            return itemView;
        }
    }

    /**
     * 不设置默认由图片地址和用户id组成一个 唯一 来控制是否x1 x2 x3
     *
     * @param keyGenerationRuleHolder
     */
    public void setKeyGenerationRuleHolder(KeyGenerationRuleHolder keyGenerationRuleHolder) {
        this.keyGenerationRuleHolder = keyGenerationRuleHolder;
    }

    KeyGenerationRuleHolder keyGenerationRuleHolder;

    public interface KeyGenerationRuleHolder {
        String onRequestGenerationRule(GiftModelI modelI, UserInfoI userInfoI);
    }

}
