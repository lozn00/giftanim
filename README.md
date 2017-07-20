
 图片演示

![演示图片地址](https://github.com/qssq/giftanim/blob/master/Pictures/1.gif)

![演示图片地址](https://github.com/qssq/giftanim/blob/master/Pictures/anim.gif)

视频demo演示

http://v.youku.com/v_show/id_XMjg3ODMyNDc3Ng==.html?spm=a2h3j.8428770.3416059.1
#Live giftanim 经典的直播礼物动画 你值得拥有！

集成起来非常简单

##### 使用方法

```
 compile 'cn.qssq666:giftanim:0.1'
```


布局定位到与与一条线对齐并包裹内容，或者外面嵌套一个帧布局然后底部对齐也是可以的。
`

		   <cn.qssq666.giftmodule.periscope.GiftAnimLayout
		        android:id="@+id/giftlayout"
		        android:layout_width="match_parent"
		        android:layout_height="wrap_content"
		        android:layout_above="@+id/line"></cn.qssq666.giftmodule.periscope.GiftAnimLayout>


`






您可以完全不用设置，也可以设置，



`

       giftAnimLayout.setOnGiftBarClick(new GiftAnimLayout.OnGiftBarClick() {
            @Override
            public void onClick(UserInfoI userInfo) {
                Toast.makeText(MainActivity.this, "你点击了" + userInfo.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        giftAnimLayout.setGiftAdapterAndCallBack(new GiftBarAdapter());//具体作用看GiftBarAdapter注释
        giftAnimLayout.setMaxShowCount(3);
        giftAnimLayout.setHidenAnim(R.anim.follow_anim_from_left_vertical_hidden);//  giftAnimLayout.setHidenAnim(R.anim.follow_anim_from_left_to_right_hidden);
        giftAnimLayout.setShowDuration(6000);
        giftAnimLayout.setThanMaxWait(false);//


`


首先项目是2016年写的，后面都废弃了 最近又做直播了，但是我发现我这个东西还有很多bug,经过修修改改，终于达到了我想要的效果。 代码实现也比较简单。


另外我感觉开源是我继续敲代码的动力，希望各位多多点赞，让更多人知道  力图最简单的方式 最快的速度实现需求 简单易懂方便扩展和抽取 是我的目标 和理想。



* 支持gift图片地址
* 支持自己维护x1 x2总数
* 支持x1 x2条件调节
* 上面所有的方法都不是必须填写的  都默认已经调整好。
* 支持礼物动画 x1 x2 x3 缩放效果
* 支持自定义 显示动画 和移除动画  移除动画已经上了例子。
* 支持自己维护x1 x2 x3
* 支持最新礼物自动插底显示  2017-07-09 完工
* 支持礼物总数爆表压力测试，将自动排队 
* 确保所有礼物执行完毕
* 支持交叉显示动画  已显示的又收到了和另外一个已显示交叉位置 2017-07-09 完工
* 支持view缓存  2017-07-09 完工
* 支持自定义布局扩展
* 支持自定义图片加载方式  2017-07-08完工
* 支持扩展 ，简单的布局动画用户轻松扩展
* 方便自定义 礼物的x1 x2 x3是由 userid和礼物地址控制的如果不需要连x的动画可以让每一个礼物返回的key不一样 继承礼物动动画类，复写这个方法，就行这个方法叫ggetKey(UserInfoI userInfo, GiftModelI giftModel)可以返回一个时间戳就行，
* 支持自定义交叉值控制 当礼物已存在但不在最靠屏幕中间的，如果 这个礼物超过了最靠屏幕中间已经积累的值的指定大小就发生一次交叉动画 ，setAcrossDValue 比如设置5 如果不管是多少度交叉就设置更大的数值或者设置为Integer的最大值



简单的接口实现 giftmodel userinfo,用户轻松解耦，

唯一标识 是由userInfo的getUserid+礼物地址组成


setGiftAdapterAndCallBack 可以实现高度定置化 








```


点赞动画布局

`			
			
			  cn.qssq666.giftanim.periscope.FavorLayout
			        android:id="@+id/favorlayout"
			        android:layout_width="match_parent"
			        android:layout_height="match_parent"
			        android:layout_marginLeft="@dimen/theme_margin"
			        android:layout_marginTop="@dimen/theme_margin" />
			
			
`

触发点赞代码逻辑

`

 	  favorLayout.addFavor();

`


礼物动画定制化演示




				  
				public class GiftBarAdapter implements GiftAnimLayout.GiftCallBack {
				
				
				    private static final String TAG = "GiftBarAdapter";
				
				    @Override
				    public void onGiftAnimOver(GiftModelI giftModel) {
				        ((GiftDemoModel) giftModel).setShowcount(0);//如果是0则会直接设置为1的
				        Log.w(TAG, "onGiftAnimOver:" + giftModel);
				    }
				
				    @Override
				    public void onFindExistGiftAnim(GiftModelI giftModel) {
				//                ((GiftModel) giftModel).setShowcount(1);
				        Log.w(TAG, "onFindExistGiftAnim:" + giftModel);
				
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
				        //返回null表示内部本来就有的那个
				        return (ViewGroup) LayoutInflater.from(giftAnimLayout.getContext()).inflate(R.layout.view_live_gift_bar_prescro, giftAnimLayout, false);
				    }
				
				    /**
				     * 返回false表明内部进行绑定图片 那么这里就没必要再进行处理了。但是这里要维护的话头像也给维护了哈!
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



上面的注释很清楚了。默认的imageloader已经维护好加载了，如果要gift动画 那么就是上面的例子效果了。


  实际上demo的代码已经设置好了adapterAndCallBack 您也可以完全不用设置，然后跑跑代码 也没毛病。 哈哈。

自定义布局 等 就是new一个 callback 

    giftAnimLayout.setGiftAdapterAndCallBack(new GiftBarAdapter());//具体作用看GiftBarAdapter注释