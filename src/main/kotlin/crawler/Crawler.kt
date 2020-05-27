package crawler

import enums.LinkType
import interfaces.CrawlSource
import interfaces.Scraper
import kotlinx.coroutines.*
import markets.Product
import markets.Utils.ProductScraper
import markets.Utils.ProductSender
import org.jsoup.nodes.Document

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

    private val Sender = ProductSender

    private var products :MutableSet<Product> = mutableSetOf()

    private val crawledLinks = mutableListOf<String>()


  /*
  Entry Point of every crawler.Crawler
   */
    override fun loadProducts() {
     run()
   }


    private fun run() = runBlocking {
        println("Starting Crawling!")
        val crawlProcess = entryPoints.map {
            launch(Dispatchers.Default) {
                //Stoße seitenladen an!
                loadingRoutine(it)
            }
        }
        crawlProcess.map { it.join() }
        println ("Crawling Done!")
        println("Starting sending!")
        Sender.send2(products)
        println("Sending done!")
    }


    private fun loadingRoutine(url: String) = runBlocking {
        println(url)
        if(!crawledLinks.contains(url)){
            crawledLinks.add(url)
            val loadingPhase = launch {
                val doc = getDocumentOf(url)
                if(url.contains(SCRAPER.MARKET.DETAILVIEWLINKIDENTIFIER)){
                   // println("extracting produkts")
                    extractProduct(doc)
                }else{
                    loadOverview(doc)
                }
            }
            loadingPhase.join()
        }
    }

    /*
    Filters out the Product-Related Links
     */
    private fun loadOverview(doc: Document) {
        val links = SCRAPER.scrapeHtmlTag(doc, "a")
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
            if (!crawledLinks.contains(it)) {
                if (it.contains(SCRAPER.MARKET.DETAILVIEWLINKIDENTIFIER))
                    dvLinkSet.add(it)
                else
                    ovLinkSet.add(it)
            }
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
           // println("new ProductList -> ${links[LinkType.DETAILVIEW]}\n\n")
            startNextLoading(links[LinkType.DETAILVIEW] ?: error("An Error Occures!?!=!"))
        }
        else
            startNextLoading(links[LinkType.PRODUCTOVERVIEW] ?: error("ERROR"))
    }

    /*
    Initiates the Next Loading Phase
     */
    private fun startNextLoading(list: Set<String>) = runBlocking{
      // println("startNExtLoading -> $list")
        launch {
            list.map {
                launch {
                    loadingRoutine(baseURL+it)
                }.join()
            }
        }.join()
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
        val scraper = ProductScraper(SCRAPER)
        val product = scraper.scrapeProduct(productDocument)
        if (product != null && product.isValid) {
             products.add(product)
        }
    }
}


