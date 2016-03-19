package com.wireless.transfile.webserver;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wireless.transfile.R;
import com.wireless.transfile.app.AppLog;
import com.wireless.transfile.utility.Utility;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wireless.transfile.app.AppSettings.setPortNumber;
import static com.wireless.transfile.app.AppSettings.setrequest;
import static com.wireless.transfile.utility.FileWalker.getDirectoryFiles;
import static com.wireless.transfile.utility.JsonInfo.getDirectoryList;
import static com.wireless.transfile.utility.JsonInfo.getMusicList;
import static com.wireless.transfile.utility.JsonInfo.getPhotoList;
import static com.wireless.transfile.utility.JsonInfo.getSystemInfo;
import static com.wireless.transfile.utility.JsonInfo.getVideoList;
import static com.wireless.transfile.utility.MimeTypes.contentType;

public class HttpResponse extends Thread {
    Socket socket;
    String header;
    Context context;

    NotificationManager notificationManager;
    private static final int THUMBNAILSIZE = 168;
    // private static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";
    // private DateFormat mHttpDate = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);

    HttpResponse(Socket socket, Context context, NotificationManager notificationManager) {
        this.socket = socket;
        this.context = context;
        this.notificationManager = notificationManager;
        header = "";
    }

    @Override
    public synchronized void run() {
        BufferedReader bufferedReader;
        String firstLine;
        // String secondLine;
        String response;

        String method;
        String httpVersion;
        String requestUri;
        OutputStream outputStream;
        Map<String, List<String>> query = null;

        try {
            outputStream = socket.getOutputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            boolean flag = false;
            String key = "";
            String fileURL;

            firstLine = bufferedReader.readLine();
            //   secondLine = bufferedReader.readLine();

            int first = firstLine.indexOf(" ");
            int end = firstLine.lastIndexOf(" ");
            method = firstLine.substring(0, first);
            httpVersion = firstLine.substring(end + 1, firstLine.length());
            requestUri = firstLine.substring(first + 1, end);
            int questionMarkIndex;
            requestUri = URLDecoder.decode(requestUri, "UTF-8");

            if (requestUri.contains("?"))
                questionMarkIndex = requestUri.indexOf("?");
            else
                questionMarkIndex = -1;
            //  /storage/emulated/0/index.html?as

            if (questionMarkIndex != -1) {
                fileURL = requestUri.substring(0, questionMarkIndex);
                requestUri = requestUri.substring(questionMarkIndex + 1, requestUri.length());
                query = getQueryParams("?" + requestUri);
                Log.i(fileURL, requestUri);
            } else
                fileURL = requestUri;

            List<String> list = null;
            if (query != null && query.containsKey("Key")) {
                list = query.get("Key");
                flag = true;
            }
            if (list != null && !list.isEmpty()) {
                key = list.get(0);
            }

            if (flag) {
                if (method.equals("GET")) {
                    switch (key) {
                        case "PhoneSystemInfo":
                            response = getSystemInfo(context);
                            break;
                        case "PhotoList":
                            response = getPhotoList(context, 0);
                            break;
                        case "VideoList":
                            response = getVideoList(context, 1);
                            break;
                        case "MusicList":
                            response = getMusicList(context, 2);
                            break;
                        default:
                            response = "404 File Not Found" + "  OR  " + "Invalid Request";
                            break;
                    }
                } else {
                    response = "404 File Not Found2";
                }
                outputStream.write((httpVersion + " 200" + "\r\n").getBytes());
                outputStream.write(("Content type: text/html" + "\r\n").getBytes());
                outputStream.write(("Content length: " + response.length() + "\r\n").getBytes());
                outputStream.write(("\r\n").getBytes());
                outputStream.write((response + "\r\n").getBytes());

            } else {
                if (fileURL.startsWith("/web") || fileURL.equals("/") || fileURL.equals("/index.html")) {
                    String openFile;
                    if (fileURL.equals("/")) {
                        openFile = "index.html";
                    } else {
                        openFile = fileURL.substring(1, fileURL.length());
                    }

                    InputStream inputStream;
                    try {
                        inputStream = context.getAssets().open(openFile);
                        AppLog.logString("Loading File Done");
                        if (inputStream != null) {
                            try {
                                outputStream.write((httpVersion + " 200" + "\r\n").getBytes());
                                outputStream.write(("Content type: " + contentType(openFile) + "\r\n").getBytes());
                                outputStream.write(("\r\n").getBytes());
                                byte[] buffer = new byte[256];
                                int bytesRead;
                                while ((bytesRead = inputStream.read(buffer)) != -1) {
                                    outputStream.write(buffer, 0, bytesRead);
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                                AppLog.logString("Loading File Fail");
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        AppLog.logString("Loading File Fail");
                    }


                } else {
                    try {
                        File file = new File(fileURL);
                        if (file.exists()) {
                            if (!file.isDirectory()) {
                                FileInputStream fileInputStream = new FileInputStream(file);
                                int width;

                                if (query != null && query.containsKey("Thumbnail")) {
                                    if (query.containsKey("Width")) {
                                        if (query.get("Width") != null && !query.get("Width").isEmpty()) {
                                            list = query.get("Width");
                                            if (list != null && !list.isEmpty()) {
                                                key = list.get(0);
                                                Log.i("Width", key);
                                                width = Integer.parseInt(key);
                                            } else
                                                width = THUMBNAILSIZE;
                                        } else
                                            width = THUMBNAILSIZE;
                                    } else {
                                        width = THUMBNAILSIZE;
                                    }
                                    outputStream.write((httpVersion + " 200" + "\r\n").getBytes());
                                    outputStream.write(("Content type: " + contentType(file.getName()) + "\r\n").getBytes());
                                    outputStream.write(("\r\n").getBytes());
                                    thumbnail(fileInputStream, outputStream, width);
                                } else if (query != null && query.containsKey("VideoThumbnail")) {
                                    outputStream.write((httpVersion + " 200" + "\r\n").getBytes());
                                    outputStream.write(("Content type: " + contentType(file.getName()) + "\r\n").getBytes());
                                    outputStream.write(("\r\n").getBytes());
                                    Bitmap bmThumbnail;
                                    // MINI_KIND: 512 x 384 thumbnail
                                    bmThumbnail = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    bmThumbnail.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                                    byte[] imageData = byteArrayOutputStream.toByteArray();
                                    outputStream.write(imageData, 0, imageData.length);

                                } else {
                                    outputStream.write((httpVersion + " 200" + "\r\n").getBytes());
                                    outputStream.write(("Content type: " + contentType(file.getName()) + "\r\n").getBytes());
                                    outputStream.write(("Content length: " + file.length() + "\r\n").getBytes());
                                    outputStream.write(("\r\n").getBytes());
                                    sendBytes(fileInputStream, outputStream);
                                }
                            } else {
                                response = getDirectoryList(file);
                                //response = fileURL + "\nIt is a directory, You are not allowed!";
                                outputStream.write((httpVersion + " 200" + "\r\n").getBytes());
                                outputStream.write(("Content type: text/html" + "\r\n").getBytes());
                                outputStream.write(("Content length: " + response.length() + "\r\n").getBytes());
                                outputStream.write(("\r\n").getBytes());
                                outputStream.write((response + "\r\n").getBytes());
                            }
                        } else {
                            response = "404 File Not Found";
                            outputStream.write((httpVersion + " 404" + "\r\n").getBytes());
                            outputStream.write(("Content type: text/html" + "\r\n").getBytes());
                            outputStream.write(("Content length: " + response.length() + "\r\n").getBytes());
                            outputStream.write(("\r\n").getBytes());
                            outputStream.write((response + "\r\n").getBytes());
                        }
                    } catch (FileNotFoundException f) {
                        f.printStackTrace();
                    }
                }
            }
            outputStream.flush();
            outputStream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Map<String, List<String>> getQueryParams(String url) {
        try {
            Map<String, List<String>> params = new HashMap<>();
            String[] urlParts = url.split("\\?");
            if (urlParts.length > 1) {
                String query = urlParts[1];
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    String key = URLDecoder.decode(pair[0], "UTF-8");
                    String value = "";
                    if (pair.length > 1) {
                        value = URLDecoder.decode(pair[1], "UTF-8");
                    }
                    List<String> values = params.get(key);
                    if (values == null) {
                        values = new ArrayList<>();
                        params.put(key, values);
                        Log.i(key, value);
                    }
                    values.add(value);
                }
            }
            return params;
        } catch (UnsupportedEncodingException ex) {
            throw new AssertionError(ex);
        }
    }

    private void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
        byte[] buffer = new byte[1024];
        int bytes;
        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }

    private void thumbnail(FileInputStream fis, OutputStream os, int widthInt) throws IOException {
        Bitmap imageBitmap = BitmapFactory.decodeStream(fis);
        int height = imageBitmap.getHeight();
        int width = imageBitmap.getWidth();
        double ratio = height / (width * 1.0);
        imageBitmap = Bitmap.createScaledBitmap(imageBitmap, widthInt, (int) (ratio * widthInt), false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] imageData = byteArrayOutputStream.toByteArray();
        os.write(imageData, 0, imageData.length);
    }

}