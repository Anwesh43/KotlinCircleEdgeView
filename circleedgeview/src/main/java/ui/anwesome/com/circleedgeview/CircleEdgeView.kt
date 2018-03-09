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
            if(Math.abs(scale - prevScale) > 1) {
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
                canvas.drawEdgePath(size, deg/2, state.scale, paint)
                canvas.drawArc(RectF(-size/2, -size/2, size/2, size/2),deg/2, deg/2, false, paint)
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
    data class Renderer(var view : CircleEdgeView, var time : Int = 0) {
        val circleEdge : CircleEdge = CircleEdge(0)
        val animator : Animator = Animator(view)
        fun render(canvas : Canvas, paint : Paint) {
            if (time == 0) {
                paint.color = Color.parseColor("#F4511E")
                paint.strokeWidth = Math.min(canvas.width.toFloat(), canvas.height.toFloat()) / 60
                paint.strokeCap = Paint.Cap.ROUND
                paint.style = Paint.Style.STROKE
            }
            canvas.drawColor(Color.parseColor("#212121"))
            circleEdge.draw(canvas, paint)
            time++
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
    path.moveTo(r/2, 0f)
    for(i in 1..deg.toInt()-1) {
        val sf = Math.floor((i)/(0.5*deg))
        val deg_factor = (deg * sf - ((2 * sf - 1) * (i))) / (0.5 * deg)
        val updated_r =  r/2 + ((r * 0.5) * deg_factor * scale)
        val x = (updated_r * Math.cos(i * Math.PI/180)).toFloat()
        val y = (updated_r * Math.sin(i * Math.PI/180)).toFloat()
        path.lineTo(x, y)
    }
    drawPath(path, paint)
}