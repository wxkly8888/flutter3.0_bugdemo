package com.leica.video.mjpeg;

import android.graphics.Bitmap;

public interface OnFrameCapturedListener {
    void onFrameCaptured(Bitmap bitmap);
}