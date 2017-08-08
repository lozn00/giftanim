package cn.qssq666.giftmodule.periscope;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

import cn.qssq666.giftmodule.R;


/**
 * Created by yifeiyuan on 15/6/19.
 * <p/>
 * http://www.jianshu.com/p/03fdcfd3ae9c
 */
public class FavorLayout extends RelativeLayout {

    private static final String TAG = "FavorLayout";

    private Interpolator line = new LinearInterpolator();//线性
    private Interpolator acc = new AccelerateInterpolator();//加速
    private Interpolator dce = new DecelerateInterpolator();//减速
    private Interpolator accdec = new AccelerateDecelerateInterpolator();//先加速后减速
    private Interpolator[] interpolators;

    private int mHeight;
    private int mWidth;

    private LayoutParams lp;

    public FavorLayout(Context context) {
        super(context);
        setBackgroundColor(getResources().getColor(android.R.color.transparent));
        init(context);
    }

    public FavorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(getResources().getColor(android.R.color.transparent));
        init(context);
    }


    private Random random = new Random();

    private int dHeight;
    private int dWidth;

    public void setDrawables(Drawable[] drawables) {
        this.drawables = drawables;
    }

    Drawable[] drawables;

    public void init(Context context) {
  /*      gestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            *//**
         * 按下
         * @param e
         * @return
         *//*
            @Override
            public boolean onDown(MotionEvent e) {
                Log.i(TAG, "onDown");
//                performClick();
//                return true;
                return true;//这个不好弄，按下的时候交给父亲可好?不然划不动了
            }

            *//**
         * 正在按住
         * @param e
         *//*
            @Override
            public void onShowPress(MotionEvent e) {
                Log.i(TAG, "onShowPress");
            }

            *//*
             * 按下并抬起了 单击事件
             * @param e
             * @return
             *//*
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.i(TAG, "onSingleTapUp");
                performClick();
                return true;
            }

            *//**
         * 滑动
         * @param e1
         * @param e2
         * @param distanceX
         * @param distanceY
         * @return
         *//*
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.i(TAG, "onScroll" + distanceX + "," + distanceY);//滚动事件
//                getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.i(TAG, "onLongPress");
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });*/

        //初始化显示的图片

//        red = getResources().getDrawable(R.drawable.red);
//        yellow = getResources().getDrawable(R.drawable.yellow);
//        blue = getResources().getDrawable(R.drawable.blue);
        if(drawables==null){
            drawables = new Drawable[]{
//                ContextCompat.getDrawable(context, R.drawable.img_redsmall),
//                ContextCompat.getDrawable(context, R.drawable.img_redsmall),
                    ContextCompat.getDrawable(context, R.drawable.img_bluesmall),
                    ContextCompat.getDrawable(context, R.drawable.zan_4_heart),
                    ContextCompat.getDrawable(context, R.drawable.zan_1_bear),
                    ContextCompat.getDrawable(context, R.drawable.zan_2_cat),
                    ContextCompat.getDrawable(context, R.drawable.zan_3_circle),
                    ContextCompat.getDrawable(context, R.drawable.zan_5_pig),
                    ContextCompat.getDrawable(context, R.drawable.zan_6_sheep),
                    ContextCompat.getDrawable(context, R.drawable.zan_7_rabbit),
                    ContextCompat.getDrawable(context, R.drawable.zan_8_spotty),
                    ContextCompat.getDrawable(context, R.drawable.zan_9_dog)
            };

        }
//        drawables[3] = ContextCompat.getDrawable(context,R.drawable.ico_flower_purple);
        //获取图的宽高 用于后面的计算
        //注意 我这里3张图片的大小都是一样的,所以我只取了一个
        dHeight = drawables[0].getIntrinsicHeight();
        dWidth = drawables[0].getIntrinsicWidth();

        //底部 并且 水平居中
        lp = new LayoutParams(dWidth, dHeight);
        lp.addRule(CENTER_HORIZONTAL, TRUE);//这里的TRUE 要注意 不是true
        lp.addRule(ALIGN_PARENT_BOTTOM, TRUE);
        lp.addRule(ALIGN_PARENT_RIGHT, TRUE);
//        lp.addRule(ALIGN_PARENT_BOTTOM, TRUE);

        // 初始化插补器
        interpolators = new Interpolator[4];
        interpolators[0] = line;
        interpolators[1] = acc;
        interpolators[2] = dce;
        interpolators[3] = accdec;

    }


    public LayoutParams getContentLayoutParam() {
        return lp;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取本身的宽高 这里要注意,测量之后才有宽高
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
//        Log.i(TAG, "mWidth" + mWidth + ",mHeight:" + mHeight);
        if (mHeight <= 0) {
            mHeight = 250;
            Log.e(TAG, "无法获取高度,你是否设置了底部居中参数");
        }
        if (mWidth <= 0) {
            mWidth = 250;
            Log.e(TAG, "无法获取宽度,你是否设置了某些居中对齐的参数,请用代码实现!");
        }
    }


    public void addFavor() {

        ImageView imageView = new ImageView(getContext());
        //随机选一个
        imageView.setImageDrawable(drawables[random.nextInt(drawables.length)]);
        imageView.setLayoutParams(lp);

        addView(imageView);
        Log.v(TAG, "add后子view数:" + getChildCount());

        Animator set = getAnimator(imageView);
        set.addListener(new AnimEndListener(imageView));
        set.start();

    }

    private Animator getAnimator(View target) {
        AnimatorSet set = getEnterAnimtor(target);
        ValueAnimator bezierValueAnimator = getBezierValueAnimator(target);

        AnimatorSet finalSet = new AnimatorSet();
        finalSet.playSequentially(set);
        finalSet.setInterpolator(interpolators[random.nextInt(4)]);
        finalSet.setTarget(target);
//        float scaleValue = 1 + random.nextInt(4) * 0.1f;
//        final ScaleAnimation scaleAnimation = new ScaleAnimation(1f, scaleValue, 1f, scaleValue,
//                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        finalSet.playSequentially(set, bezierValueAnimator);
        return finalSet;
    }

    private AnimatorSet getEnterAnimtor(final View target) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(target, View.ALPHA, 0.2f, 1f);
        float scaleValue = 1.1f + random.nextInt(9) * 0.1f;//1.0 -1.3
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(target, View.SCALE_X, 1f, scaleValue);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(target, View.SCALE_Y, 1f, scaleValue);
        AnimatorSet enter = new AnimatorSet();
        enter.setDuration(50);
        enter.setInterpolator(new LinearInterpolator());
        enter.playTogether(alpha, scaleX, scaleY);
        enter.setTarget(target);
        return enter;
    }

    private ValueAnimator getBezierValueAnimator(View target) {

        //初始化一个贝塞尔计算器- - 传入
        BezierEvaluator evaluator = new BezierEvaluator(getPointF(2), getPointF(1));

        //这里最好画个图 理解一下 传入了起点 和 终点
        //开始 x 开始y 结束x 结束y x横左边
        ValueAnimator animator = ValueAnimator.ofObject(evaluator, new PointF(mWidth - dWidth, mHeight - dHeight), new PointF(0, 0));
//        ValueAnimator animator = ValueAnimator.ofObject(evaluator, new PointF((mWidth - dWidth) / 2, mHeight - dHeight), new PointF(random.nextInt(getWidth()), 0));
        animator.addUpdateListener(new BezierListenr(target));
        animator.setTarget(target);
        animator.setDuration(4000);
        return animator;
    }

    /**
     * 获取中间的两个 点
     *
     * @param scale
     */
    private PointF getPointF(int scale) {

        PointF pointF = new PointF();
        pointF.x = random.nextInt((mWidth - 100));//减去100 是为了控制 x轴活动范围,看效果 随意~~
        //再Y轴上 为了确保第二个点 在第一个点之上,我把Y分成了上下两半 这样动画效果好一些  也可以用其他方法
        pointF.y = random.nextInt((mHeight - 100)) / scale;
        return pointF;
    }

    private class BezierListenr implements ValueAnimator.AnimatorUpdateListener {

        private View target;

        public BezierListenr(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            //这里获取到贝塞尔曲线计算出来的的x y值 赋值给view 这样就能让爱心随着曲线走啦
            PointF pointF = (PointF) animation.getAnimatedValue();
            target.setX(pointF.x);
            target.setY(pointF.y);
            // 这里顺便做一个alpha动画
            target.setAlpha(1 - animation.getAnimatedFraction());
        }
    }


    private class AnimEndListener extends AnimatorListenerAdapter {
        private View target;

        public AnimEndListener(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            //因为不停的add 导致子view数量只增不减,所以在view动画结束后remove掉
            removeView((target));
            Log.v(TAG, "removeView后子view数:" + getChildCount());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
/*
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
//        return false;
//        return false; //只能这样解决该死的父类一直不要触摸事件的滑动冲突问题了
//        return b;
//        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        return super.onInterceptTouchEvent(ev);
        boolean b = gestureDetector.onTouchEvent(ev);
        if (!b) {
            ((ViewGroup) getParent()).onTouchEvent(ev);
            Log.i(TAG, "这是你的事件,你处理吧，当down为true后父亲不会再收到事件了，只能这样写了吗?");
        }
        Log.i(TAG, "是否吃掉父亲给我的梨子" + b + "," + ev);
        return b;
    }*/
}
