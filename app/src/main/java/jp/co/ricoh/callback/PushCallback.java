package jp.co.ricoh.callback;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;

import jp.co.ricoh.bean.MessageEvent;

public class PushCallback implements MqttCallback {
    private static final String TAG = "PusherCallback";

    @Override
    public void connectionLost(Throwable cause) {
        Log.i(TAG, "连接失败");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.i(TAG, "消息到达");
        String msg = new String(message.getPayload());
        EventBus.getDefault().post(new MessageEvent(msg));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.i(TAG, "消息成功发送");
    }
}
