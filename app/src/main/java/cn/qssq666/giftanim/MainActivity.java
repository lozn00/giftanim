package cn.qssq666.giftanim;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import cn.qssq666.giftmodule.bean.GiftModel;
import cn.qssq666.giftmodule.bean.UserInfo;
import cn.qssq666.giftmodule.interfacei.GiftModelI;
import cn.qssq666.giftmodule.interfacei.UserInfoI;
import cn.qssq666.giftmodule.periscope.FavorLayout;
import cn.qssq666.giftmodule.periscope.GiftAnimLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FavorLayout favorLayout;
    private GiftAnimLayout giftAnimLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_zan:
                favorLayout.addFavor();
                break;
            case R.id.btn_gift:
                UserInfo info = new UserInfo();
                String userId = new Random().nextInt(5) + "";
                info.setUserId("" + userId);
                int i = new Random().nextInt(imgs.size());
                info.setPortraitUri("http://www.showself.com/yule/uploadfile/2016/0712/20160712060703783.jpg");
                info.setName("情随事迁" + i);
                GiftModelI model = new GiftModel(100 + i, imgs.get(i));
//                giftAnimLayout.productAndShow(this, info, model);
                giftAnimLayout.showNewGift(this, info, model);
                break;


        }
    }

    ArrayList<String> imgs = new ArrayList<>();

    {
      /*  imgs.add("http://img0.imgtn.bdimg.com/it/u=3224008386,3644745976&fm=23&gp=0.jpg");
        imgs.add("http://img5.imgtn.bdimg.com/it/u=3014315087,1285288972&fm=23&gp=0.jpg");
        imgs.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3663589651,1780630636&fm=23&gp=0.jpg");*/
        imgs.add("drawable://" + R.drawable.zan_1_bear);
        imgs.add("drawable://" + R.drawable.zan_2_cat);
  /*        imgs.add("drawable://" + R.drawable.zan_3_circle);
        imgs.add("drawable://" + R.drawable.zan_4_heart);
        imgs.add("drawable://" + R.drawable.zan_5_pig);
        imgs.add("drawable://" + R.drawable.zan_6_sheep);
        imgs.add("drawable://" + R.drawable.zan_7_rabbit);
        imgs.add("drawable://" + R.drawable.zan_8_spotty);
        imgs.add("drawable://" + R.drawable.zan_9_dog);*/
    }

    /**
     * 如何自己维护x 1 x2 解决 已经x到了20多的时候新用户直接看到了，呢 创建一个字段 为 showcount 默认值为 0
     * 首先 礼物模型必须保持在内存中一直存在 ，也就是点击相同礼物还是原来的对象，不会创建新的对象，
     * （还是实现礼物模型的）  在用户赠送礼物的时候判断 showcount==0?如果=0就依次加
     * 然后在 {@link GiftAnimLayout#setGiftCallBack} onGiftAnimOver 重新设置showcount=0
     * 最后 礼物模型送过去的是携带showcount的    会在 onRequestShowGiftCount 调用的。 如果不维护 那么内部会检测是否showcount==0
     */
}
