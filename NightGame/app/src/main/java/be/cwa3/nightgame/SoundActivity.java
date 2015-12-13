package be.cwa3.nightgame;

/**
 * Created by jesse on 26/10/2015.
 */
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import be.cwa3.nightgame.Utils.SoundHelper;
import ca.uol.aig.fftpack.RealDoubleFFT;

public class SoundActivity extends AppCompatActivity implements OnClickListener {

    int frequency = 44100;
    int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    private RealDoubleFFT transformer;
    int blockSize = 1024;
    double[] soundData;

    Button startStopButton;
    boolean started = false;

    RecordAudio recordTask;

    ImageView imageView;
    ListView recognizedListView;
    TextView recognizedTextview;
    Bitmap bitmap;
    Canvas canvas;
    Paint paint;

    //AudioRecord audioRecord;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);
        soundData = new double[blockSize];
        Arrays.fill(soundData, 0);
        startStopButton = (Button) findViewById(R.id.StartStopButton);
        recognizedTextview = (TextView) findViewById(R.id.recognized_sound);
        recognizedListView = (ListView) findViewById(R.id.recognized_sound_list);

        startStopButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d("sound", "started");
                    recognizedTextview.setText("");
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                            android.R.layout.simple_list_item_1, android.R.id.text1, new ArrayList<String>());
                    recognizedListView.setAdapter(adapter);
                    started= true;
                    startStopButton.setText("Buzy");
                    recordTask = new RecordAudio();
                    recordTask.execute();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    started = false;
                    startStopButton.setText("Waiting");
                    recordTask.cancel(true);
                    List<Double> abssums = new ArrayList<>();
                    double thisSum = 0;
                    int begin = 0;
                    int eind = (4000 * blockSize / frequency);
                    for(int g =begin; g<eind; g++) {
                        int freq = g* frequency / blockSize;
                        int freqnext = (g+1)*frequency / blockSize;
                        if(Math.floor(freqnext/100) > Math.floor(freq/100) || g==eind-1){
                            thisSum += Math.abs(soundData[g]);
                            abssums.add(thisSum);
                            thisSum = 0;
                        }
                        //Log.d("sound", String.valueOf(freq));
                    }
                    recognizedTextview.setText(SoundHelper.getBestAnswer(abssums));
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                            android.R.layout.simple_list_item_1, android.R.id.text1, SoundHelper.getAnswerList(abssums));
                    recognizedListView.setAdapter(adapter);
                    Log.d("sound", new Gson().toJson(abssums));
                    Arrays.fill(soundData, 0);
                }
                return false;

            }
        });

        transformer = new RealDoubleFFT(blockSize);

        imageView = (ImageView) this.findViewById(R.id.ImageView1);
        bitmap = Bitmap.createBitmap((int) 256, (int) 100,
                Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        paint.setColor(Color.GREEN);
        imageView.setImageBitmap(bitmap);

    }

    public class RecordAudio extends AsyncTask<Void, double[], Void> {

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                // int bufferSize = AudioRecord.getMinBufferSize(frequency,
                // AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
                int bufferSize = AudioRecord.getMinBufferSize(frequency,
                        channelConfiguration, audioEncoding);

                AudioRecord audioRecord = new AudioRecord(
                        MediaRecorder.AudioSource.MIC, frequency,
                        channelConfiguration, audioEncoding, bufferSize);

                short[] buffer = new short[blockSize];
                double[] toTransform = new double[blockSize];

                audioRecord.startRecording();

                // started = true; hopes this should true before calling
                // following while loop

                while (started) {
                    int bufferReadResult = audioRecord.read(buffer, 0,
                            blockSize);

                    for (int i = 0; i < blockSize && i < bufferReadResult; i++) {
                        toTransform[i] = (double) buffer[i] / 32768.0; // signed
                        // 16
                    }                                       // bit
                    transformer.ft(toTransform);
                    publishProgress(toTransform);



                }

                audioRecord.stop();

            } catch (Throwable t) {
                t.printStackTrace();
                Log.e("AudioRecord", "Recording Failed");
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(double[]... toTransform) {

            canvas.drawColor(Color.BLACK);

            for (int i = 0; i < toTransform[0].length; i++) {
                int x = i;
                int downy = (int) (100 - (toTransform[0][i] * 10));
                int upy = 100;

                canvas.drawLine(x, downy, x, upy, paint);
            }

            imageView.invalidate();


            //double[] old;
            //old = toTransform[0].clone();
            //Arrays.sort(old);
            /*
            int n= 0;
            for(double value : toTransform[0]){
                Log.d("test", String.format("%d Hz: %f", n * frequency / blockSize, value));
                n++;
            }*/
            /*
            for(int g =0; g<5; g++){
                double toSearch = old[g];
                int k =0;
                for(double value : toTransform[0]) {
                    if(value == toSearch){
                        Log.d("test", String.format("%d %d Hz: %f", g, k * frequency / blockSize, value));
                    }
                    k++;
                }
            }*/
            for(int i=0; i<toTransform[0].length; i++){
                soundData[i] += toTransform[0][i];
            }
            // WERKEND
            /*double abssum = 0;
            int begin = (12000 * blockSize / frequency);
            int eind = (17000 * blockSize / frequency);
            for(int g =begin; g<eind; g++) {
                abssum += Math.abs(old[g]);
            }
            if(abssum > 5) {
                //Log.d("test", String.valueOf(abssum));


            List<Double> abssums = new ArrayList<>();
            double thisSum = 0;
            begin = 0;
            eind = (20000 * blockSize / frequency);
            for(int g =begin; g<eind; g++) {
                int freq = g* frequency / blockSize;
                int freqnext = (g+1)*frequency / blockSize;
                if(freqnext/1000 > freq/1000){
                    thisSum += Math.abs(old[g]);
                    abssums.add(thisSum);
                    thisSum = 0;
                }
            }
            Log.d("test", new Gson().toJson(abssums));
            }*/
            /*
            String string = new Gson().toJson(toTransform);
            FileWriter outputStream;


             //external writing
             try {
                 outputStream = new FileWriter(Environment.getExternalStorageDirectory().toString() +"/aData.txt",true);
                 Log.d("key", Environment.getExternalStorageDirectory().toString());
                 outputStream.append(string);
                 outputStream.close();
             } catch (Exception e) {
                 e.printStackTrace();
             }*/

            // TODO Auto-generated method stub
            // super.onProgressUpdate(values);
        }

    }

    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        if (started) {
            started = false;
            startStopButton.setText("Start");
            recordTask.cancel(true);
        } else {
            started = true;
            startStopButton.setText("Stop");
            recordTask = new RecordAudio();
            recordTask.execute();
        }
    }
}
