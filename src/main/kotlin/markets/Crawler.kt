package markets

import enums.LinkType
import interfaces.CrawlSource
import interfaces.Scraper
import kotlinx.coroutines.*
import markets.Utils.ProductScraper
import markets.Utils.ProductSender
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

/**
 * @author: Kai Danz
 */
class Crawler(val SCRAPER: Scraper, val TESTING : Boolean = false) : CrawlSource {

    override val entryPoints: HashSet<String>
        get() = SCRAPER.MARKET.ENTRYPOINTS
    override val productRelationId: String
        get() = SCRAPER.MARKET.DETAILVIEWLINKIDENTIFIER
    override val baseURL: String
        get() = SCRAPER.MARKET.MARKETURL


  /*
  Entry Point of every Crawler
   */
    override fun loadProducts() {
           when(TESTING){
                true -> SCRAPER.TESTER?.loadDetailView()?.let { startLoading(it,true) }
                false -> entryPoints.map { startLoading(it) }
           }
       }

    /*
    Testing Function
     */
    private fun startLoading(docs: HashSet<Document>, isDetailView : Boolean = false) = runBlocking{
        docs.map {
            launch {
                val doc = it
                val links = SCRAPER.scrapeHtmlTag(doc, "a")
                if(isDetailView){
                    extractProduct(doc)
                }
                else
                    cleaning(links)
            }
        }
    }

    /*
    Live-Environment
     */
    private fun startLoading(url: String) = runBlocking{
        launch {
            val doc = getDocumentOf(url)
            println(url)
            println(url.contains(SCRAPER.MARKET.DETAILVIEWLINKIDENTIFIER))
            if(url.contains(SCRAPER.MARKET.DETAILVIEWLINKIDENTIFIER)){
                extractProduct(doc)
            }
            else{
                cleaning(SCRAPER.scrapeHtmlTag(doc, "a"))
            }
        }
    }

    /*
    Filters out the Product-Related Links
     */
    private fun cleaning(links: Elements) {
        val textLinks = links.map { it.toString() }
        val extractedLinks = getProductLinks(textLinks)
        val cleanedLinks = cleanLinks(extractedLinks)
        determine(cleanedLinks)
    }

    /*
    Detects if the Link in the LinkSet is a DV link or PO link
     */
    private fun determine(links: Set<String>) {
        val dvLinkSet  = mutableSetOf<String>()
        val ovLinkSet = mutableSetOf<String>()
        links.map {
            if(it.contains(SCRAPER.MARKET.DETAILVIEWLINKIDENTIFIER))
                dvLinkSet.add(it)
            else
                ovLinkSet.add(it)
        }
        prepareLoading(mapOf(
            LinkType.DETAILVIEW to dvLinkSet,
            LinkType.PRODUCTOVERVIEW to ovLinkSet))

    }

    /*
    Divides the Links in DetailView and ProductOverview and direct it to the next Loading Phase
     */
    private fun prepareLoading(links: Map<LinkType, Set<String>>){
        //If there are any DV links ignore the Rest
        if((links[LinkType.DETAILVIEW] ?: error("DUUUUDE")).isNotEmpty()) {
            println(links[LinkType.DETAILVIEW])
            startNextLoading(links[LinkType.DETAILVIEW] ?: error("An Error Occures!?!=!"))
        }
        else
            startNextLoading(links[LinkType.PRODUCTOVERVIEW] ?: error("ERROR"))
    }

    /*
    Initiates the Next Loading Phase
     */
    private fun startNextLoading(list: Set<String>) = runBlocking{
        list.map {
            async(Dispatchers.Default) {
               startLoading(baseURL+it)
            }
        }
    }

    /*
    cleans the Set of the Trash that we do not want
     */
    private fun cleanLinks(links: Set<String>): Set<String> {
        val cleanedSet : MutableSet<String> = mutableSetOf()
        links.map {
            cleanedSet.add(SCRAPER.cutOutLink(it))
        }
        return cleanedSet
    }

    /*
    Exctract the Product-Related Links of the List
     */
    private fun getProductLinks(list: List<String>) : Set<String>{
        val sortedLinks : MutableSet<String> = mutableSetOf()
        list.map {
            if(it.contains(SCRAPER.MARKET.ARTICELIDENTIFIER)){
                sortedLinks.add(it)
            }
        }
        return sortedLinks
    }

    /*
    Initiates the Product Loading Phase
     */
    private fun extractProduct(productDocument: Document){
        val product = ProductScraper(productDocument).scrapeProduct(SCRAPER)
        if (product != null) {
            if(product.isValid) {
                val SENDER = ProductSender()
               SENDER.send(product)
            }
        }
    }
}


