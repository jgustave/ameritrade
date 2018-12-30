package jd.amer.model;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.time.Instant;

/**
 *
 */
public class StrmCredentials {

    public String userid = "";
    public String token = "";
    public String company = "";
    public String segment = "";
    public String cddomain = "";
    public String usergroup = "";
    public String accesslevel = "";
    public String authorized = "";
    public String timestamp = "";
    public String appid = "";
    public String acl = "";

    public StrmCredentials () {
    }

    public StrmCredentials (JSONObject up) {
        userid = up.getJSONArray("accounts").getJSONObject(0).getString("accountId");
        token = up.getJSONObject("streamerInfo").getString("token");
        company =up.getJSONArray("accounts").getJSONObject(0).getString("company");
        segment = up.getJSONArray("accounts").getJSONObject(0).getString("segment");
        cddomain = up.getJSONArray("accounts").getJSONObject(0).getString("accountCdDomainId");
        usergroup = up.getJSONObject("streamerInfo").getString("userGroup");
        accesslevel = up.getJSONObject("streamerInfo").getString("accessLevel");
        authorized = "Y";

        //Hack to change to ISO 8601
        String tsStr = up.getJSONObject("streamerInfo").getString("tokenTimestamp");
        tsStr=tsStr.replace("+0000","Z");

        timestamp = String.valueOf(Instant.parse( tsStr ).toEpochMilli());
        appid = up.getJSONObject("streamerInfo").getString("appId");
        acl = up.getJSONObject("streamerInfo").getString("acl");

    }

//    public String toQp () {
//
//        return "userid="+URLEncoder.encode(userid)+"&token="+URLEncoder.encode(token)+"&company="+URLEncoder.encode(company)+
//               "&segment="+URLEncoder.encode(segment)+"&cddomain="+URLEncoder.encode(cddomain)+"&usergroup="+URLEncoder.encode(usergroup)+
//               "&accesslevel="+URLEncoder.encode(accesslevel)+"&authorized="+URLEncoder.encode(authorized);
//    }
    public String toQpb () {

        return "userid="+userid+"&token="+token+"&company="+company+
               "&segment="+segment+"&cddomain="+cddomain+"&usergroup="+usergroup+
               "&accesslevel="+accesslevel+"&authorized="+authorized+
               "&timestamp="+timestamp+"&appid="+appid+"&acl="+acl;
    }
}
