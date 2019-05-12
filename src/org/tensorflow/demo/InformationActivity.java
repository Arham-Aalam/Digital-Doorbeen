package org.tensorflow.demo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.JsonReader;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.lang.Math.abs;


public class InformationActivity extends AppCompatActivity {

    public static final String LOG_TAG = "forPrediction";
    private List<RectF> predictions;
    private List<String> predictionTitles;
    TextView text, description, percentage;
    ProgressBar progressBar;
    private Bitmap bitmap = null;

   public void onCreate(Bundle savedInstanceState){
       super.onCreate(savedInstanceState);
       setContentView(R.layout.information_display);
       text = (TextView) findViewById(R.id.title);
       description = (TextView) findViewById(R.id.description);
       percentage = (TextView) findViewById(R.id.percentage);
       progressBar = (ProgressBar) findViewById(R.id.progress);

       final String results;

       Intent intent = getIntent();
       results = intent.getStringExtra("message");
       text.setText(results);


       Bitmap bmp;
      // Bitmap resizedbitmap = Bitmap.createBitmap(bmp, tx , ty, abs(bx - tx), abs(by - ty));
       //imageView.setImageBitmap(resizedbitmap);


       final ImageView imageView = (ImageView) findViewById(R.id.imageView1);

       byte[] byteArray = getIntent().getByteArrayExtra("image");
       String speechText = getIntent().getStringExtra("speechText");
       predictions = (List<RectF>) getIntent().getSerializableExtra("predictions");
       predictionTitles = (List<String>) getIntent().getSerializableExtra("predictionTitles");
       bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
       imageView.setImageBitmap(bmp);

       //text.setText(speechText);
       /*
       if (predictions == null) {
           Toast.makeText(getApplicationContext(), "Predictions Are null", Toast.LENGTH_SHORT).show();
       } else {
           Toast.makeText(getApplicationContext(), "Got the predictions!", Toast.LENGTH_SHORT).show();
       }
       System.out.println("Speech Text ::::\n" + speechText);
       int count = 0;


       for (RectF rect : predictions) {

           System.out.println("pred : " + rect.toString());
           System.out.println("+++++++++++++++++++++++++++ L :" + rect.left + "  T:" + rect.top + " B:" + rect.bottom + " R:" + rect.right);

       }
       */

       String[] arrOfStr = speechText.split("\n", 5);

       for (String a : arrOfStr) {
           if( a.equals("1") || a.equals("2") ||  a.equals("3") || a.equals("4") && predictions.size() > 0) {

               System.out.println("First Rectangle =  "+  predictions.get(0));
               //flag = true;
               int b = Integer.parseInt(a);
               System.out.println("b = "+ b);
               final String predTitle = predictionTitles.get(b-1);
               RectF rect = predictions.get(b-1);
               System.out.println("calc : " + (int)(rect.centerX()) + "  " + (int)(rect.centerY()) + "  " + (int)rect.width() + "    " + (int)rect.height());
               //int x = (int)(rect.centerX() - rect.width()/2);
               //int y = (int)(rect.centerY() - rect.height()/2);
               int x = (int)rect.centerX();
               int y = (int)(rect.centerY());

               //System.out.println("calc : " + x + "  " + y + "  " + rect.width() + "    " + rect.height());
               if((int)bmp.getWidth() < x +  rect.width()) {
                   x = ((int)(x +  rect.width() - bmp.getWidth()) - x);
               }
               if((int)bmp.getHeight() < y + rect.height()) {
                   y = ((int)(y + rect.height() - bmp.getHeight()) - y);
               }

               for(String title: predictionTitles) {
                   System.out.println("pred-> " + title);
               }
               //Arham Work - changes
                /*
                //Bitmap resizedbitmap = Bitmap.createBitmap(bmp, x, y, (int)rect.width()/2, (int)rect.height()/2);
                Bitmap resized = Bitmap.createScaledBitmap(bmp, 720, 1280, true);
                //imageView.setImageBitmap(resized);
                int tx = (int) rect.left;
                int ty = (int) rect.top;
                int bx = (int) rect.right;
                int by = (int) rect.bottom;
                Bitmap resizedbitmap = Bitmap.createBitmap(resized, tx , ty, bx, by);
                imageView.setImageBitmap(resizedbitmap);
                */


               //My chages using rgbFrameBitmap Image
//Bitmap resizedbitmap = Bitmap.createBitmap(bmp, x, y, (int)rect.width()/2, (int)rect.height()/2);
               //Bitmap resized = Bitmap.createScaledBitmap(bmp, 720, 1280, true);
               //imageView.setImageBitmap(resized);
               int tx = (int) rect.left;
               int ty = (int) rect.top;
               int bx = (int) rect.right;
               int by = (int) rect.bottom;

               if (tx <= 0) {
                   tx = 1;
               }
               if (ty <= 0) {
                   ty = 1;
               }
               final Bitmap resizedbitmap = Bitmap.createBitmap(bmp, tx , ty, abs(bx - tx), abs(by - ty));
               Matrix matrix = new Matrix();

               matrix.postRotate(90);

               Bitmap scaledBitmap = Bitmap.createScaledBitmap(resizedbitmap, resizedbitmap.getWidth(), resizedbitmap.getHeight(), true);
               final Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

               imageView.setImageBitmap(rotatedBitmap);

             //  ByteArrayOutputStream baos = new ByteArrayOutputStream();
               // resizedbitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
               makeOtherReq(predTitle);
           }
       }
   }

