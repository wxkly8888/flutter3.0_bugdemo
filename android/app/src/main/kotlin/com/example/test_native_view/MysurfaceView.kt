import android.content.Context
import android.graphics.*
import android.view.SurfaceHolder
import android.view.SurfaceView

class MysurfaceView(context: Context?) : SurfaceView(context), SurfaceHolder.Callback {


    //线程
    internal inner class MyThread : Runnable {
        override fun run() {
            val canvas: Canvas = holder.lockCanvas(null) //获取画布
            val mPaint = Paint()
            mPaint.setColor(Color.RED)
            canvas.drawRect(Rect(100,100,100,100), mPaint)
            holder.unlockCanvasAndPost(canvas);
        }
    }

    init {
        val holder = this.getHolder() //获取holder
        holder.addCallback(this)
        setBackgroundColor(Color.BLACK)
        //setFocusable(true);
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Thread(MyThread()).start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

    }
}