package com.mita.mqtt.athlete.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.mita.mqtt.athlete.R;
import com.mita.mqtt.athlete.mqtt.MqttHelper;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


// audio test class
public class AudioSubscriber extends AppCompatActivity {

    private MqttHelper mqttHelper;
    private File file2;
    private MediaRecorder mediaRecorder;
    public static final int RequestPermissionCode = 1;
    private MediaPlayer mediaPlayer;
    Random random;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_subscriber);
        startMqttSubscribeCoach();
        if (ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AudioSubscriber.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                    1);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
    }

    private void startMqttSubscribeCoach() {
        TextView tvmsg = findViewById(R.id.tvmsg);
        mqttHelper = new MqttHelper(getApplicationContext(), tvmsg);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.i("DebugCur", "SubScribed Sucessfully!!!!");

            }

            @Override
            public void connectionLost(Throwable throwable) {
                Log.w("Debug", " SubScribe ConnectionLost - " + throwable.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                //   Log.w("Debug",mqttMessage.toString());
                // Log.i("==Debug", mqttMessage.toString());
                String message;
                JSONObject mainObj = new JSONObject();
                JSONArray ja = new JSONArray();
                // if (TOPIC.equals(topic)) {
                message = new String(mqttMessage.getPayload());
                Log.i("==MqttMsgAthlete", message);

                JSONObject jsonObj = new JSONObject(message);
                String AudioMsg = jsonObj.getString("audio_string");
              //  Log.i("==MqttMsgAthlete", AudioMsg);
                byte[] decoded = Base64.decode(AudioMsg, 0);
                 Log.i("===Decoded: ", String.valueOf(decoded));
                if (checkPermission()) {
                    try {
                        Random random = new Random();
                        int ii = 100000;
                        ii = random.nextInt(ii);
                        file2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +ii+"audio1.3gp");
                        FileOutputStream os = new FileOutputStream(file2, true);
                        os.write(decoded);
                        os.close();
                        Log.i("===DecodedFile", String.valueOf(file2));
                        MediaRecorderReady();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("==CatchEx",e.getMessage());
                    }
                      mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(String.valueOf(file2));
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
                } else {
                    Toast.makeText(AudioSubscriber.this, "222", Toast.LENGTH_SHORT).show();

                    requestPermission();
                }


            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                Log.i("Debug", "DeliveryCompleted : " + iMqttDeliveryToken.toString());
            }
        });

    }

    public void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mediaRecorder.setOutputFile(file2);
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(AudioSubscriber.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(AudioSubscriber.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AudioSubscriber.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

}