   public void makeOtherReq(String predTitle) {
           String validTitle =  Character.toUpperCase(predTitle.charAt(0)) + predTitle.substring(1);
           Toast.makeText(getApplicationContext(), validTitle, Toast.LENGTH_SHORT).show();
           String wikiURL = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles=" + validTitle;
            Content contentReq = new Content(wikiURL, predTitle);
            contentReq.execute();
           /*
           RequestQueue requestQ = Volley.newRequestQueue(getApplicationContext());
           StringRequest imgJsonReq = new StringRequest(
                   Request.Method.GET,
                   wikiURL,
                   new Response.Listener<String>() {
                       @Override
                       public void onResponse(String response) {
                           if (null != response) {
                               try {
                                   JSONObject jsonObject = new JSONObject(response);
                                   JSONObject queryObject = jsonObject.getJSONArray("normalized").getJSONObject(0);
                                   String fromTitle = queryObject.getString("from");
                                   String toTitle = queryObject.getString("to");
                                   System.out.println( "got_response" + fromTitle + " " + toTitle);

                                   JSONObject pagesObject = queryObject.getJSONObject("pages");
                                   Iterator<String> iterator = pagesObject.keys();

                                   List<InfoModel> info = new ArrayList<>();

                                   while(iterator.hasNext()) {
                                       String key = iterator.next();
                                       if (pagesObject.get(key) instanceof JSONObject) {
                                           info.add(new InfoModel(pagesObject.getJSONObject(key).getString("title"), pagesObject.getJSONObject(key).getString("extract")));
                                       }
                                   }
                                   for (InfoModel content: info) {
                                       System.out.println("ex-> " + content.getExtracter());
                                   }

                               } catch (JSONException e) {
                                   e.printStackTrace();
                               }
                           } else {
                               Toast.makeText(getApplicationContext(), "Unable to get Info!!", Toast.LENGTH_SHORT).show();
                               finish();
                           }
                       }
                   },
                   new Response.ErrorListener() {
                       @Override
                       public void onErrorResponse(VolleyError error) {
                           Toast.makeText(getApplicationContext(), "Unable to get Info!!", Toast.LENGTH_SHORT).show();
                       }
                   }
           );
           requestQ.add(imgJsonReq);
       //////////////////////
       */
   }

    private class Content extends AsyncTask<Void, Void, Void> {

       String url;
        List<InfoModel> info = null;
        String title = null;

