package ui.anwesome.com.circleedgeview

/**
 * Created by anweshmishra on 09/03/18.
 */
import android.app.Activity
import android.graphics.*
import android.content.*
import android.view.*
class CircleEdgeView(ctx : Context) : View(ctx) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val renderer : Renderer = Renderer(this)
    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }
    override fun onTouchEvent(event : MotionEvent):Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
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
    data class Animator(var view : View, var animated : Boolean = false) {
        fun animate(updatecb : () -> Unit) {
            if(animated) {
                updatecb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                }
                catch(ex : Exception) {

                }
            }
        }
        fun start() {
            if(!animated) {
                animated = true
                view.postInvalidate()
            }
        }
        fun stop() {
            if(animated) {
                animated = false
            }
        }
    }
    data class CircleEdge(var i : Int) {
        val state = State()
        fun draw(canvas : Canvas, paint : Paint) {
            val k = 6
            val deg = 360f/k
            val w = canvas.width.toFloat()
            val h = canvas.height.toFloat()
            val size = Math.min(w,h)/3
            canvas.save()
            canvas.translate(w/2, h/2)
            for(i in 0..k-1) {
                canvas.save()
                canvas.rotate(i * deg)
                canvas.drawEdgePath(size, deg, state.scale, paint)
                canvas.restore()
            }
            canvas.restore()
        }
        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }
        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }
    }
    data class Renderer(var view : CircleEdgeView) {
        val circleEdge : CircleEdge = CircleEdge(0)
        val animator : Animator = Animator(view)
        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            circleEdge.draw(canvas, paint)
            animator.animate {
                circleEdge.update {
                    animator.stop()
                }
            }
        }
        fun handleTap() {
            circleEdge.startUpdating {
                animator.start()
            }
        }
    }
    companion object {
        fun create(activity : Activity, w : Int, h : Int):CircleEdgeView {
            val view = CircleEdgeView(activity)
            activity.addContentView(view, ViewGroup.LayoutParams(w, h))
            return view
        }
    }
}
fun Canvas.drawEdgePath(r : Float, deg : Float, scale : Float,paint : Paint)  {
    val path = Path()
    for(i in 0..deg.toInt()) {
        val sf = Math.floor((i - 0.5*deg)/(0.5*deg))
        val deg_factor = (deg * sf - ((2 * sf - 1) * (i))) / (0.5 * deg)
        val updated_r = (r/2 + r/2 * deg_factor)
        val x = (updated_r * Math.cos(i * Math.PI/180)).toFloat()
        val y = r * Math.sin(i * Math.PI/180).toFloat()
        if (i == 0) {
            path.moveTo(x, y)
        }
        else {
            path.lineTo(x, y)
        }
    }
    drawPath(path, paint)
}