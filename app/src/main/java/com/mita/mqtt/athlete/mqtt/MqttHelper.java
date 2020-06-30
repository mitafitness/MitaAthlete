package com.mita.mqtt.athlete.mqtt;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import static com.mita.mqtt.athlete.activity.CurrnetActivityDevNew.AudioTOPIC;

public class MqttHelper {

    public MqttAndroidClient mqttAndroidClient;

    //    final String serverUri = "tcp://maqiatto.com:1883";
    static String serverUri = Param.myUrl; //"tcp://192.168.1.167:1883";
//    final String serverUri ="tcp://192.168.1.167:1883";

    final String clientId = "publisher";
    //    final String mSubscriptionTopicOne = "TOPIC/U_LOKESH_200/SENSOR_200_1";
    private final String mSubscriptionTopicOne = "/TOPIC/COACHID/ATHLETEID/FEEDBACK/REALRUN";
    final String mSubscriptionTopicTwo = "heartsensor";
    final String mRandomTopic = "wenzins";

    final String username = "mita1";
    final String password = "Bangalore123";
    TextView myMsg;
    static int msgcount = 0;

    public MqttHelper(Context context, TextView tvmsg) {
        myMsg = tvmsg;
        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.w("Debug", s);
                myMsg.setText("Connected");
            }

            @Override
            public void connectionLost(Throwable throwable) {
                myMsg.setText("Connection Lost");

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug", mqttMessage.toString());

                myMsg.setText("Msg Arrived");

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                msgcount++;
                myMsg.setText("Msg Delivered - " + msgcount);
            }
        });
        connect();
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    public void publishData(String data, String mqttTopic) {
//        MqttMessage message = new MqttMessage(data.getBytes());
        MqttMessage message = new MqttMessage();
        try {
            message.setPayload(data.getBytes());
//            mqttAndroidClient.publish(mSubscriptionTopicOne,message);
            mqttAndroidClient.publish(mqttTopic, message);
           // Log.i("==MqttTopic", mqttTopic);
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public void publishSensorTwoData(String data) {
//        MqttMessage message = new MqttMessage(data.getBytes());
//        MqttConnectOptions options = new MqttConnectOptions();
//        options.setWill(mRandomTopic,data.getBytes(),2,false);

        MqttMessage message = new MqttMessage();
        try {
            message.setPayload(data.getBytes());
            mqttAndroidClient.publish(mRandomTopic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());

        try {

            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    Log.i("Debug", "Published!");

                    //need to commit for publisher
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i("Debug", "Failed to connect to: " + serverUri + " " + exception.toString());
                }
            });


        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }


    private void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(AudioTOPIC, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w("Debug", "from coach Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Debug", "from coach Subscribed fail!");
                }
            });

        } catch (MqttException ex) {
            System.err.println("Exceptionst subscribing");
            ex.printStackTrace();
        }
    }

    public void unsubscribeToTopic() {

        try {
            IMqttToken unsubscriptionToken = mqttAndroidClient.unsubscribe(mSubscriptionTopicOne);
            unsubscriptionToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w("Debug", "UnSubscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
