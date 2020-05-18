import markets.Crawler
import markets.Utils.ProductSender
import markets.aldiNord.AldiNordScraper


/**
 * @author: Kai Danz
 */
fun main(){

    val aldiNord = Crawler(AldiNordScraper(), true)
    aldiNord.loadProducts()
}
