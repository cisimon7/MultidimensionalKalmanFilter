import io.data2viz.viz.JFxVizRenderer
import io.data2viz.viz.Viz
import javafx.scene.canvas.Canvas
import koma.extensions.map
import koma.matrix.Matrix

data class ARecord(val time: Double, val ax: Double, val ay: Double)

data class GPSRecord(val time: Double, val lat: Double, val log: Double)

data class Quint(val first: Double, val second: Double, val third: Double, val fourth: Double, val time: Double)

fun Canvas.visualize(init: Viz.() -> Unit) {
    val viz = io.data2viz.viz.viz(init)
    JFxVizRenderer(this, viz)
    viz.startAnimations()
    viz.render()
}

fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()

fun Matrix<Double>.short() = this.map { it.round() }