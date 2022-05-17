//package com.leica.video.mjpeg;
//
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//
//import okhttp3.Call;
//import okhttp3.Request;
//import okhttp3.Response;
//
//public class OkhttpUtil {
//    /**
//     * 下载文件
//     * @param fileUrl 文件url
//     * @param destFileDir 存储目标目录
//     */
//    public <T> void downLoadFile(String fileUrl, final String destFileDir, final ReqCallBack<T> callBack) {
//        final String fileName = MD5.encode(fileUrl);
//        final File file = new File(destFileDir, fileName);
//        if (file.exists()) {
//            successCallBack((T) file, callBack);
//            return;
//        }
//        final Request request = new Request.Builder().url(fileUrl).build();
//        final Call call = mOkHttpClient.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.e(TAG, e.toString());
//                failedCallBack("下载失败", callBack);
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                InputStream is = null;
//                byte[] buf = new byte[2048];
//                int len = 0;
//                FileOutputStream fos = null;
//                try {
//                    long total = response.body().contentLength();
//                    Log.e(TAG, "total------>" + total);
//                    long current = 0;
//                    is = response.body().byteStream();
//                    fos = new FileOutputStream(file);
//                    while ((len = is.read(buf)) != -1) {
//                        current += len;
//                        fos.write(buf, 0, len);
//                        Log.e(TAG, "current------>" + current);
//                    }
//                    fos.flush();
//                    successCallBack((T) file, callBack);
//                } catch (IOException e) {
//                    Log.e(TAG, e.toString());
//                    failedCallBack("下载失败", callBack);
//                } finally {
//                    try {
//                        if (is != null) {
//                            is.close();
//                        }
//                        if (fos != null) {
//                            fos.close();
//                        }
//                    } catch (IOException e) {
//                        Log.e(TAG, e.toString());
//                    }
//                }
//            }
//        });
//    }
//}
