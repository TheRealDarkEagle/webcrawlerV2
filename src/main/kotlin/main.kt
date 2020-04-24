import interfaces.CrawlSource
import kotlinx.serialization.json.*
import markets.AldiNord
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File
/*
class Markt(override val marktName: String, override val entryPoints: HashSet<String>) : CrawlSource {

    override fun load() {
        TODO("Not yet implemented")
    }

    fun getProductOverview(): List<String> {
        //val doc = loadSite("C:\\Users\\Kai\\reweCrawler\\testpages\\productOverview\\productListPage1.txt")
        var overviewList : HashSet<Document> = HashSet()
        entryPoints.map {
            overviewList.add(Jsoup.connect(it).get())

        }
        val doc = Jsoup.connect("https://www.aldi-nord.de/produkte/aus-unserem-sortiment/muesli-cornflakes-cerealien/bio-genussriegel-3289.article.html").get()
        //Jsoup.connect("https://shop.rewe.de/productList?page=1")
        //danach suchen wir ->  <a href="/p/..../">
        println(doc)
        val productList = scrapeProductLink(doc)
        return productList
    }

    fun loadSite(path: String): Document =  Jsoup.parse(File(path).readText())

    fun scrapeProductLink(document: Document):List<String>{
        val list = document.body().getElementsByTag("a")
        var refs = mutableListOf<String>()
        list.map { var txt = it.toString()
            if(txt.startsWith("<a href=\"/p/")){
                refs.add(txt.substring(txt.indexOf("\"")+1,txt.indexOf("\n")-2))
            }
        }
        return refs
    }

    fun scrapeProductOf(){
        val group = File("C:\\Users\\Kai\\reweCrawler\\testpages\\productSites").listFiles()
        var first = group.first()
        //getProductof(loadSite(first.toString()))
        group.map { getProductof(loadSite(it.toString())) }

    }

    fun getProductof(site: Document){
        val json = getProductJson(site)
        val offer = Json.parseJson(json.get("offers").toString()) as JsonObject
        val productName = getValueof(json, "name")
        val price = getValueof(offer,"price").toDouble()
        val description = getValueof(json, "description")
        val breadcrumb = scrapeBreadCrumb(site)
        sendProduct(productName,price, description, breadcrumb)
    }
    private fun scrapeBreadCrumb(doc: Document): String{
        val scripts = doc.body().getElementsByTag("script")
        var breadrumbScript :JsonObject? = null
        scripts.map {
            if(it.toString().contains("\"BreadcrumbList\",")){
                var txt = it.toString()
                txt = txt.substring(txt.indexOf(">")+1, txt.lastIndexOf("<")).trim()
                breadrumbScript = Json.parseJson(txt) as JsonObject
            }
        }
        var jsonArray = breadrumbScript?.get("itemListElement") as JsonArray
        val breadcrump = convertToJO(jsonArray[0])
        val items = breadcrump.get("item")?.let { convertToJO(it) }
        if(items != null){
            var jsonEntityText = items.get("name").toString()
            return jsonEntityText.replace("&amp;", "&")
        }
        return ""
    }
    //Converts a given Json area into a JsonObject
    private fun convertToJO(area: JsonElement): JsonObject =  area as JsonObject

    private fun getValueof(json: JsonObject, key: String): String{
        var value = json.get(key).toString()
        value = value.substring(1,value.length-1)
        return value.replace("\n","")
    }
    private fun getProductJson(ofDocument: Document): JsonObject{
        var script = ofDocument.head().getElementsByTag("script").last().toString()
        script = script.substring(script.indexOf(">")+1, script.lastIndexOf("<")).trimStart().trimEnd()
        return Json.parseJson(script) as JsonObject
    }

    private fun sendProduct(name: String, price: Double, desc: String, category: String?){
        println("Name: ${name} \nPrice: ${price}€\nDescription: ${desc}\nCategory: ${category}\n\n")
    }
}
*/

fun main(){
    val aldiNord = AldiNord()
    aldiNord.loadProducts()
    //getProductOverview()
    //scrapeProductOf()
}
