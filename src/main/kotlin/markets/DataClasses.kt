package markets

data class Product(val name: String, val price: Double, val description: String, val grammage: Int, val category: String){
    val isValid :Boolean
        get() = (hasContent(name)&&hasContent(price)&&hasContent(description)&&hasContent(grammage)&&hasContent(category))
    private fun hasContent(s:String) = !s.isEmpty()
    private fun hasContent(d:Double) = d >= 0
    private fun hasContent(i:Int) = i >= 0
    override fun toString(): String = "name: ${name},price: ${price},description: ${description},grammage: ${grammage},category: ${category}"
}
