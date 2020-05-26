import crawler.Crawler
import markets.aldiNord.AldiNordScraper


/**
 * @author: Kai Danz
 */
fun main(){
    Crawler(AldiNordScraper()).loadProducts()
}
