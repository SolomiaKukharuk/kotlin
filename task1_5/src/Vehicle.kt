data class Vehicle(
    var brand: String = "",
    var model: String = "",
    var year: Int = 0,
    var licensePlate: String = ""
)

fun main() {

    // Завдання 1: apply + also
    val vehicle = Vehicle()
        .apply {
            brand = "Honda"
            model = "Civic"
            year = 2020
            licensePlate = "КА3344ВІ"
        }
        .also {
            println("Створено новий транспортний засіб: $it")
        }

    println()

    // Завдання 2: with
    with(vehicle) {
        println("Марка = $brand")
        println("Модель = $model")
        println("Рік випуску: $year")
        println("Номерний знак: $licensePlate")
    }
}