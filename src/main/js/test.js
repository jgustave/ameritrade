//Example JavaScript.

var Request = require("request");
var WebSocket = require("ws");

function jsonToQueryString(json) {
    return Object.keys(json).map(function(key) {
            return encodeURIComponent(key) + '=' +
                encodeURIComponent(json[key]);
        }).join('&');
}


const options = {
  url: 'https://api.tdameritrade.com/v1/userprincipals',
  headers: {
    'Authorization': 'Bearer PUT_AUTH_TOKEN_HERE'
  },
  qs:{fields:'streamerSubscriptionKeys,streamerConnectionInfo'}
};

function callback(error, response, body) {
    if(error) {
        console.dir(error);
        return console.dir(error);
    }

    console.dir(JSON.parse(body));

    var userPrincipalsResponse = JSON.parse(body);
    var tokenTimeStampAsDateObj = new Date(userPrincipalsResponse.streamerInfo.tokenTimestamp);
    var tokenTimeStampAsMs = tokenTimeStampAsDateObj.getTime();

    var credentials = {
        "userid": userPrincipalsResponse.accounts[0].accountId,
        "token": userPrincipalsResponse.streamerInfo.token,
        "company": userPrincipalsResponse.accounts[0].company,
        "segment": userPrincipalsResponse.accounts[0].segment,
        "cddomain": userPrincipalsResponse.accounts[0].accountCdDomainId,
        "usergroup": userPrincipalsResponse.streamerInfo.userGroup,
        "accesslevel": userPrincipalsResponse.streamerInfo.accessLevel,
        "authorized": "Y",
        "timestamp": tokenTimeStampAsMs,
        "appid": userPrincipalsResponse.streamerInfo.appId,
        "acl": userPrincipalsResponse.streamerInfo.acl
    };

    var request = {
        "requests": [
                {
                    "service": "ADMIN",
                    "command": "LOGIN",
                    "requestid": 0,
                    "account": userPrincipalsResponse.accounts[0].accountId,
                    "source": userPrincipalsResponse.streamerInfo.appId,
                    "parameters": {
                        "credential": jsonToQueryString(credentials),
                        "token": userPrincipalsResponse.streamerInfo.token,
                        "version": "1.0"
                    }
                }
        ]
    };

    var mySock = new WebSocket("wss://" + userPrincipalsResponse.streamerInfo.streamerSocketUrl + "/ws");

    mySock.onmessage = function(evt) { console.log(evt.data); }; mySock.onclose = function() { console.log("CLOSED"); };

    setTimeout(function(){
        console.dir(JSON.stringify(request));
        mySock.send(JSON.stringify(request));
        setTimeout(function(){
            var request = {
                "requests": [
                        {
                            "service": "CHART_EQUITY",
                            "requestid": "2",
                            "command": "SUBS",
                            "account": userPrincipalsResponse.accounts[0].accountId,
                            "source": userPrincipalsResponse.streamerInfo.appId,
                            "parameters": {
                                "keys": "AAPL",
                                   "fields": "0,1,2,3,4,5,6,7,8"
                            }
                        }
                ]
            };
            console.dir(JSON.stringify(request));
            mySock.send(JSON.stringify(request));
        },1000)
    },1000);
}

Request( options, callback);

