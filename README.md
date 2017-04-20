# giftanim
送赞送礼物动画 仿映客礼物侧栏动画效果，2016年的老东西了，老板都删除直播功能了，就分享给大家了。。

支持礼物动画 x1 x2 x3
支持用户uersid和图片保持排斥以解决应该x1不。

包含点赞动画和 送礼物侧拉动画。

```
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
