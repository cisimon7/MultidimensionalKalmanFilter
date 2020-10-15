package view

import AccelerationData
import io.data2viz.scale.Scales
import io.data2viz.viz.Margins

val margins = Margins(40.5, 30.5, 50.5, 70.5)

const val width = 1_000.0
const val height = 700.0
val chartWidth = width - margins.hMargins
val chartHeight = height - margins.vMargins

val aPoints = AccelerationData.distance

val gPoints = GPSData.distance

val xScale = Scales.Continuous.linear {
    domain = listOf((gPoints + aPoints).map { it.x }.minOrNull()!!, (gPoints + aPoints).map { it.x }.maxOrNull()!!)
    range = listOf(.0, chartWidth)
}

val yScale = Scales.Continuous.linear {
    domain = listOf((gPoints + aPoints).map { it.y }.minOrNull()!!, (gPoints + aPoints).map { it.y }.maxOrNull()!!)
    range = listOf(chartHeight, .0)
}