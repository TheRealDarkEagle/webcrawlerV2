package markets
import enums.LinkType
import interfaces.CrawlSource
import javafx.application.Application.launch
import kotlinx.coroutines.*
import org.jsoup.select.Elements

/*

    author: Kai Danz

 */
class AldiNord : CrawlSource {

    override val marketName: String
        get() = "Aldi-Nord"
    override val entryPoints: HashSet<String>
        get() = hashSetOf("https://www.aldi-nord.de/produkte.html")
    override val productRelationId: String
        get() = "/produkte/"
    override val marketUrl: String
        get() = "https://www.aldi-nord.de"

    //Testzwecke!
    private val poSite = "https://www.aldi-nord.de/produkte/unsere-marken/almare.html"

    override fun loadProducts() {
       // entryPoints.map { startLoading(it) }
        startLoading(poSite)
    }

    //Starts the Loading and Handling of the given URL
    private fun startLoading(url: String) {
        val doc = getDocumentOf(url)
        val links = scrapeHtmlTag(doc, "a")
        if(url.contains(".article"))
            saveDoc(doc)
        else
            cleaning(links)
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
            if(it.contains(".article"))
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
            async(Dispatchers.Default) {
            jobexample(marketUrl+it)
            }
        }
    }


    private fun jobexample(url : String){
        startLoading(url)
    }

    //cleans the Set of the Trash
    private fun cleanLinks(links: Set<String>): Set<String> {
        val cleanedSet : MutableSet<String> = mutableSetOf()
        links.map {
            cleanedSet.add(cutOutLink(it))
        }
        return cleanedSet
    }

    //Extracts the real link of the String
    private fun cutOutLink(s : String) : String {
        var start = s.indexOf('"')
        var end = s.indexOf('"',start+1)
        return s.substring(start+1,end)
    }

    private fun getProductLinks(list: List<String>) : Set<String>{
        val sortedLinks : MutableSet<String> = mutableSetOf()
        list.map {
            if(it.contains(productRelationId)){
                sortedLinks.add(it)
            }
        }
        return sortedLinks
    }
    private fun saveDoc(doc: org.jsoup.nodes.Document){
       TODO("Do something with the Identified DetailView product Link!")
    }
}

