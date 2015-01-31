package drankoDmitry.learningcards;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class YandexTranslateHelper {
    private static final String API_KEY = "trnsl.1.1.20150114T230720Z.5fd1169244869827.46e50baa6059a3dd2a9a4742d5b541d6216e70c8";
    private static final String URL_LANG = "https://translate.yandex.net/api/v1.5/tr.json/getLangs?key=" + API_KEY + "&ui=en";
    private ImportFileActivity activity;
    private Context context;
    private String lang1;
    private String lang2;


    public void getLanguages(ImportFileActivity _activity) {
        activity = _activity;
        new HttpLangTask().execute();

    }

    public void translateAndAdd(List<ContentValues> valuesList, String _lang1, String _lang2, Context ctx) {
        context = ctx;
        lang1 = _lang1;
        lang2 = _lang2;
        new HttpTranslateTask().execute(valuesList);
    }

    private class HttpLangTask extends AsyncTask<String, Void, List<String>> {
        @Override
        protected List<String> doInBackground(String... args) {

            InputStream inputStream;
            List<String> res = new LinkedList<String>();
            try {
                HttpClient httpclient = new DefaultHttpClient();

                HttpResponse httpResponse = httpclient.execute(new HttpGet(URL_LANG));

                inputStream = httpResponse.getEntity().getContent();

                if (inputStream != null) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    String result = "";
                    while ((line = bufferedReader.readLine()) != null)
                        result += line;

                    inputStream.close();
                    JSONObject jObject = new JSONObject(result);
                    JSONObject jObj = jObject.getJSONObject("langs");
                    Iterator<String> iter = (Iterator<String>) jObj.keys();
                    while (iter.hasNext()) {
                        res.add(iter.next());
                    }
                    Collections.sort(res);
                }

            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }
            return res;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            activity.makeToast(result);
        }
    }

    private class HttpTranslateTask extends AsyncTask<List<ContentValues>, Void, List<ContentValues>> {
        @Override
        protected List<ContentValues> doInBackground(List<ContentValues>... listArr) {

            List<ContentValues> list = listArr[0];
            StringBuilder UrlTranslate = new StringBuilder("https://translate.yandex.net/api/v1.5/tr.json/translate?key=" + API_KEY + "&lang=" + lang1 + "-" + lang2);
            Iterator<ContentValues> iter = list.iterator();
            while (iter.hasNext()) {
                ContentValues values = iter.next();
                UrlTranslate.append("&text=");
                if (!values.containsKey(CardsDatabase.TRANSLATION)) {
                    UrlTranslate.append(values.getAsString(CardsDatabase.WORD));
                }
            }

            InputStream inputStream;
            try {
                HttpClient httpclient = new DefaultHttpClient();

                HttpResponse httpResponse = httpclient.execute(new HttpGet(UrlTranslate.toString()));

                inputStream = httpResponse.getEntity().getContent();

                if (inputStream != null) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    String res = "";
                    while ((line = bufferedReader.readLine()) != null)
                        res += line;

                    inputStream.close();
                    Log.d("http response", res);
                    JSONObject jObject = new JSONObject(res); //todo error codes
                    JSONArray jArr = jObject.getJSONArray("text");
                    Iterator<ContentValues> cIter = list.iterator();
                    int i = 0;
                    while (cIter.hasNext()) {
                        String translation = jArr.getString(i++);
                        Log.d("translation item", translation);
                        if (!"".equals(translation)) {
                            ContentValues values = cIter.next();
                            Log.d("values before", values.toString());
                            values.put(CardsDatabase.TRANSLATION, translation);
                            Log.d("values after", values.toString());
                        } else {
                            cIter.next();
                        }
                    }
                }

            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<ContentValues> result) {
            CardsDatabase.insertCards(result, context);
        }
    }

}
