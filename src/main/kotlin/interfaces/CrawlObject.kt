package interfaces

/**
 * @author: Kai Danz
 */
interface CrawlObject {

    val MARKETNAME: String
    val ENTRYPOINTS: HashSet<String>
    val DETAILVIEWLINKIDENTIFIER: String
    val MARKETURL: String
    val ARTICELIDENTIFIER: String
}