package markets.Utils

import java.net.HttpURLConnection
import java.net.URL


/**
 * @author: Kai Danz
 */
object ProductSender {
    private val URL : URL = URL("127.0.0.1:9000/new")
    private val con : HttpURLConnection

    init {
        con = URL.openConnection() as HttpURLConnection
        con.requestMethod = "POST"
        con.setRequestProperty("Content-Type", "application/json; utf-8")
        con.doOutput = true
    }

    fun send(productJson: String) {
        con.outputStream.use { os ->
            val input: ByteArray = productJson.toByteArray()
            os.write(input, 0, input.size)
        }
    }

}
