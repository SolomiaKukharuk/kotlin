import kotlin.random.Random

fun main(){
    task3_1()
    task3_3()
    task3_4()
    task3_6()
    task3_7()
}

fun task3_1(){
    val a = readln().toInt()
    val b = readln().toInt()
    var summ1 = 0
    var summ2 = 0

    (1..b).forEach {
        summ1 += a
    }
    repeat(a){
        summ2 += b
    }
    println(summ1)
    println(summ2)
}

fun task3_3(){
    val numbers = IntArray(100) {
        Random.nextInt(-100, 101)
    }

    val positive = numbers.filter { it > 0 }

    val divisibleBy5 = positive.filter { it % 5 == 0 }

    val squared = divisibleBy5.map { it * it }

    val sorted = squared.sortedDescending()

    println("Найбільше: ${sorted.maxOrNull()}")
    println("Найменше: ${sorted.minOrNull()}")

    val strings = sorted.map { it.toString() }

    val result = strings.joinToString(" ")

    println(result)
}

fun countStrings(
    strings: Collection<String>,
    predicate: (String) -> Boolean
): Int {
    return strings.count { predicate(it) }
}

fun task3_4() {
    val strings = readln()
        .trim()
        .split("\\s+".toRegex())

    val result = countStrings(strings) { str ->
        str.any { it.isDigit() }
    }

    println(result)
}

//task3_5
fun stringFilter(
    strings: Collection<String>,
    predicate: (String) -> Boolean
): Collection<String> {
    return strings.filter { predicate(it) }
}

fun task3_6(){
    val s = readln()

    val counts = s.groupingBy { it }.eachCount()

    println(counts)
}

fun task3_7() {
    val rawLogs = listOf(
        "2026-09-01 INFO: User logged in",
        "2026-09-01 ERROR: 500 Internal Server Error",
        "2026-09-01 WARN: High memory usage",
        "2026-09-01 ERROR: 404 Not Found",
        "2026-09-01 INFO: Payment processed"
    )

    val types = rawLogs.map { log ->
        when {
            log.contains("INFO") -> "INFO"
            log.contains("WARN") -> "WARN"
            log.contains("ERROR") -> "ERROR"
            else -> "UNKNOWN"
        }
    }

    val infoCount = types.count { it == "INFO" }
    val warnCount = types.count { it == "WARN" }
    val errorCount = types.count { it == "ERROR" }

    val errors = rawLogs
        .filter { it.contains("ERROR") }
        .toMutableList()

    println(
        """
        INFO: $infoCount
        WARN: $warnCount
        ERROR: $errorCount
        
        Errors:
        ${errors.joinToString("\n")}
        """.trimIndent()
    )
}