package markets.Utils

import interfaces.Scraper
import org.jsoup.nodes.Document

class ProductScraper(val document: Document) {


    fun scrapeProduct(marketScraper: Scraper) {
        val name = marketScraper.getName(document)
        val price = marketScraper.getPrice(document)
        val description = marketScraper.getDesciption(document)
        val category = marketScraper.getCategorie(document)
        val grammage = marketScraper.getGrammage(document)

        giveContent(linkedSetOf<String>(name, price.toString(), description, category, grammage.toString()))
    }

    private fun giveContent(props: LinkedHashSet<String>) {
        println(props)
        props.map {
            println(it)
        }
    }

}

