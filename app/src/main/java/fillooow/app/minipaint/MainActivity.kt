package fillooow.app.minipaint

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val customCanvasView = CustomCanvasView(this)
        customCanvasView.systemUiVisibility = SYSTEM_UI_FLAG_FULLSCREEN
        customCanvasView.contentDescription = getString(R.string.canvasContentDescription)

        setContentView(customCanvasView)
    }
}