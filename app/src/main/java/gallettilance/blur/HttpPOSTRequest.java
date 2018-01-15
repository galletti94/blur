package gallettilance.blur;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;

public class HttpPOSTRequest extends AsyncTask<String, Void, String> {

    private static final String REQUEST_METHOD = "POST";
    private static final int READ_TIMEOUT = 1000000;
    private static final int CONNECTION_TIMEOUT = 1000000;

    @Override
    protected String doInBackground(String... params){

        String stringUrl = params[0];
        String img = params[1];
        String img_label = params[2];
        String img_type = params[3];

        JSONObject myjson = new JSONObject();

        try {
            myjson.put("img_label", img_label);
            myjson.put("img_type", img_type);
            myjson.put("img", img);
            Log.e("MY PARAMETERS", myjson.toString());

            URL myUrl = new URL(stringUrl+getPostDataString(myjson));
            HttpsURLConnection connection =(HttpsURLConnection)
                    myUrl.openConnection();

            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.connect();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Log.d("ResponseCode", Integer.toString(connection.getResponseCode()));
            Log.d("ResponseMessage", connection.getResponseMessage());
            Log.d("Response", response.toString());

            connection.disconnect();
            return response.toString();
        }

        catch(Exception e) {
            return "Exception: " + e.getMessage();
        }
    }

    private String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = params.keys();

        while(itr.hasNext()){
            String key= itr.next();
            Object value = params.get(key);

            if (first) {
                first = false;
                result.append("?");
            } else {
                result.append("&");
            }

            result.append(key);
            result.append("=");
            result.append(value.toString());
        }

        Log.d("result.toString()", result.toString());
        return result.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        Log.e("Result", result);
    }
}