package advent25

class Resources {
    fun loadString(name: String): String {
        return this::class.java.getResourceAsStream("input/$name")?.bufferedReader()?.readText() ?: ""
    }
}