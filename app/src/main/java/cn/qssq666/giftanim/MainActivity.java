package cn.qssq666.giftanim;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;
import java.util.Random;

import cn.qssq666.giftmodule.BuildConfig;
import cn.qssq666.giftmodule.bean.GiftDemoModel;
import cn.qssq666.giftmodule.bean.UserDemoInfo;
import cn.qssq666.giftmodule.interfacei.GiftModelI;
import cn.qssq666.giftmodule.interfacei.UserInfoI;
import cn.qssq666.giftmodule.periscope.FavorLayout;
import cn.qssq666.giftmodule.periscope.GiftAnimLayout;

/**
 * 支持自定义布局，只需要保持id唯一即可
 * 支持自定义设置图片加载方式
 * 支持设置队列，让所有礼物都在足够的时间完成。
 * 支持自定义礼物总数 设置很简单
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FavorLayout favorLayout;
    private GiftAnimLayout giftAnimLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fresco.initialize(this);
        favorLayout = ((FavorLayout) findViewById(R.id.favorlayout));

        giftAnimLayout = ((GiftAnimLayout) findViewById(R.id.giftlayout));
        giftAnimLayout.setOnGiftBarClick(new GiftAnimLayout.OnGiftBarClick() {
            @Override
            public void onClick(UserInfoI userInfo) {
                Toast.makeText(MainActivity.this, "你点击了" + userInfo.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        giftAnimLayout.setGiftAdapterAndCallBack(new GiftBarAdapter());//具体作用看GiftBarAdapter注释
        giftAnimLayout.setMaxShowCount(3);
        giftAnimLayout.setHidenAnim(R.anim.follow_anim_from_left_vertical_hidden);
//        giftAnimLayout.setHidenAnim(R.anim.follow_anim_from_left_to_right_hidden);
        giftAnimLayout.setShowDuration(4000);
        giftAnimLayout.setThanMaxWait(false);//
        findViewById(R.id.btn_zan).setOnClickListener(this);
        findViewById(R.id.btn_gift_random).setOnClickListener(this);
        findViewById(R.id.btn_gift1).setOnClickListener(this);
        findViewById(R.id.btn_gift2).setOnClickListener(this);
        findViewById(R.id.btn_gift3).setOnClickListener(this);
        findViewById(R.id.btn_gift4).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_zan:
                favorLayout.addFavor();
                break;
            case R.id.btn_gift_random:
                //锁机
            {
                UserDemoInfo info = new UserDemoInfo();
                String userId = new Random().nextInt(5) + "";
                info.setUserId("" + userId);
                int i = new Random().nextInt(imgs.size());
                info.setPortraitUri(i % 2 == 0 ? "http://www.showself.com/yule/uploadfile/2016/0712/20160712060703783.jpg" : "http://img5.duitang.com/uploads/item/201608/18/20160818001352_PQ25B.thumb.224_0.jpeg");

                info.setName("情随事迁" + i);
                GiftModelI model = new GiftDemoModel(100 + i, imgs.get(i));
//                giftAnimLayout.productAndShow(this, info, model);
                giftAnimLayout.showNewGift(this, info, model);
            }
            break;

            case R.id.btn_gift1: {
                UserDemoInfo info = new UserDemoInfo();
                info.setName("傻妞1号");
                info.setUserId("1000");
                info.setPortraitUri(faces.get(0));
                GiftModelI model = new GiftDemoModel(imgs.get(0));
                giftAnimLayout.showNewGift(this, info, model);
                break;

            }
            case R.id.btn_gift2: {
                UserDemoInfo info = new UserDemoInfo();
                info.setName("傻妞2号");
                info.setUserId("1001");
                info.setPortraitUri(faces.get(1));
                GiftModelI model = new GiftDemoModel(imgs.get(1));
                giftAnimLayout.showNewGift(this, info, model);
                break;

            }
            case R.id.btn_gift3: {
                UserDemoInfo info = new UserDemoInfo();
                info.setName("傻妞3号");
                info.setUserId("1002");
                info.setPortraitUri(faces.get(2));
                GiftModelI model = new GiftDemoModel(imgs.get(2));
                giftAnimLayout.showNewGift(this, info, model);

                break;

            }
            case R.id.btn_gift4: {
                UserDemoInfo info = new UserDemoInfo();
                info.setName("傻妞4号");
                info.setUserId("1004");
                info.setPortraitUri(faces.get(3));
                GiftModelI model = new GiftDemoModel(imgs.get(3));
                giftAnimLayout.showNewGift(this, info, model);
                break;

            }

        }

    }

    public ArrayList<String> imgs = new ArrayList<>();
    public ArrayList<String> faces = new ArrayList<>();


    {

      /*  imgs.add("http://img0.imgtn.bdimg.com/it/u=3224008386,3644745976&fm=23&gp=0.jpg");
        imgs.add("http://img5.imgtn.bdimg.com/it/u=3014315087,1285288972&fm=23&gp=0.jpg");
        imgs.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3663589651,1780630636&fm=23&gp=0.jpg");*/
     /*   imgs.add("drawable://" + R.drawable.zan_1_bear);
        imgs.add("drawable://" + R.drawable.zan_2_cat);*/
        imgs.add("http://www.dabaoku.com/gif/ziran/017/046bt.gif");
        imgs.add("http://img1.2345.com/duoteimg/qqbiaoqing/141121299310/17.gif");
        imgs.add("http://www.showself.com/yule/uploadfile/2016/0712/20160712060703783.jpg");
        imgs.add("http://img3.imgtn.bdimg.com/it/u=1078298671,2393231834&fm=28&gp=0.jpg");
        imgs.add("res://" + BuildConfig.APPLICATION_ID + "/" + R.drawable.zan_4_heart);//FACEBOOK 框架支持
        imgs.add("res://" + BuildConfig.APPLICATION_ID + "/" + R.drawable.zan_7_rabbit);//FACEBOOK 框架支持
        imgs.add("res://" + BuildConfig.APPLICATION_ID + "/" + R.drawable.zan_9_dog);//FACEBOOK 框架支持


        faces.add("http://www.showself.com/yule/uploadfile/2016/0712/20160712060703783.jpg");
        faces.add("http://img5.duitang.com/uploads/item/201608/18/20160818001352_PQ25B.thumb.224_0.jpeg");
        faces.add("http://img0.imgtn.bdimg.com/it/u=3269603734,3398464200&fm=26&gp=0.jpg");
        faces.add("http://img5.duitang.com/uploads/item/201608/18/20160818001352_PQ25B.thumb.224_0.jpeg");
        faces.add("http://img.qzone.la/uploads/allimg/101231/1-1012310J406.jpg");
        faces.add("http://img1.imgtn.bdimg.com/it/u=2587453106,3747224829&fm=26&gp=0.jpg");
