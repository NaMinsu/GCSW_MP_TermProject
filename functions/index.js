const functions = require('firebase-functions');
const  admin = require('firebase-admin');
admin.initializeApp({
  credential: admin.credential.applicationDefault(),
});

exports.sendFCM = functions.https.onCall((data, context) => {
  const token = data.token;
  const title = data.title;
  const body = data.text;
  const subtext = data.subtext;
  const android_channel_id = data.android_channel_id;
    const options = {

     headers: {
          'Method': 'POST',
         'Content-Type': 'application/json; charset=utf-8',
        'Authorization': 'Bearer ' + "AAAAsUUuAyE:APA91bGrRVJMJ9Mph2e10KVHk17hW4aym3xAtHHaPLIJ0Lr3F9sboMXAHWjPcSH16EvgyWTK5_FBKHv2j4E1JPkVHKIWGqkDRlOS_W0LNC5YJOnX4uc8Flam6Gmuknfd60lzBdCiPkgA"
      }
      // [END use_access_token]
    };

  var payload = {
    notification: {
      title: title,
      body: body,
      tag:"notification-1",
      "android_channel_id":android_channel_id
    },
    data :{
      "Subtext":subtext
    }

  }

  var result = admin.messaging().sendToDevice(token, payload);
  return result;
})