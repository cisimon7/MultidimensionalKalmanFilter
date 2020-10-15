import koma.end
import koma.mat

object KalmanFilter {

    // States initialized
    val KStates = mutableListOf(Pair(.0, .0))
    val XStates = mutableListOf(Pair(.0, .0))

    val PState = mat[.0, .0 end .0, .0]

    val F = mat[.0, .0]
    val G = mat[.0, .0]
    val H = mat[.0, .0]

    val Q = mat[.0, .0 end .0, .0]
    val R = mat[.0, .0 end .0, .0]

    val sensor1 = AccelerationData.distance
    // val sensor2 = GPSData.distance

    fun run() {
        // Time Update

        // Measurement Update
    }
}