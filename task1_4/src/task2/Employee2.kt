package task2

data class Employee2(
    val firstName: String,
    val lastName: String,
    val position: String
) {
    var bonus: Int = 0
}

fun main() {
    val emp1 = Employee2("Ivan", "Petrenko", "Developer")
    val emp2 = Employee2("Ivan", "Petrenko", "Developer")
    val emp3 = emp1

    println(emp1)

    println("emp1 === emp2: ${emp1 === emp2}")
    println("emp1 == emp2: ${emp1 == emp2}")

    println("emp1 === emp3: ${emp1 === emp3}")
    println("emp1 == emp3: ${emp1 == emp3}")

    val (name, surname, pos) = emp1

    println(name)
    println(surname)
    println(pos)

    val promoted = emp1.copy(position = "Senior Developer")

    println(promoted)

    emp1.bonus = 1000
    emp2.bonus = 5000

    println("emp1 bonus: ${emp1.bonus}")
    println("emp2 bonus: ${emp2.bonus}")

    println("emp1 == emp2: ${emp1 == emp2}")
}