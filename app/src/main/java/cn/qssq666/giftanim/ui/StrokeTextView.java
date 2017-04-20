package cn.qssq666.giftanim.ui;

/**
 * Created by luozheng on 16/1/13.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.qssq666.giftanim.R;


/*
 * StrokeTextView的目标是给文字描边
 * 实现方法是两个TextView叠加,只有描边的TextView为底,实体TextView叠加在上面
 * 看上去文字就有个不同颜色的边框了
 */
@SuppressLint("AppCompatCustomView")
public class StrokeTextView extends TextView {

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    int value = 1;
    private TextView borderText = null;///用于描边的TextView

    public StrokeTextView(Context context) {
        super(context);
        borderText = new TextView(context);
        init(context, null, 0);
    }

    public StrokeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        borderText = new TextView(context, attrs);
        init(context, attrs, 0);
    }

    public StrokeTextView(Context context, AttributeSet attrs,
                          int defStyle) {
        super(context, attrs, defStyle);
        borderText = new TextView(context, attrs, defStyle);
        init(context, attrs, 0);
    }

    public void setStrokeBorderWidth(int width) {
        TextPaint tp1 = borderText.getPaint();
        tp1.setStrokeWidth(width);                                  //设置描边宽度
        tp1.setStyle(Style.STROKE);
    }

    public void setStrokeBorderColor(int color) {
        borderText.setTextColor(color);
    }

    public void setStrokeBorderColor(ColorStateList strokeBorderColor) {
        borderText.setTextColor(strokeBorderColor);
    }

    public void init(Context context, AttributeSet attributeSet, int defStyle) {
        TextPaint tp1 = borderText.getPaint();
        tp1.setStrokeWidth(3);                                  //设置描边宽度
        tp1.setStyle(Style.STROKE);                             //对文字只描边

//        borderText.setTextColor(Color.BLACK);  //设置描边颜色
        borderText.setGravity(getGravity());
        if (attributeSet != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.StrokeTextView, defStyle, 0);
            borderText.setTextColor(typedArray.getColor(R.styleable.StrokeTextView_borderColor, context.getResources().getColor(R.color.colorBlack)));  //设置描边颜色
        } else {
            borderText.setTextColor(Color.BLACK);
        }

    }

    public TextView getBorderTextView() {
        return borderText;
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        borderText.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        CharSequence tt = borderText.getText();

        //两个TextView上的文字必须一致
        if (tt == null || !tt.equals(this.getText())) {
            borderText.setText(getText());
            this.postInvalidate();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        borderText.measure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        borderText.layout(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        borderText.draw(canvas);
        super.onDraw(canvas);
    }

}