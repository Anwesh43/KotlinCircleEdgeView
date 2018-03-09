package ui.anwesome.com.kotlincircleedgeview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ui.anwesome.com.circleedgeview.CircleEdgeView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CircleEdgeView.create(this, 500, 500)
    }
}
