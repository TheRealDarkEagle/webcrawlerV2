package markets.aldiNord
/*
Author : Kai Danz
 */
import interfaces.CrawlObject

class AldiNord : CrawlObject {

    override val MARKETNAME: String
        get() = "Aldi-Nord"
    override val ENTRYPOINTS: HashSet<String>
        get() = hashSetOf("https://www.aldi-nord.de/produkte.html")
    override val DETAILVIEWLINKIDENTIFIER: String
        get() = ".article"
    override val MARKETURL: String
        get() = "https://www.aldi-nord.de"
    override val ARTICELIDENTIFIER: String
        get() = "/produkte/"
}
