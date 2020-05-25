import markets.Crawler
import markets.Product
import markets.Utils.ProductSender
import markets.aldiNord.AldiNordScraper


/**
 * @author: Kai Danz
 */
fun main(){
    Crawler(AldiNordScraper()).loadProducts()
/*
    val sender = ProductSender
    val p = Product("","",0.0, "",0,"")
    for (x in 0..100_000) {
        sender.send(p)
        println(x)
    }
 */
}
