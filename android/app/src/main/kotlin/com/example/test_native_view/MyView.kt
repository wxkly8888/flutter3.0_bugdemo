import android.content.Context
import android.view.View
import com.example.test_native_view.MainActivity
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

    override fun getView(): View {
//       val surfaceView=SurfaceView(context)
//        surfaceView.setBackgroundColor(Color.BLACK)
//        return surfaceView
        return MysurfaceView(context)
    }

    override fun dispose() {}
    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
    }

}