package com.abc.sharefilesz.base;

import android.content.Context;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class AppConfig
{
    public final static int
            SERVER_PORT_COMMUNICATION = 1128,
            SERVER_PORT_SEAMLESS = 58762,
            SERVER_PORT_WEBSHARE = 58732,
            SERVER_PORT_UPDATE_CHANNEL = 58765,
            DEFAULT_SOCKET_TIMEOUT = 5000,
            DEFAULT_SOCKET_TIMEOUT_LARGE = 40000,
            DEFAULT_NOTIFICATION_DELAY = 2000,
            SUPPORTED_MIN_VERSION = 1,
            NICKNAME_LENGTH_MAX = 32,
            BUFFER_LENGTH_DEFAULT = 8096,
            PHOTO_SCALE_FACTOR = 100,
            WEB_SHARE_CONNECTION_MAX = 20;

    public final static String

            PREFIX_ACCESS_POINT = "TS_",
            EXT_FILE_PART = "tshare",
            NDS_COMM_SERVICE_NAME = "TSComm",
            NDS_COMM_SERVICE_TYPE = "_tscomm._tcp.";

    public final static String[] DEFAULT_DISABLED_INTERFACES = new String[]{"rmnet"};


    public static void sendUpdate(Context context, String toIp) throws IOException
    {
        Socket socket = new Socket();

        socket.bind(null);
        socket.connect(new InetSocketAddress(toIp, AppConfig.SERVER_PORT_UPDATE_CHANNEL));

        FileInputStream fileInputStream = new FileInputStream(context.getApplicationInfo().sourceDir);
        OutputStream outputStream = socket.getOutputStream();

        byte[] buffer = new byte[AppConfig.BUFFER_LENGTH_DEFAULT];
        int len;
        long lastRead = System.currentTimeMillis();

        while ((len = fileInputStream.read(buffer)) != -1) {
            if (len > 0) {
                outputStream.write(buffer, 0, len);
                outputStream.flush();

                lastRead = System.currentTimeMillis();
            }

            if ((System.currentTimeMillis() - lastRead) > AppConfig.DEFAULT_SOCKET_TIMEOUT) {
                System.out.println("CoolTransfer: Timed out... Exiting.");
                break;
            }
        }

        outputStream.close();
        fileInputStream.close();

        socket.close();
    }

}
