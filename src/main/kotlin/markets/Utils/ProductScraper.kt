package markets.Utils

import interfaces.Scraper
import markets.Product
import org.jsoup.nodes.Document
import java.util.logging.Logger

class ProductScraper(val document: Document) {

    fun scrapeProduct(marketScraper: Scraper) : Product? {
        val name = marketScraper.getName(document)
        val price = marketScraper.getPrice(document)
        val description = marketScraper.getDesciption(document)
        val category = marketScraper.getCategorie(document)
        val grammage = marketScraper.getGrammage(document)
        val product = Product(name,price,description,grammage,category)
        println(product.toString())
        if(product.isValid){
            println("Product is valid!")
            return product
        }
        else{
            println("Product is INVALID -> ${product.toString()}")
            return null
        }
    }

}