        public Content(String url, String title) {
            this.url = url;
            this.title = title;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String response = Jsoup.connect(this.url).ignoreContentType(true).execute().body();
                //System.out.println(response + " =resp");
                JSONObject jsonObject = new JSONObject(response);
                JSONObject pagesObject = jsonObject.getJSONObject("query").getJSONObject("pages");
                Iterator<String> iterator = pagesObject.keys();

                info = new ArrayList<>();

                while(iterator.hasNext()) {
                    String key = iterator.next();
                    if (pagesObject.get(key) instanceof JSONObject) {
                        info.add(new InfoModel(pagesObject.getJSONObject(key).getString("title"), pagesObject.getJSONObject(key).getString("extract")));
                    }
                }
                for (InfoModel content: info) {
                    System.out.println("ex-> " + content.getExtracter());
                }

            }
            catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Unable to get Info!!", Toast.LENGTH_SHORT).show();
                finish();
                e.printStackTrace();
            }
            catch (JSONException e) {
                //Toast.makeText(getApplicationContext(), "JSON error!!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            text.setText(this.title);
            progressBar.setVisibility(View.GONE);
            description.setVisibility(View.VISIBLE);
            if(null != info) {
                String s = "";
                for(InfoModel im: info) {
                    s += "- ";
                    s += im.getExtracter();
                    s += "\n\n";
                }
                description.setText(s);
            } else {
                description.setText("Unable to find anything related to this image");
            }
        }
    }
}

/*
    String fullPath = "https://desolate-thicket-82809.herokuapp.com/" + path;
    ByteArrayInputStream bs = new ByteArrayInputStream(imagebyte);

    String url = "http://saucenao.com/search.php";
    String userAgent = "Mozilla/66.0.3 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.108 Safari/537.36";

            try {
                Document document = Jsoup
                        .connect(url)
                        .method(Connection.Method.POST)
                        .userAgent(userAgent)
                        .data("file", "file", bs)
                        .post();
                Elements links = document.select("a[href]");
                for(Element link : links) {
                    System.out.println("link-> " + link.attr("abs:href"));
                }
                System.out.println(document.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }



            try {
                    Connection.Response res = Jsoup
                    .connect("https://www.tineye.com/")
                    .method(Connection.Method.GET)
                    .userAgent(userAgent)
                    .execute();

                    Map<String, String> cookies = res.cookies();
        //Document document = Jsoup.connect("https://www.google.com/searchbyimage/image_url=" + fullPath);
        //Document document = Jsoup.connect("https://www.google.com/searchbyimage?site=search&image_url==" + fullPath)
        //https://desolate-thicket-82809.herokuapp.com/0toi8.jpg
        Document document = Jsoup.connect("https://www.tineye.com/search/?url=" + fullPath)
        .cookies(cookies)
        .userAgent(userAgent)
        //.userAgent(userAgent)
        .get();
        //AIzaSyCurK8cv-fIuOgugS2Uc8Y4Gf64ontGv-E
        Elements links = document.select("a[href]");
        for(Element link : links) {
        System.out.println("link-> " + link.attr("abs:href"));
        }
        System.out.println(document.body());
        } catch (IOException e) {
        e.printStackTrace();
        }
        */

/*
Handler hr = new Handler();
               hr.post(new Runnable() {
                   @Override
                   public void run() {
                       ByteArrayOutputStream baos = new ByteArrayOutputStream();
                       rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                       final byte imagebyte[]= baos.toByteArray();
                       final String imagestring= Base64.encodeToString(imagebyte,Base64.DEFAULT);
                           RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                           String URL = "";
                           StringRequest imagePostReq = new StringRequest(
                                   Request.Method.POST,
                                   "https://desolate-thicket-82809.herokuapp.com/image",
                                   new Response.Listener<String>() {
                                       @Override
                                       public void onResponse(String response) {
                                           if(null != response) {
                                               makeOtherReq(predTitle);
                                           } else {
                                               Toast.makeText( getApplicationContext(), "no response!!", Toast.LENGTH_SHORT).show();
                                           }
                                       }
                                   }, new Response.ErrorListener() {
                               @Override
                               public void onErrorResponse(VolleyError error) {
                                   makeOtherReq(predTitle);
                               }
                           }){
                               @Override
                               public String getBodyContentType() {
                                   return "application/x-www-form-urlencoded; charset=UTF-8";
                               }

                               @Override
                               protected Map<String, String> getParams() throws AuthFailureError {
                                   Map<String, String> params = new HashMap<String, String>();
                                   params.put("image", imagestring);
                                   return params;
                               }
                           };
                           requestQueue.add(imagePostReq);
                   }
               });
 */