package com.example.luka.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Button button0;
    Button button1;
    Button button2;
    Button button3;
    ImageView image;

    int chosenCeleb = 0;
    int locationOfCorrectAnswer = 0;
    String [] answers = new String[4];

    ArrayList<String> celebUrls = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button0 = (Button) findViewById(R.id.button0);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        image = (ImageView) findViewById(R.id.imageView);

        ImageDownloader imageTask = new ImageDownloader();
        Bitmap myImage;

        DownloadTask task = new DownloadTask();
        String result = null;

        try {
            result = task.execute("http://www.posh24.se/kandisar").get();
            String[] splitPage = result.split("<div class=\"sidebarContainer\">");

            String allImages = "";
            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(splitPage[0]);

            while(m.find()){
                celebUrls.add(m.group(1));
            }

            String allNames = "";
             p = Pattern.compile("alt=\"(.*?)\"/>");
             m = p.matcher(splitPage[0]);

            while(m.find()){
                celebNames.add(m.group(1));
            }

            Log.i("Content of Url", allNames);
            Log.i("Content of Url", m.toString());
            Log.i("Test", result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Random random = new Random();
        chosenCeleb = random.nextInt(celebUrls.size());

        ImageDownloader imageDownloader = new ImageDownloader();
        Bitmap celebImage;

        try {
            celebImage = imageDownloader.execute(celebUrls.get(chosenCeleb)).get();
            image.setImageBitmap(celebImage);


            locationOfCorrectAnswer = random.nextInt(4);
            int incorectAnswerLocation;

            for(int i = 0; i < 4; i++){
                if(i == locationOfCorrectAnswer){
                    answers[i] = celebNames.get(chosenCeleb);
                }
                else {
                    incorectAnswerLocation = random.nextInt(celebUrls.size());

                    while(incorectAnswerLocation == locationOfCorrectAnswer){
                        incorectAnswerLocation = random.nextInt(celebUrls.size());
                    }
                    answers[i] = celebNames.get(incorectAnswerLocation);
                }
            }
            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        createNew();

    }

    public void createNew() {
        Random random = new Random();
        chosenCeleb = random.nextInt(celebUrls.size());

        ImageDownloader imageDownloader = new ImageDownloader();
        Bitmap celebImage;

        try {
            celebImage = imageDownloader.execute(celebUrls.get(chosenCeleb)).get();
            image.setImageBitmap(celebImage);


            locationOfCorrectAnswer = random.nextInt(4);
            int incorectAnswerLocation;

            for (int i = 0; i < 4; i++) {
                if (i == locationOfCorrectAnswer) {
                    answers[i] = celebNames.get(chosenCeleb);
                } else {
                    incorectAnswerLocation = random.nextInt(celebUrls.size());

                    while (incorectAnswerLocation == locationOfCorrectAnswer) {
                        incorectAnswerLocation = random.nextInt(celebUrls.size());
                    }
                    answers[i] = celebNames.get(incorectAnswerLocation);
                }
            }
            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    public void guess(View view){
        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))){
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            createNew();
        }
        else{
            Toast.makeText(this, "Wrong, it was " + celebNames.get(chosenCeleb), Toast.LENGTH_SHORT).show();
            createNew();
        }
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {

            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();

                Bitmap myBitMap = BitmapFactory.decodeStream(inputStream);
                return myBitMap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try{

                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            }
            catch (Exception e){
                e.printStackTrace();
                return "failed;";
            }

        }
    }
}
