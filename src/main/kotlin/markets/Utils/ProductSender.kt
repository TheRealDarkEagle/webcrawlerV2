package markets.Utils

import java.net.HttpURLConnection
import java.net.URL


/**
 * @author: Kai Danz
 */
object ProductSender {
    val URL : URL
    val con : HttpURLConnection
    init {
        URL = URL("127.0.0.1:9000/new")
        con = URL.openConnection() as HttpURLConnection
        con.requestMethod = "POST"
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.doOutput = true;
    }

    //TODO("Not yet implemented")
    fun send(productJson: String) {
        con.outputStream.use { os ->
            val input: ByteArray = productJson.toByteArray()
            os.write(input, 0, input.size)
        }
    }

}
