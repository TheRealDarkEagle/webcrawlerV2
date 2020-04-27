import markets.Crawler
import markets.aldiNord.AldiNordScraper

/*
Author : Kai Danz
 */

fun main(){
    val aldiNord = Crawler(AldiNordScraper())
    aldiNord.loadProducts()
}
