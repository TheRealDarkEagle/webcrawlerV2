import com.sun.org.apache.xml.internal.dtm.ref.CoroutineManager

object LoadingThreadPool : CoroutineManager() {


    fun createCoRoutine(url: Unit){
        println("STarting coroutinte!")

        Thread.sleep(5000)

        println("Ending Coroutine!")
    }
}