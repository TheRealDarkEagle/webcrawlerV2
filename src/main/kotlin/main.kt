import markets.Crawler
import markets.aldiNord.AldiNordScraper


fun main(){
    val aldiNord = Crawler(AldiNordScraper())
    aldiNord.loadProducts()
}
