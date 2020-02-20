package jp.co.ricoh;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Random;

import jp.co.ricoh.bean.MessageEvent;
import jp.co.ricoh.service.MQTTService;

public class MainActivity extends AppCompatActivity {
    private Button mBtnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //启动服务
        Intent intent = new Intent(this, MQTTService.class);
        startService(intent);
        //在需要订阅时间的地方注册事件
        EventBus.getDefault().register(this);
        mBtnSend = findViewById(R.id.btn_send);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotification(MainActivity.this, "通知内容");
            }
        });
    }

    //处理事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showTheEventMessage(MessageEvent messageEvent) {
        showNotification(this, messageEvent.getMessage());
    }

    public void showNotification(Context context, String content) {
        //1.创建通知管理器
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //2.创建通知实例
        Notification notification = new Notification.Builder(context)
                .setContentTitle("通知标题")
                .setContentText(content)
                .setWhen(System.currentTimeMillis())
                //smallIcon 通知栏显示小图标
                //android5.0 之后通知栏图标都修改了，小图标不能含有RGB图层，也就是说图片不能带颜色，否则显示的就成白色方格了
                //解决方法一:为图片带颜色，targetSdkVersion改为21以下
                //解决方法二:只能用白色透明底的图片
                .setSmallIcon(R.mipmap.ic_launcher)
                //LargeIcon 下拉后显示的图标
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                //收到通知时的效果，这里是默认声音
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(this, HelloActivity.class), PendingIntent.FLAG_CANCEL_CURRENT))
                .build();
        //3.notify
        //notifyId每次要不一致，不然下一次的通知会覆盖上一次
        int notifyId = new Random().nextInt();
        notificationManager.notify(notifyId, notification);
    }

    @Override
    protected void onDestroy() {
        //取消事件订阅
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
