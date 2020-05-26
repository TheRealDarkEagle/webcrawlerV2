package crawler

import enums.LinkType
import interfaces.CrawlSource
import interfaces.Scraper
import kotlinx.coroutines.*
import markets.Product
import markets.Utils.ProductScraper
import markets.Utils.ProductSender
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

/**
 * @author: Kai Danz
 */

data class SendableProduct(val product: Product?)
class Crawler(val SCRAPER: Scraper, val TESTING : Boolean = false) : CrawlSource {

    override val entryPoints: HashSet<String>
        get() = SCRAPER.MARKET.ENTRYPOINTS
    override val productRelationId: String
        get() = SCRAPER.MARKET.DETAILVIEWLINKIDENTIFIER
    override val baseURL: String
        get() = SCRAPER.MARKET.MARKETURL
    private var products : MutableSet<SendableProduct> = mutableSetOf()


  /*
  Entry Point of every crawler.Crawler
   */
    override fun loadProducts() {
      val t= loadingJobs()

     run(t)
   }

    private fun loadingJobs() :Set<Job> {

        var test : MutableSet<Job> = mutableSetOf()
            when (TESTING) {
            true -> SCRAPER.TESTER?.loadDetailView()?.let { startLoading(it, true) }
            false -> entryPoints.map {

                test.add(startLoading(it))
            }
        }
        return test
    }


    private fun run(jobs: Set<Job>) = runBlocking {
       jobs.map { it.join() }
        val list = mutableSetOf<Product>()
        products.map {
            if(it.product != null){
                list.add(it.product)
            }
        }
        val sender = ProductSender()
        sender.send2(list)


        println("Job done!")

    }

    /*
    Testing Function
     */
    private fun startLoading(docs: HashSet<Document>, isDetailView : Boolean = false) = runBlocking{
        withContext(Dispatchers.IO){
            docs.map {
                launch {
                    val doc = it
                    val links = SCRAPER.scrapeHtmlTag(doc, "a")
                    for (x in 0..100_000){
                       // println(x)
                        if(isDetailView){
                            val t =  extractProduct2(doc)
                            if(t != null){
                                println("Done!?")
                                products.add(t)
                            }
                            else
                                cleaning(links)
                        }
                    }

                }
            }
        }
    }

    /*
    Live-Environment
     */
    private fun startLoading(url: String) = runBlocking{
        withContext(Dispatchers.IO){
            launch {
                val doc = getDocumentOf(url)
                println(url)
                println(url.contains(SCRAPER.MARKET.DETAILVIEWLINKIDENTIFIER))
                if(url.contains(SCRAPER.MARKET.DETAILVIEWLINKIDENTIFIER)){
                    extractProduct2(doc)
                }
                else{
                    cleaning(SCRAPER.scrapeHtmlTag(doc, "a"))
                }
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


            /*
            if(product.isValid) {
                val SENDER = ProductSender()
               SENDER.send(product)
            }

             */

        }
    }

    private fun extractProduct2(productDocument: Document) :SendableProduct? = SendableProduct(ProductScraper(productDocument).scrapeProduct(SCRAPER))


}


