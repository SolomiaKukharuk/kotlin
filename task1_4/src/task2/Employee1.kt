package task2

class Employee1(
    val firstName: String,
    val lastName: String,
    val position: String
)

fun main() {
    val emp1 = Employee1("Ivan", "Petrenko", "Developer")
    val emp2 = Employee1("Ivan", "Petrenko", "Developer")
    val emp3 = emp1

    println(emp1)

    println("emp1 === emp2: ${emp1 === emp2}")
    println("emp1 == emp2: ${emp1 == emp2}")

    println("emp1 === emp3: ${emp1 === emp3}")
    println("emp1 == emp3: ${emp1 == emp3}")
}