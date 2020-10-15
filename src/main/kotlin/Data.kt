import io.data2viz.geom.Point
import koma.abs
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.nio.file.Files
import java.nio.file.Path

object Data {
    private val records get() = Files.newBufferedReader(Path.of(Data::class.java.getResource("main.csv").toURI()))
            .lineSequence()
            .filterIndexed { id, _ -> !listOf(0, 1, 2, 3, 4, 5).contains(id) }
            .map { it.split(",") }

    private val mutex0 = Mutex()
    private val mutex1 = Mutex()

    // Total points
    private val count = records.count()

    private val sample = 2_000

    // Acceleration data
    private val aData by lazy {
        this.records.map { ARecord(it[0].toDouble(), abs(it[1].toDouble()), abs(it[2].toDouble())) }
    }

    // GPS data
    private val gData by lazy {
        this.records.map { GPSRecord(it[0].toDouble(), abs(it[4].toDouble()), abs(it[5].toDouble())) }
    }

    // Distance data
    @OptIn(ObsoleteCoroutinesApi::class)
    val distance by lazy {

        var gpsSeed = Triple(.0, .0, .0)
        var accSeed = Quint(.0, .0, .0, .0, .0)
        var gpsPoints = sequenceOf(Point(.0, .0))
        var accPoints = sequenceOf(Point(.0, .0))

        runBlocking {
            launch {
                aData.take(sample).forEachIndexed { id, (time, ax, ay) ->
                    launch {
                        val (prevX, prevY, prevVx, prevVy, prevT) = accSeed
                        val dt = time - prevT
                        mutex0.withLock {
                            accSeed = Quint(
                                    first = prevX + prevVx*dt + (0.5*dt*dt*ax),
                                    second = prevY + prevVy*dt + (0.5*dt*dt*ay),
                                    third = dt*ax,
                                    fourth = dt*ay,
                                    time = time
                            )
                            accPoints += Point(accSeed.first, accSeed.second)
                        }
                        println(id)
                    }
                }
            }
            launch {
                gData.take(sample).forEachIndexed { id, (_, lat, log) ->
                    launch {
                        val (prevLat, prevLog, time) = gpsSeed

                        mutex1.withLock {
                            gpsSeed = Triple(
                                    first = prevLat + (lat-prevLat)*0.111321,
                                    second = prevLog + (log-prevLog)*0.111,
                                    third = time
                            )
                            gpsPoints += Point(gpsSeed.first, gpsSeed.second)
                        }
                        println(id)
                    }
                }
            }
        }

        Pair(gpsPoints, accPoints)
    }
}