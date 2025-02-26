package markets.aldiNord

import interfaces.CrawlObject
import interfaces.Scraper
import markets.Helper
import org.jsoup.nodes.Document
import java.lang.NumberFormatException

/**
 * @author: Kai Danz
 */
class AldiNordScraper : Scraper {

    override val MARKET: CrawlObject
        get() = AldiNord()
    override val TESTER: Helper?
        get() = Helper("C:\\Users\\Kai\\IdeaProjects\\webcrawlerV2\\src\\main\\resources\\SiteData\\productOverview\\Aldi-Nord",
                        "C:\\Users\\Kai\\IdeaProjects\\webcrawlerV2\\src\\main\\resources\\SiteData\\ProductSites\\Aldi-Nord"
    )

    override fun getName (d: Document) :String = scrapeHtmlTag(d, "h1")[0].text()

    override fun getPrice(d: Document): Double {
        val spans = scrapeHtmlTag(d, "span")
        val priceTag = searchForClass("price__main", spans)
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

    override fun getCategorie(d: Document): String {
        val list = scrapeHtmlTag(d, "ol")
        val breadcrump = searchForClass("mod-breadcrumb__nav", list)?.childNode(5).toString()
        val ref  = cutOutLink(breadcrump)
        return  ref.substringAfterLast('/').replace(".html","").replace("-"," ")
    }

    override fun getGrammage(document: Document): Int {
        val spans = scrapeHtmlTag(document,"span")
        val grammageText = searchForClass("price__unit",spans)?.childNode(0).toString()
        return if(!grammageText.contains(Regex("\\d")))
                    1
                else
                    exctractGramage(grammageText)

    }

    private fun exctractGramage(s: String): Int {
      val possibleGrammage = exctractBy(Regex("\\d*.*\\d"),s)
        return  if(possibleGrammage==null)
                    -1
                else
                    calculate(possibleGrammage)
    }

    private fun exctractBy(regex: Regex, text: String): String? = regex.find(text)?.value?.substring(1)

   // TODO("Handle Excpetion!")
    private fun calculate(s:String): Int{
        try {
            return s.toInt()
        }catch (e: NumberFormatException){
            println("possible calculation found ... trying to calculate! -> $s")
            try {
                return calculateExpression(s)
            }
            catch (e: Exception){
                e.printStackTrace()
                return -1
            }
        }
        catch (e : Exception){
            e.printStackTrace()

        }
        return -1
    }

    fun calculateExpression(s:String): Int {
        if(s.contains("x")){
            return (s.substring(0,s.indexOf("x",0)).trim().toInt()*s.substring(s.indexOf("x",0)+1).trim().toInt())
        }
        /*else if(s.contains(".")){
            return s.replace(".","").toInt()
        }*/
        return -1
    }
}
