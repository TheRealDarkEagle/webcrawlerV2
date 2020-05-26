package markets.Utils

/**
 * @author: Kai Danz
 */
import interfaces.Scraper
import markets.Product
import org.jsoup.nodes.Document
import java.util.logging.Logger

class ProductScraper(val Scraper: Scraper) {

    fun scrapeProduct(document: Document) : Product? {
        val name = Scraper.getName(document)
        val price = Scraper.getPrice(document)
        val description = Scraper.getDesciption(document)
        val category = Scraper.getCategorie(document)
        val grammage = Scraper.getGrammage(document)
        val product = Product(Scraper.MARKET.MARKETNAME,name,price,description,grammage,category)
        if(product.isValid){
            return product
        }
        else{
            println("Product is INVALID -> ${product.toString()}")
            return null
        }
    }

}

