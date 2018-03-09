package ui.anwesome.com.circleedgeview

/**
 * Created by anweshmishra on 09/03/18.
 */
import android.graphics.*
import android.content.*
import android.view.*
class CircleEdgeView(ctx : Context) : View(ctx) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    override fun onDraw(canvas : Canvas) {

    }
    override fun onTouchEvent(event : MotionEvent):Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {
        fun update(stopcb : (Float) -> Unit) {
            scale += dir * 0.1f
            if(Math.abs(scale - prevScale) > 0) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                stopcb(scale)
            }
        }
        fun startUpdating(startcb : () -> Unit) {
            if(dir == 0f) {
                dir = 1 - 2 * scale
                startcb()
            }
        }
    }
}