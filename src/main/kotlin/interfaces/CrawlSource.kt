package interfaces
import kotlinx.coroutines.delay
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.lang.IllegalStateException
import java.net.Socket
import java.net.SocketTimeoutException

/**
 * @author: Kai Danz
 */
interface CrawlSource {
    val entryPoints : HashSet<String>
    val productRelationId : String
    val baseURL: String
    fun loadProducts()
    fun getDocumentOf(entrypoint: String) : Document {
        var doc: Document
        try{
            doc = Jsoup.connect(entrypoint).get()
        } catch (e: SocketTimeoutException) {
            error("Restart downloading...")
            return getDocumentOf(entrypoint)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            return getDocumentOf(entrypoint)
        }
        return doc
    }
}