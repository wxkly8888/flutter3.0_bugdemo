import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.example.test_native_view.MainActivity
import com.example.test_native_view.R
import com.leica.video.mjpeg.MjpegSurfaceView
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.platform.PlatformView

class MyView(
    private var context: Context?,
    messenger: BinaryMessenger?,
    activity: MainActivity,
    id: Int,
) :
    PlatformView, MethodCallHandler{
    private val mjpegView: MjpegSurfaceView
    init {
        mjpegView = MjpegSurfaceView(this.context)
//        mjpegView.surfaceView.setOnTouchListener { v: View?, event: MotionEvent? ->
//
//            mScaleDetector.onTouchEvent(event)
//            false
//        }
    }
    override fun getView(): View {

        return mjpegView
    }

    override fun dispose() {}
    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
    }

}