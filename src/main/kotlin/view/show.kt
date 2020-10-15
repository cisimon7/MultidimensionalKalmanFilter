package view

import io.data2viz.axis.Orient
import io.data2viz.axis.axis
import io.data2viz.color.Colors
import io.data2viz.color.col
import io.data2viz.geom.size
import javafx.scene.Parent
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import round
import tornadofx.*
import visualize

fun main(args: Array<String>) = launch<KalmanApp>(args)

class KalmanApp : App(MyView::class)

class MyView : View() {
    override val root: Parent = group {
        canvas(width, height) {
            visualize {
                size = size(chartWidth, chartHeight)
                group {
                    transform { translate(x= margins.left, y= margins.top) }

                    group {
                        transform { translate(x= -10.0) }
                        axis(Orient.LEFT, yScale) { tickFormat={it.round().toString()} }
                    }

                    group {
                        transform { translate(y= chartHeight+10.0) }
                        axis(Orient.BOTTOM, xScale) { tickFormat={it.round().toString()} }
                    }

                    group {
                        path {
                            fill=null
                            strokeWidth=1.0

                            stroke=Colors.Web.blueviolet
                            val pt1 = gPoints.toList()
                            moveTo(xScale(pt1[0].x), yScale(pt1[0].y))
                            pt1.drop(0).forEach { (x,y) ->
                                lineTo(xScale(x), yScale(y))
                            }
                        }

                        gPoints.forEach { (x_, y_) ->
                            circle {
                                x = xScale(x_)
                                y = yScale(y_)
                                radius = 3.0
                                fill = Colors.Web.darkorange
                            }
                        }

                        path {
                            fill=null
                            stroke=Colors.Web.darkred
                            strokeWidth=1.0

                            val pt = aPoints.toList()
                            moveTo(xScale(pt[0].x), yScale(pt[0].y))
                            pt.drop(0).forEach { (y, x) ->
                                lineTo(xScale(x), yScale(y))
                            }
                        }

                        /*aPoints.forEach { (y_, x_) ->
                            circle {
                                x = xScale(x_)
                                y = yScale(y_)
                                radius = 3.0
                                fill = Colors.Web.green
                            }
                        }*/
                    }
                }
            }
        }
    }

    init { title="MultiDimensionalKalmanFilter" }
}
