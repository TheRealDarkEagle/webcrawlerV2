package interfaces

import kotlinx.serialization.PrimitiveKind
import markets.Helper
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

interface Scraper{


    val MARKET: CrawlObject
    val TESTER: Helper

    /*
    ProductMarketForm form = new ProductMarketForm();
        form.setCategoryName("Fleisch");
        name        -   string
        preis       -   double
        info        -   String
        Kategorie   -   string
        grammage    -   int

        form.setMarketName("Aldi");
        form.setProductName("Mango");
        form.setCurrentPrice(1.25);
        form.setProductInfo("Leckeres Obst das jeder essen will! schmeckt super");
        form.setProductGrammage(280);

     */

    fun getName(document: Document): String
    fun getPrice(document: Document): Double
    fun getDesciption(document: Document): String
    fun getCategorie(document: Document): String
    fun getGrammage(document: Document) : Int

    /*
    returns Tags
     */
    fun scrapeHtmlTag(doc : Document, tag: String) : Elements = doc.body().getElementsByTag(tag)

   fun searchForClass(classname: String, e : Elements) : Element? {
        e.map {
            if(it.hasClass(classname)){
                return it
            }
        }
        return null
    }
    //Extracts the real link of the String
    fun cutOutLink(s : String) : String {
        var start = s.indexOf('"')
        var end = s.indexOf('"',start+1)
        return s.substring(start+1,end)
    }

}