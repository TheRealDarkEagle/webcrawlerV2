package interfaces
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * @author: Kai Danz
 */
interface CrawlSource {
    val entryPoints : HashSet<String>
    val productRelationId : String
    val baseURL: String
    fun loadProducts()
    fun getDocumentOf(entrypoint: String) : Document = Jsoup.connect(entrypoint).get()
}