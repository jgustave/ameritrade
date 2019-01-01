package jd.amer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jd.amer.model.CandleList;
import jd.amer.model.StrmCredentials;
import okhttp3.*;
import okio.ByteString;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Properties;

/**
 * Janky Hello World to use the AMeritrade API.
 */
public class AmerTest {

    public static void main(String[] args) {

        //Get Refresh token (expires after 90 days)
        Properties props = getProps();
        String accountId = props.getProperty("account_id");

        //Get an Auth Token (Expires after 30 minutes)
        JSONObject authResponse = getRefreshToken(props.getProperty("refresh_token"),props.getProperty("client_id"));

        String authToken = authResponse.getString("access_token");
        String refreshToken = authResponse.getString("refresh_token");


        //Save the updated refresh token (it seems to expire before 90 days)
        props.setProperty("refresh_token",refreshToken);
        setProps(props);

//        JSONArray accounts = getAccounts(authToken);
//        JSONObject account = getAccount(authToken,accountId);
//
//
//        System.out.println("isDayTrader:" + account.getJSONObject("securitiesAccount").getBoolean("isDayTrader"));
//        System.out.println("Cash:" + account.getJSONObject("securitiesAccount").getJSONObject("currentBalances").getBigDecimal("cashAvailableForTrading"));


        // Get 1 minute bars
        //CandleList candleList = getPriceHistory(authToken, "AAPL");

        // Get 1 minute bars
//        CandleList cl1 = getPriceHistoryByTime(authToken, "AAPL");
//        System.out.println(new Date(cl1.getCandles().get(0).getDatetime()) + " to " + new Date(cl1.getCandles().get(cl1.getCandles().size() - 1).getDatetime()));
//
//
//        //Get current quote and various info
//        JSONObject qobj = getQuote(authToken, "AAPL");
//
        //Get Ameritrade user config.
        JSONObject jobj = getUserPrincipals(authToken);

        //start websocket data try to get real time chart info.
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
    public static void setProps(Properties props) {
        try {
            props.store( new FileOutputStream(".props/props.txt"),"");
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
            if( response.code() != 200 ) {
                throw new RuntimeException("Failed:" + response);
            }

            String     jsonData = response.body().string();
            JSONObject jobj  = new JSONObject(jsonData);
            return( jobj );
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static JSONArray getAccounts (String authToken) {
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.tdameritrade.com/v1/accounts").newBuilder();
        urlBuilder.addQueryParameter("fields", "positions,orders");
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                            .header("Authorization", "Bearer "+authToken)
                             .url(url)
                             .build();

        try {
            Response   response = client.newCall(request).execute();
            if( response.code() != 200 ) {
                throw new RuntimeException("Failed:" + response);
            }

            String     jsonData = response.body().string();
            JSONArray jobj  = new JSONArray(jsonData);
            return( jobj );
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static JSONObject getAccount (String authToken, String account) {
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.tdameritrade.com/v1/accounts/"+account).newBuilder();
        urlBuilder.addQueryParameter("fields", "positions,orders");
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                            .header("Authorization", "Bearer "+authToken)
                             .url(url)
                             .build();

        try {
            Response   response = client.newCall(request).execute();
            if( response.code() != 200 ) {
                throw new RuntimeException("Failed:" + response);
            }

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
     *
     * @param authToken
     * @return
     */
    public static CandleList getPriceHistory (String authToken, String symbol) {
        OkHttpClient client = new OkHttpClient();

        //https://api.tdameritrade.com/v1/marketdata/AAPL/pricehistory
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.tdameritrade.com/v1/marketdata/"+symbol+"/pricehistory").newBuilder();
        urlBuilder.addQueryParameter("periodType", "day");
        urlBuilder.addQueryParameter("period", "2");
        urlBuilder.addQueryParameter("frequencyType", "minute");
        urlBuilder.addQueryParameter("frequency", "1");
        urlBuilder.addQueryParameter("needExtendedHoursData", "false");
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                            .header("Authorization", "Bearer "+authToken)
                             .url(url)
                             .build();

        try {
            Response   response = client.newCall(request).execute();
            if( response.code() != 200 ) {
                throw new RuntimeException("Failed:" + response);
            }
            Gson       gson     = new GsonBuilder().create();
            CandleList list = gson.fromJson(response.body().charStream(),CandleList.class);
            return( list );
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param authToken
     * @return
     */
    public static JSONObject getQuote (String authToken, String symbol) {
        OkHttpClient client = new OkHttpClient();

        //https://api.tdameritrade.com/v1/marketdata/AAPL/pricehistory
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.tdameritrade.com/v1/marketdata/"+symbol+"/quotes").newBuilder();
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                            .header("Authorization", "Bearer "+authToken)
                             .url(url)
                             .build();

        try {
            Response   response = client.newCall(request).execute();
            if( response.code() != 200 ) {
                throw new RuntimeException("Failed:" + response);
            }

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
    public static JSONObject getRefreshToken(String refreshToken, String clientId) {
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
            return( jobj );
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

        JSONObject jo1 = new JSONObject();
        jo1.put("keys","AAPL");
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


        JSONObject job1 = new JSONObject();
        job1.put("keys","/ES");
        job1.put("fields","0,1,2,3,4,5,6,7");
        JSONObject job = new JSONObject();
        job.put("service","CHART_FUTURES");
        job.put("requestid","3");
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


        JSONObject params1 = new JSONObject();
        params1.put("symbol","/ES");
        params1.put("frequency","m1");
        params1.put("period","d1");
        JSONObject foo1 = new JSONObject();
        foo1.put("service","CHART_HISTORY_FUTURES");
        foo1.put("requestid","3");
        foo1.put("command","GET");
        foo1.put("account",userPrincipals.getJSONArray("accounts").getJSONObject(0).getString("accountId"));
        foo1.put("source",userPrincipals.getJSONObject("streamerInfo").getString("appId"));
        foo1.put("parmeters",params1);

        try
        {
           Thread.sleep(3000);
        }catch( Exception ignored )
        {}
        System.out.println(wrap(foo1).toString());
        webSocket.send(wrap(foo1).toString());


        JSONObject params2 = new JSONObject();
        params2.put("keys","/ES");
        params2.put("fields","0,1,2,3,4");
        JSONObject foo2 = new JSONObject();
        foo2.put("service","LEVELONE_FUTURES");
        foo2.put("requestid","6");
        foo2.put("command","SUBS");
        foo2.put("account",userPrincipals.getJSONArray("accounts").getJSONObject(0).getString("accountId"));
        foo2.put("source",userPrincipals.getJSONObject("streamerInfo").getString("appId"));
        foo2.put("parmeters",params2);

        try
        {
           Thread.sleep(3000);
        }catch( Exception ignored )
        {}
        System.out.println(wrap(foo2).toString());
        webSocket.send(wrap(foo2).toString());


        JSONObject params3 = new JSONObject();
        params3.put("keys","AAPL");
        params3.put("fields","0,1,2,3,4");
        JSONObject foo3 = new JSONObject();
        foo3.put("service","TIMESALE_EQUITY");
        foo3.put("requestid","7");
        foo3.put("command","SUBS");
        foo3.put("account",userPrincipals.getJSONArray("accounts").getJSONObject(0).getString("accountId"));
        foo3.put("source",userPrincipals.getJSONObject("streamerInfo").getString("appId"));
        foo3.put("parmeters",params3);

        try
        {
           Thread.sleep(3000);
        }catch( Exception ignored )
        {}
        System.out.println(wrap(foo3).toString());
        webSocket.send(wrap(foo3).toString());


    }
    /**
     * Seems to somewhat ignore the startDate. StartDate within the same day
     * seems to snap to the beginning of the day.
     * End date it mostly uses as specified.
     *
     *
     *
     * @param authToken
     * @return
     */
    public static CandleList getPriceHistoryByTime (String authToken, String symbol) {

        //ZonedDateTime end = ZonedDateTime.of(2018, 12, 20, 12, 10, 0, 0, ZoneId.of("America/New_York"));
        //ZonedDateTime end    = ZonedDateTime.of(2018, 12, 20, 14, 5, 0, 0, ZoneId.of("America/New_York"));
        ZonedDateTime end    = ZonedDateTime.of(2019, 1, 1, 14, 5, 0, 0, ZoneId.of("America/New_York"));
        long          ee     = end.toInstant().getEpochSecond()* 1000;
        OkHttpClient  client = new OkHttpClient();

        //https://api.tdameritrade.com/v1/marketdata/AAPL/pricehistory
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.tdameritrade.com/v1/marketdata/"+symbol+"/pricehistory").newBuilder();
//        urlBuilder.addQueryParameter("periodType", "day");
//        urlBuilder.addQueryParameter("period", "2");
        urlBuilder.addQueryParameter("frequencyType", "minute");
        urlBuilder.addQueryParameter("frequency", "1");
        urlBuilder.addQueryParameter("needExtendedHoursData", "false");
        urlBuilder.addQueryParameter("startDate", String.valueOf(ee-(10*24*60*60*1000)));
        //urlBuilder.addQueryParameter("startDate", String.valueOf(ee-(2*60*60*1000)));
        urlBuilder.addQueryParameter("endDate", String.valueOf(ee));
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                            .header("Authorization", "Bearer "+authToken)
                             .url(url)
                             .build();

        try {
            Response   response = client.newCall(request).execute();
            if( response.code() != 200 ) {
                throw new RuntimeException("Failed:" + response);
            }
            Gson       gson     = new GsonBuilder().create();
            CandleList list = gson.fromJson(response.body().charStream(),CandleList.class);
            return( list );
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
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
