import io.data2viz.geom.Point
import koma.pow
import koma.sqrt
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.nio.file.Files
import java.nio.file.Path

object AccelerationData {
    private val records get() = Files.newBufferedReader(Path.of(AccelerationData::class.java.getResource("Accelerometer2.csv").toURI()))
        .lineSequence()
            .filterIndexed { id, _ -> !listOf(0).contains(id) }
            .filterIndexed { id, _ ->  id%100 == 0 }
            .map { it.split(",") }

    // Total points
    private val count = records.count()

    private val sample = count

    // Acceleration data
    private val aData by lazy {
        this.records.map { ARecord(it[0].toDouble(), (it[1].toDouble()), (it[2].toDouble())) }.take(sample)
    }

    // Distance data
    @OptIn(ObsoleteCoroutinesApi::class)
    val distance by lazy {

        // To be locked for sequential updating
        var seed = Quint(.0, .0, .0, .0, .0)
        var points = sequenceOf(Point(.0, .0))

        var total = 0.0

        aData.forEach { (time, ax, ay) ->
            val (prevX, prevY, prevVx, prevVy, prevT) = seed
            val dt = 0.4//time - prevT   //0.065
            seed = Quint(
                    first = prevX + prevVx*dt + (0.5*dt*dt*ax),
                    second = prevY + prevVy*dt + (0.5*dt*dt*ay),
                    third = dt*ax,
                    fourth = dt*ay,
                    time = time
            )

            println("$dt")

            total += sqrt(pow(seed.first-prevX, 2)+pow(seed.second-prevY, 2))
            points += Point(seed.first, seed.second)
        }
        points
    }
}