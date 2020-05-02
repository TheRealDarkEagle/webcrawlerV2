package markets

/**
 * @author: Kai Danz
 */
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File
import java.nio.charset.Charset
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Paths

class Helper (val overviewPath: String, val detailviewPath: String){

    val home = Paths.get(System.getProperty("user.home")).fileSystem

    fun loadDetailView(): HashSet<Document> {
        println(detailviewPath)
        val path = detailviewPath
        val set = hashSetOf<Document>()
        File(path).listFiles().map {
            set.add(Jsoup.parse((it.readText())))
        }
        return set
    }

    fun loadOverView(): HashSet<Document> {
        println(overviewPath)
        val path = overviewPath
        val set = hashSetOf<Document>()
        File(path).listFiles().map {
            set.add(Jsoup.parse((it.readText())))
        }
        return set
    }


}
