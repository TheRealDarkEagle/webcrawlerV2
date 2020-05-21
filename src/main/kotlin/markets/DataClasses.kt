package markets

/**
 * @author: Kai Danz
 */
data class Product(val marketName: String, val productName: String, val currentPrice: Double, val productInfo: String, val productGrammage: Int, val categoryName: String){
    val isValid :Boolean
        get() = (hasContent(productName)&&hasContent(currentPrice)&&hasContent(productInfo)&&hasContent(productGrammage)&&hasContent(categoryName))
    private fun hasContent(s:String) = s.isNotEmpty()
    private fun hasContent(d:Double) = d >= 0
    private fun hasContent(i:Int) = i >= 0
    override fun toString(): String = "{\n" +
            "    \"marketName\":\"$marketName\",\n" +
            "    \"categoryName\":\"$categoryName\",\n" +
            "    \"productName\":\"$productName\",\n" +
            "    \"productInfo\":\"$productInfo\",\n" +
            "    \"currentPrice\":\"$currentPrice\",\n" +
            "    \"rabbatPrice\":\" \",\n" +
            "    \"productGrammage\":\"$productGrammage\"\n" +
            "\n}"
}