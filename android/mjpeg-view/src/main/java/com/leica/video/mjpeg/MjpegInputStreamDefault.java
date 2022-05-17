package com.leica.video.mjpeg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.collection.LruCache;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Queue;
import java.util.Stack;

/*
 * I don't really understand and want to know what the hell it does!
 * Maybe one day I will refactor it ;-)
 * <p/>
 * https://code.google.com/archive/p/android-camera-axis
 */
public class MjpegInputStreamDefault extends MjpegInputStream {
    private static final String TAG = MjpegInputStream.class.getSimpleName();
    private final static int HEADER_MAX_LENGTH = 100;
    private final static int FRAME_MAX_LENGTH = 1000000 + HEADER_MAX_LENGTH;
    private final byte[] SOI_MARKER = {(byte) 0xFF, (byte) 0xD8};
    private final byte[] EOF_MARKER = {(byte) 0xFF, (byte) 0xD9};
    private final String CONTENT_LENGTH = "Content-Length";
    private int mContentLength = -1;
    Stack<byte[]> stack=new Stack<>();
    Bitmap inBitmap;
    // no more accessible
    MjpegInputStreamDefault(InputStream in) {
        super(new BufferedInputStream(in, FRAME_MAX_LENGTH));
    }

    private int getEndOfSeqeunce(DataInputStream in, byte[] sequence) throws IOException {
        int seqIndex = 0;
        byte c;
        for (int i = 0; i < FRAME_MAX_LENGTH; i++) {
            c = (byte) in.readUnsignedByte();
            if (c == sequence[seqIndex]) {
                seqIndex++;
                if (seqIndex == sequence.length) {
                    return i + 1;
                }
            } else {
                seqIndex = 0;
            }
        }
        return -1;
    }

    private int getStartOfSequence(DataInputStream in, byte[] sequence) throws IOException {
        int end = getEndOfSeqeunce(in, sequence);
        return (end < 0) ? (-1) : (end - sequence.length);
    }
    Properties props = new Properties();
    private int parseContentLength(byte[] headerBytes) throws IOException, IllegalArgumentException {
        ByteArrayInputStream headerIn = new ByteArrayInputStream(headerBytes);
        props.load(headerIn);
        return Integer.parseInt(props.getProperty(CONTENT_LENGTH));
    }
    BitmapFactory.Options opts = new BitmapFactory.Options();

    Bitmap readMjpegFrame() throws IOException {
        long startReadTime=System.currentTimeMillis();
        mark(FRAME_MAX_LENGTH);
        int headerLen = getStartOfSequence(this, SOI_MARKER);
        long readAfterLenTime=System.currentTimeMillis();
//        Log.i("time","decode headerLen time="+(readAfterLenTime-startReadTime));
        reset();
        byte[] header = new byte[headerLen];
        readFully(header);
        int currentLength=0;
        try {
            currentLength = parseContentLength(header);
        } catch (IllegalArgumentException iae) {
            currentLength = getEndOfSeqeunce(this, EOF_MARKER);
        }
        long currentLengthTime=System.currentTimeMillis();
//        Log.i("time","decode currentLength time="+(currentLengthTime-readAfterLenTime));
        Bitmap bitmap=null;
        byte[] frameData = new byte[currentLength];
//        skipBytes(headerLen);
        readFully(frameData);
        long startDecodeTime=System.currentTimeMillis();
//        Log.i("time","readFully frameData time="+(startDecodeTime-currentLengthTime)+" length="+currentLength);
        opts.inPreferredConfig=Bitmap.Config.RGB_565;
//        bitmap=BitmapFactory.decodeByteArray(frameData,0,currentLength,opts);
        if(mContentLength<currentLength){
            mContentLength=currentLength;
      //    Bitmap bitmap= BitmapFactory.decodeStream(new ByteArrayInputStream(frameData));
             bitmap=BitmapFactory.decodeByteArray(frameData,0,mContentLength,opts);
            inBitmap = bitmap.copy(Bitmap.Config.RGB_565,false);
        }else {
            opts.inBitmap=inBitmap;
//             bitmap= BitmapFactory.decodeStream(new ByteArrayInputStream(frameData),null,opts);
            bitmap=BitmapFactory.decodeByteArray(frameData,0,currentLength,opts);
        }

//        Log.i("time","decode bitmap time="+(System.currentTimeMillis()-startDecodeTime));
        return bitmap;
//        return null;
    }
}
