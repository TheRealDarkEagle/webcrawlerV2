package markets
import enums.LinkType
import interfaces.CrawlSource
import interfaces.Scraper
import kotlinx.coroutines.*
import markets.Utils.ProductScraper
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

/*

    author: Kai Danz

 */
class Crawler(val SCRAPER: Scraper) : CrawlSource {
    val HELPER = SCRAPER.TESTER


    override val entryPoints: HashSet<String>
        get() = SCRAPER.MARKET.ENTRYPOINTS
    override val productRelationId: String
        get() = SCRAPER.MARKET.DETAILVIEWLINKIDENTIFIER
    override val baseURL: String
        get() = SCRAPER.MARKET.MARKETURL
    val TESTING = false
              //Entry Point of every Crawler
        override fun loadProducts() {
            // entryPoints.map { startLoading(it) }
            //startLoading(poSite2, false)
               when(TESTING){
                   true -> startLoading(HELPER.loadDetailView(),true)
                    false -> entryPoints.map { startLoading(it) }
               }
           }

        private fun startLoading(docs: HashSet<Document>, isDetailView : Boolean = false) = runBlocking{
            docs.map {

                launch {
                    val doc = it
                    //getDocumentOf(url)
                    val links = SCRAPER.scrapeHtmlTag(doc, "a")
                    if(isDetailView){
                        saveDoc(doc)
                    }
                    else
                        cleaning(links)
                }
            }
        }

        //Starts the Loading and Handling of the given URL
        private fun startLoading(url: String) = runBlocking{
            launch {
                val doc = getDocumentOf(url)
                val links = SCRAPER.scrapeHtmlTag(doc, "a")
                if(url.contains(SCRAPER.MARKET.DETAILVIEWLINKIDENTIFIER)){
                    saveDoc(doc)
                }
                else
                    cleaning(links)
            }
        }

        //Management Function to Clean the Links of unnecessary parts
        private fun cleaning(links: Elements) {
            val textLinks = links.map { it.toString() }
            val extractedLinks = getProductLinks(textLinks)
            val cleanedLinks = cleanLinks(extractedLinks)
            determine(cleanedLinks)
        }

        //Determs if the Link in the Set is a DetailView-Link or a ProductOverview-Link
        private fun determine(links: Set<String>) {
            val dvLinkSet  = mutableSetOf<String>()
            val ovLinkSet = mutableSetOf<String>()
            //Wenn eine dv verlinkung da ist
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

        //Divides the Links in DetailView and ProductOverview
        private fun prepareLoading(links: Map<LinkType, Set<String>>){
            if(!links[LinkType.DETAILVIEW]?.isEmpty()!!)
                startNextLoading(links.get(LinkType.DETAILVIEW)!!)
            else
                startNextLoading(links.get(LinkType.PRODUCTOVERVIEW)!!)
        }

        //Initiates the Next Loading Phase
        private fun startNextLoading(list: Set<String>) = runBlocking{
            list.map {
                println("ProductLinks -> ${baseURL+it}")
                async(Dispatchers.Default) {
                   startLoading(baseURL+it)
                }
            }
        }

        //cleans the Set of the Trash
        private fun cleanLinks(links: Set<String>): Set<String> {
            val cleanedSet : MutableSet<String> = mutableSetOf()
            links.map {
                cleanedSet.add(SCRAPER.cutOutLink(it))
            }

            return cleanedSet

        }



        //Exctract the Product-Related Links of the List
        private fun getProductLinks(list: List<String>) : Set<String>{
            val sortedLinks : MutableSet<String> = mutableSetOf()
            list.map {
                if(it.contains(SCRAPER.MARKET.ARTICELIDENTIFIER)){
                    sortedLinks.add(it)
                }
            }
            return sortedLinks
        }

        //Save Function
        private fun saveDoc( productDocument: org.jsoup.nodes.Document){
            ProductScraper(productDocument).scrapeProduct(SCRAPER)

        }
    }


