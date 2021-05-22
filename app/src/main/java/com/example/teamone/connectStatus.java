package com.example.teamone;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class connectStatus {
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_NOT_CONNECTED = 3;

    /*
    인터넷 상태를 파악하는 함수입니다.
    static으로 지정해서 다른 클래스에서도 꺼내서 사용할 수 있습니다.
    간단하게는 인터넷 상황을 봐서 와이파이는 1을 리턴, LTE는 2를 리턴, 인터넷 연결안됨은 3을 리턴합니다.
    사용은 데이터를 읽어오는 상황을 if(getConnectivityStatus(~~)!=3)으로 지정하고
    해당 if문 초기에 database.goOnline();을 합니다
    그리고 else문에는 database.goOffline();을 하는데,
    왜냐하면 인터넷이 연결이 안된 상태에서 Online일 경우 다시 데이터를 켜도 Online 상태로 복구하는데 오래걸립니다.
    따라서 인터넷이 연결 안되면 offline을 만들어놓고
    연결되면 online을 만듭니다.
    */
    public static int getConnectivityStatus(Context context){ //해당 context의 서비스를 사용하기위해서 context객체를 받는다.
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo != null){
            int type = networkInfo.getType();
            if(type == ConnectivityManager.TYPE_MOBILE){//쓰리지나 LTE로 연결된것(모바일을 뜻한다.)
                return TYPE_MOBILE;
            }else if(type == ConnectivityManager.TYPE_WIFI){//와이파이 연결된것
                return TYPE_WIFI;
            }
        }
        return TYPE_NOT_CONNECTED;  //연결이 되지않은 상태
    }


}