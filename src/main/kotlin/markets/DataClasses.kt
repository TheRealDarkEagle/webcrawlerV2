package markets

/**
 * @author: Kai Danz
 */
data class Product(val productName: String, val currentPrice: Double, val productInfo: String, val productGrammage: Int, val categoryName: String){
    val isValid :Boolean
        get() = (hasContent(productName)&&hasContent(currentPrice)&&hasContent(productInfo)&&hasContent(productGrammage)&&hasContent(categoryName))
    private fun hasContent(s:String) = !s.isEmpty()
    private fun hasContent(d:Double) = d >= 0
    private fun hasContent(i:Int) = i >= 0
    override fun toString(): String = "name: ${productName},price: ${currentPrice},description: ${productInfo},grammage: ${productGrammage},category: ${categoryName}"
}
