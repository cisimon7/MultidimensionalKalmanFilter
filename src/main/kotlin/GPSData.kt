import io.data2viz.geom.Point
import io.data2viz.shape.pi
import koma.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.nio.file.Files
import java.nio.file.Path

object GPSData {
    private val records get() = Files.newBufferedReader(Path.of(AccelerationData::class.java.getResource("Location2.csv").toURI()))
        .lineSequence()
        .filterIndexed { id, _ -> !listOf(0).contains(id) }
        .map { it.split(",") }

    private val mutex = Mutex()

    // Total points
    private val count = records.count()

    private val sample = count

    // GPS data
    private val gData by lazy {
        this.records.map { GPSRecord(it[0].toDouble(), abs(it[1].toDouble())/** pi /180*/, abs(it[2].toDouble()/** pi/180*/)) }
    }

    // Distance data
    @OptIn(ObsoleteCoroutinesApi::class)
    val distance by lazy {

        var seed = Quint(.0, .0, 9.891112181, 8.891036238, .0)
        var points = sequenceOf(Point(.0, .0))
        var total = 0

        runBlocking {
            gData.take(sample).forEach { (_, lat, log) ->
                launch {
                    val (prevX, prevY, prevLat, prevLog, time) = seed

                    mutex.withLock {
                        seed = Quint(
                                first = prevX + abs(lat-prevLat)*111_000,
                                second = prevY + abs(log-prevLog)*113_000,
                                third = lat,
                                fourth = log,
                                time = time
                        )
                        points += Point(seed.first, seed.second)
                    }
                }
            }
        }
        points
    }
}