package markets.Utils

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class Scraper {



    //returns Tags
    fun scrapeHtmlTag(doc : Document, tag: String) : Elements = doc.body().getElementsByTag(tag)
}