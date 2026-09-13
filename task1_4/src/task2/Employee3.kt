package task2

class Employee3(
    val firstName: String,
    val lastName: String,
    val position: String
) {
    var bonus: Int = 0

    override fun toString(): String {
        return "Employee(firstName=$firstName, lastName=$lastName, position=$position)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other !is Employee3) return false

        return firstName == other.firstName &&
                lastName == other.lastName &&
                position == other.position
    }

    override fun hashCode(): Int {
        var result = firstName.hashCode()

        result = 31 * result + lastName.hashCode()
        result = 31 * result + position.hashCode()

        return result
    }

    operator fun component1(): String {
        return firstName
    }

    operator fun component2(): String {
        return lastName
    }

    operator fun component3(): String {
        return position
    }

    fun copy(
        firstName: String = this.firstName,
        lastName: String = this.lastName,
        position: String = this.position
    ): Employee3 {
        return Employee3(firstName, lastName, position)
    }
}

fun main() {
    val emp1 = Employee3("Ivan", "Petrenko", "Developer")
    val emp2 = Employee3("Ivan", "Petrenko", "Developer")
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