package markets.aldiNord

import interfaces.CrawlObject
import interfaces.Scraper
import markets.Helper
import org.jsoup.nodes.Document


class AldiNordScraper : Scraper {

    override val MARKET: CrawlObject
        get() = AldiNord()
    override val TESTER: Helper
        get() = Helper("C:\\Users\\Kai\\IdeaProjects\\webcrawlerV2\\src\\main\\resources\\SiteData\\productOverview\\Aldi-Nord",
                        "C:\\Users\\Kai\\IdeaProjects\\webcrawlerV2\\src\\main\\resources\\SiteData\\ProductSites\\Aldi-Nord"
    )


    override fun getName (d: Document) :String = scrapeHtmlTag(d, "h1")[0].text()

    override fun getPrice(d: Document): Double {
        val spans = scrapeHtmlTag(d, "span")
        var priceTag = searchForClass("price__main", spans)
        val price = priceTag?.childNode(0).toString()
         return price.toDouble()
    }

    override fun getDesciption(d: Document): String {
        val divs = scrapeHtmlTag(d, "div")
        val infoTag = searchForClass("rte",divs)?.childNode(1)?.childNode(0)?.toString()
        if(infoTag!==null){
            return infoTag
        }
        return ""
    }

    override fun getCategorie(document: Document): String {

        return "Category"
    }

    override fun getGrammage(document: Document): Int {
        return -1
    }

}
