package markets.Utils
import markets.Product
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder


/**
 * @author: Kai Danz
 */

/*
@TODO: bau productsender wieder in ein Singleton um
        Gebe Queue mit (Non-Blocking?)
           Muss THREAD-SAVE sein (!)

        Variante 2:
        als Future bauen
 */
class ProductSender {

    private val httpClient = HttpClientBuilder.create().build()
    private val httpRequest = HttpPost("http://localhost:9000/collatio/product")

    init {
        httpRequest.addHeader("content-Type", "application/json; charset=UTF-8;")
    }
        fun send(product: Product) {
            var productJson: String = createProductJson(product)

            //We have Problems with the Encoding of äöü so we need to replace them till we find a Solution
            productJson = replaceBadChars(productJson)

            /*  productJson = "{\n" +
                    "\t\"marketName\":\" \",\n" +
                    "\t\"categoryName\":\" \",\n" +
                    "\t\"productName\":\" \",\n" +
                    "\t\"productInfo\":\" \",\n" +
                    "\t\"currentPrice\":\" \",\n" +
                    "\t\"rabbatPrice\":\"\",\n" +
                    "\t\"productGrammage\":\" \"\n" +
                    "\n" +
                    "}"

           */

            val requestEntity = StringEntity(productJson)
            requestEntity.setContentEncoding("UTF-8")
            requestEntity.setContentType("application/json; UTF-8")
            httpRequest.entity = requestEntity

            httpClient.execute(httpRequest)


        }

        private fun createProductJson(product: Product) =
            """{"marketName":"${product.marketName}","categoryName":"${product.categoryName}","productName":"${product.productName}","productInfo":"${product.productInfo}","currentPrice":"${product.currentPrice}","rabbatPrice":"","productGrammage":"${product.productGrammage}"}"""

        private fun replaceBadChars(s: String): String{
            var text = s
            if(text.contains("ö"))
                text = replace(text,'ö',"oe")
            if(text.contains('ä'))
                text = replace(text,'ä',"ae")
            if(text.contains('ü'))
                text = replace(text,'ü',"ue")
            return text
        }

        private fun replace(string: String, withChar: Char, newValue: String) = string.replace(""+withChar,newValue)
    }



