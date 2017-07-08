


![演示图片地址](https://github.com/qssq/giftanim/blob/master/Pictures/1.gif)

![演示图片地址](https://github.com/qssq/giftanim/blob/master/Pictures/anim.gif)

#Live giftanim 经典的直播礼物动画 你值得拥有！
送赞送礼物动画 仿映客礼物侧栏动画效果，2016年的老东西了，老板都删除直播功能了，就分享给大家了。。



支持礼物动画 x1 x2 x3
支持自己维护x1 x2 x3
支持扩展 ，简单的布局动画用户轻松扩展
简单的接口实现 giftmodel userinfo,用户轻松解耦，


支持用户uersid和图片保持排斥以解决应该x1不。

包含点赞动画和 送礼物侧拉动画。


对外方法 
支持设置自定义布局
支持设置自定义图片加载框架 主要针对某些图片。
setOnGiftBarClick 监听bar点击事件可以获取用户信息和被点击礼物。
setMaxShowCount 可控制最大显示多少条，也就是后面的将会排队等待。
setShowDuration 控制一个礼物的显示时间
showNewGift 显示一个礼物 传递context,userinfo,giftmodel 的实现类
setGiftCallBack 可以实现高度定置化 
```
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


功能扩展
 自己维护显示x1 x2 解决实际需求 进入房间后是从自定位置开始问题。
 比如实现giftmodelI接口 并创建一个showcount字段 来自服务器， 在 结束礼物的时候  onGiftAnimOver重置总数为0 用户发送一个礼物模型json应该为1 ，
   favorLayout = ((FavorLayout) findViewById(R.id.favorlayout));

        giftAnimLayout = ((GiftAnimLayout) findViewById(R.id.giftlayout));
        giftAnimLayout.setOnGiftBarClick(new GiftAnimLayout.OnGiftBarClick() {
            @Override
            public void onClick(UserInfoI userInfo) {
                Toast.makeText(MainActivity.this, "你点击了" + userInfo.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.btn_zan).setOnClickListener(this);
        findViewById(R.id.btn_gift).setOnClickListener(this);
        
        
            @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_zan://纯粹点赞 
               favorLayout.addFavor();
                break;
            case R.id.btn_gift://某某送礼物 
             UserInfo info = new UserInfo();
                String userId = new Random().nextInt(5) + "";
                info.setUserId("" + userId);
                int i = new Random().nextInt(imgs.size());
                info.setName("情随事迁" + i);
                GiftModel model = new GiftModel(100 + i, imgs.get(i));
//                giftAnimLayout.productAndShow(this, info, model);
                giftAnimLayout.showNewGift(this, info, model);
                break;


        }
    }
```
送礼动画布局
```
   <cn.qssq666.giftanim.periscope.GiftAnimLayout
        android:id="@+id/giftlayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"></cn.qssq666.giftanim.periscope.GiftAnimLayout>

  


```
点赞动画布局
```
  cn.qssq666.giftanim.periscope.FavorLayout
        android:id="@+id/favorlayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_marginLeft="@dimen/theme_margin"
        android:layout_marginTop="@dimen/theme_margin" />
 ```
 demo布局
 ```
 <?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/colorAccent"
    tools:context="cn.qssq666.giftanim.MainActivity">


    <cn.qssq666.giftanim.periscope.GiftAnimLayout
        android:id="@+id/giftlayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"></cn.qssq666.giftanim.periscope.GiftAnimLayout>

    <cn.qssq666.giftanim.periscope.FavorLayout
        android:id="@+id/favorlayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_marginLeft="@dimen/theme_margin"
        android:layout_marginTop="@dimen/theme_margin" />

    <LinearLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignParentBottom="true"
        android:layout_alignParentRight="true"
        android:layout_margin="@dimen/theme_margin"
        android:orientation="horizontal">

        <Button
            android:id="@+id/btn_zan"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="赞" />

        <Button
            android:id="@+id/btn_gift"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="礼物" />

    </LinearLayout>


</RelativeLayout>

```
 所以说我这个还是很简单的啦
 
 2017年6月22日 16:53:25
 抽成 模块 并把礼物抽成接口。 方便定制。
