package adiweb.co.id.mengoocokdadudengansensoraccelerometer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import kotlin.math.abs

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var senAccelerometer: Sensor
    private var lastupdate: Long = 0
    private var last_x: Float = 0.0f
    private var last_y: Float = 0.0f
    private var last_z: Float = 0.0f
    private val SHAKE_THRESHOLD: Int = 600

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Memanggil Sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        senAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    /**
     * //Membuat Event pada saat sensor digunakan
     */
    override fun onSensorChanged(event: SensorEvent?) {
        val mySensor = event?.sensor
        if (mySensor != null) {
            if (mySensor.type == Sensor.TYPE_ACCELEROMETER){
                val x  = event.values[0]
                val y  = event.values[1]
                val z  = event.values[2]

                val curTime = System.currentTimeMillis()

                if ((curTime - lastupdate) > 100){
                    val diffTime = (curTime - lastupdate)
                    lastupdate = curTime

                    val speed = abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000

                    if (speed > SHAKE_THRESHOLD){
                        rollDice()
                    }

                    last_x = x
                    last_y = y
                    last_z = z
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL)


    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    /**
     * //Update Kocokan dadu.
     */
    private fun rollDice() {
        // Membuat dadu baru dengan bentuk 6 sisi
        val dice = Dice(6)
        val diceRoll1 = dice.roll()

        // Mencari Layout ImageView
        val diceImage: ImageView = findViewById(R.id.dadu1)

        // Menentukan ID drawbale yang akan digunkan dalam putaran dadu
        val drawableResource = when (diceRoll1) {
            1 -> R.drawable.dadu_1
            2 -> R.drawable.dadu_2
            3 -> R.drawable.dadu_3
            4 -> R.drawable.dadu_4
            5 -> R.drawable.dadu_5
            else -> R.drawable.dadu_6
        }

        diceImage.setImageResource(drawableResource)

        diceImage.contentDescription = diceRoll1.toString()
    }
}

/**
 * //Putaran dadu dengan hasil acak
 */
class Dice(private val numSides: Int) {

    fun roll(): Int {
        return (1..numSides).random()
    }
}