//        imgs.add("drawable://" + R.drawable.zan_2_cat);//TODO imageloader支持

  /*        imgs.add("drawable://" + R.drawable.zan_3_circle);
        imgs.add("drawable://" + R.drawable.zan_4_heart);
        imgs.add("drawable://" + R.drawable.zan_5_pig);
        imgs.add("drawable://" + R.drawable.zan_6_sheep);
        imgs.add("drawable://" + R.drawable.zan_7_rabbit);
        imgs.add("drawable://" + R.drawable.zan_8_spotty);
        imgs.add("drawable://" + R.drawable.zan_9_dog);*/

        /**
         * 如何自己维护x 1 x2 解决 已经x到了20多的时候新用户直接看到了，呢 创建一个字段 为 showcount 默认值为 0
         * 首先 礼物模型必须保持在内存中一直存在 ，也就是点击相同礼物还是原来的对象，不会创建新的对象，
         * （还是实现礼物模型的）  在用户赠送礼物的时候判断 showcount==0?如果=0就依次加
         * 然后在 {@link GiftAnimLayout#setGiftCallBack} onGiftAnimOver 重新设置showcount=0
         * 最后 礼物模型送过去的是携带showcount的    会在 onRequestShowGiftCount 调用的。 如果不维护 那么内部会检测是否showcount==0
         */
    }
}
