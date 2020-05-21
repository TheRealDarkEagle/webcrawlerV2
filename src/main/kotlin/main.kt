import markets.Crawler
import markets.Utils.ProductSender
import markets.aldiNord.AldiNordScraper


/**
 * @author: Kai Danz
 */
fun main(){
    Crawler(AldiNordScraper()).loadProducts()
}
