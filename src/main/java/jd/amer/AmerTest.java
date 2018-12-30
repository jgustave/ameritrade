package jd.amer;

import jd.amer.model.StrmCredentials;
import okhttp3.*;
import okio.ByteString;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Properties;

/**
 *
 */
public class AmerTest {

    public static void main(String[] args) {

        //Get Refresh token (expires after 90 days)
        Properties props = getProps();

        //Get an Auth Token (Expires after 30 minutes)
        String authToken = getRefreshToken(props.getProperty("refresh_token"),props.getProperty("client_id"));

        //Get Ameritrade user config.
        JSONObject jobj = getUserPrincipals(authToken);

        //start websocket data.
        loginStream(jobj);

    }

    public static Properties getProps() {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(".props/props.txt"));
            return( props );
        }
        catch (IOException e) {
            throw new RuntimeException("",e);
        }
    }


    public static JSONObject getUserPrincipals (String authToken) {
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.tdameritrade.com/v1/userprincipals").newBuilder();
        urlBuilder.addQueryParameter("fields", "streamerSubscriptionKeys,streamerConnectionInfo");
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                            .header("Authorization", "Bearer "+authToken)
                             .url(url)
                             .build();

        try {
            Response   response = client.newCall(request).execute();
            String     jsonData = response.body().string();
            JSONObject jobj  = new JSONObject(jsonData);
            return( jobj );
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Instructions on how to create an auth token.
     * https://www.reddit.com/r/algotrading/comments/914q22/successful_access_to_td_ameritrade_api/
     * https://developer.tdameritrade.com/content/simple-auth-local-apps
     * https://developer.tdameritrade.com/authentication/apis/post/token-0
     * @param refreshToken
     * @param clientId
     * @return
     */
    public static String getRefreshToken(String refreshToken, String clientId) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("grant_type", "refresh_token")
                .add("access_type", "offline")
                .add("client_id", clientId)
                .add("refresh_token", refreshToken)
                .build();



        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url("https://api.tdameritrade.com/v1/oauth2/token")
                .post(requestBody)
                .build();

        try {
            Response   response = client.newCall(request).execute();
            if( response.code() != 200 ) {
                throw new RuntimeException("Failed:" + response);
            }
            String     jsonData = response.body().string();
            JSONObject jobj  = new JSONObject(jsonData);
            return( jobj.getString("access_token") );
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public static void loginStream (JSONObject userPrincipals ) {
        String strmUrl = userPrincipals.getJSONObject("streamerInfo").getString("streamerSocketUrl");

        OkHttpClient    client     = new OkHttpClient();

        Request request = new Request.Builder().url("ws://" + strmUrl + "/ws").build();
        WebSocket webSocket = client.newWebSocket(request, new WebSocketListener() {

            @Override
            public void onOpen (WebSocket webSocket, Response response) {
                try {
                    String str = response.body().string();
                    System.out.println(str);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("onOpen:" + response);
            }

            @Override
            public void onMessage (WebSocket webSocket, String text) {
                System.out.println("onmsg1:"+text);
            }

            @Override
            public void onMessage (WebSocket webSocket, ByteString bytes) {
                System.out.println("onMsg2:" + bytes);
            }

            @Override
            public void onClosed (WebSocket webSocket, int code, String reason) {
                System.out.println("onClosed" + code + " " + reason);
            }

            @Override
            public void onFailure (WebSocket webSocket, Throwable t, Response response) {
                System.out.println("onFailure" + t + response);
            }
        });


        JSONObject lreq = new JSONObject();
        JSONObject creds = new JSONObject();
        lreq.put("parameters",creds);

        lreq.put("service","ADMIN");
        lreq.put("command","LOGIN");
        lreq.put("requestid",0);
        lreq.put("account",userPrincipals.getJSONArray("accounts").getJSONObject(0).getString("accountId"));
        lreq.put("source",userPrincipals.getJSONObject("streamerInfo").getString("appId"));

        StrmCredentials sc = new StrmCredentials(userPrincipals);
        creds.put("credential", URLEncoder.encode(sc.toQpb()));
        creds.put("token",userPrincipals.getJSONObject("streamerInfo").getString("token"));
        creds.put("version","1.0");

        String foo = wrap(lreq).toString();

        try
        {
           Thread.sleep(3000);
        }catch( Exception ignored )
        {}

        System.out.println(foo);
        webSocket.send(foo);



        System.out.println("AAA");

        JSONObject jo1 = new JSONObject();
        jo1.put("keys","AAPL,MSFT");
        jo1.put("fields","0,1,2,3,4,5,6,7");
        JSONObject jo = new JSONObject();
        jo.put("service","CHART_EQUITY");
        jo.put("requestid","2");
        jo.put("command","SUBS");
        jo.put("account",userPrincipals.getJSONArray("accounts").getJSONObject(0).getString("accountId"));
        jo.put("source",userPrincipals.getJSONObject("streamerInfo").getString("appId"));
        jo.put("parmeters",jo1);

        try
        {
           Thread.sleep(3000);
        }catch( Exception ignored )
        {}
        System.out.println(wrap(jo).toString());
        webSocket.send(wrap(jo).toString());
        System.out.println("BBB");


        JSONObject job1 = new JSONObject();
        job1.put("keys","/ES");
        job1.put("fields","0,1,2,3,4,5,6,7");
        JSONObject job = new JSONObject();
        job.put("service","CHART_FUTURES");
        job.put("requestid","2");
        job.put("command","SUBS");
        job.put("account",userPrincipals.getJSONArray("accounts").getJSONObject(0).getString("accountId"));
        job.put("source",userPrincipals.getJSONObject("streamerInfo").getString("appId"));
        job.put("parmeters",job1);

        try
        {
           Thread.sleep(3000);
        }catch( Exception ignored )
        {}
        System.out.println(wrap(job).toString());
        webSocket.send(wrap(job).toString());
        System.out.println("CCC");

    }

    /**
     * Small helper to wrap a request
     * @param obj
     * @return
     */
    private static JSONObject wrap(JSONObject obj) {
        JSONObject req = new JSONObject();
        JSONArray lreqArray = new JSONArray();
        req.put("requests",lreqArray);
        lreqArray.put(0,obj);
        return( req );
    }
}
