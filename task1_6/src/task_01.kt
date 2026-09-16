data class Address(
    val street: String,
    val city: String?,
    val postalCode: String?
)

data class Client(
    val id: Int,
    val name: String,
    val email: String?,
    val address: Address?,
    val extraData: Any?
)

val clientList = listOf(
    Client(
        id = 1,
        name = "Олена",
        email = "olena@example.com",
        address = Address(
            "вул. Саксаганського, 10",
            "Київ",
            "01033"
        ),
        extraData = "VIP-клієнт"
    ),

    Client(
        id = 2,
        name = "Богдан",
        email = null,
        address = Address(
            "вул. Городоцька, 45",
            "Львів",
            null
        ),
        extraData = 42
    ),

    Client(
        id = 3,
        name = "Марія",
        email = "maria@example.com",
        address = null,
        extraData = null
    ),

    Client(
        id = 4,
        name = "Дмитро",
        email = null,
        address = Address(
            "вул. Соборна, 1",
            null,
            null
        ),
        extraData = "Очікує дзвінка"
    )
)


// Завдання 1
fun getShippingLabel(client: Client): String {
    val address = client.address
        ?: return "Самовивіз: Клієнт ${client.name} не надав адреси"

    val city = address.city ?: "Місто не вказано"
    val postalCode = address.postalCode ?: "Індекс невідомий"

    return "${address.street}, $city, $postalCode"
}


// Завдання 2
fun printClientNote(client: Client) {
    val note = client.extraData as? String
        ?: "Додаткові примітки відсутні"

    println("Клієнт ${client.name} -> Примітка: $note")
}


// Завдання 4
fun getClientEmailOrThrow(client: Client): String {
    return requireNotNull(client.email) {
        "Клієнт з ID ${client.id} не має електронної пошти!"
    }
}


// Завдання 5
fun forceGetPostalCode(client: Client): String {
    return client.address!!.postalCode!!
}


fun main() {

    println("Завдання 1")

    println("Адреса Олена: ${getShippingLabel(clientList[0])}")
    println("Адреса Богдан: ${getShippingLabel(clientList[1])}")
    println("Адреса Марія: ${getShippingLabel(clientList[2])}")


    println()
    println("Завдання 2")

    clientList.forEach {
        printClientNote(it)
    }


    println()
    println("Завдання 3")

    val emails: List<String?> = clientList.map {
        it.email
    }

    val validEmails: List<String> = emails.filterNotNull()

    val shortestEmailLength = validEmails
        .minByOrNull { it.length }
        ?.length
        ?: 0

    println("База email для розсилки: $validEmails")
    println("Довжина найкоротшого email: $shortestEmailLength")


    println()
    println("Завдання 4")

    try {
        println(getClientEmailOrThrow(clientList[1]))
    } catch (e: IllegalArgumentException) {
        println("Перехоплено виняток: ${e.message}")
    }


    println()
    println("Завдання 5")

    println(
        "Індекс клієнта Олена: ${forceGetPostalCode(clientList[0])}"
    )

    try {
        println(
            "Індекс клієнта Богдан: ${forceGetPostalCode(clientList[1])}"
        )
    } catch (e: NullPointerException) {
        println("Перехоплено очікуваний NPE: ${e.message}")
    }

    // !! припустимо використовувати лише тоді, коли ми точно знаємо,
    // що значення не може бути null, хоча система типів Kotlin цього не знає.
}