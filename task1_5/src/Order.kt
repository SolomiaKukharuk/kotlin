data class OrderItem(
    val name: String,
    val price: Double,
    val quantity: Int
)

class Order(val id: String) {
    var customerName: String = ""
    var deliveryAddress: String = ""
    var discountPercent: Int = 0
    val items: MutableList<OrderItem> = mutableListOf()

    fun addItem(item: OrderItem) {
        items.add(item)
    }
}

fun main() {

    // Завдання 1: apply + also
    val order = Order("ORD-2026-001")
        .apply {
            customerName = "Олексій Коваленко"
            deliveryAddress = "вул. Хрещатик, 22, Київ"
            discountPercent = 10

            addItem(OrderItem("Клавіатура", 2500.0, 1))
            addItem(OrderItem("Мишка", 1200.0, 1))
            addItem(OrderItem("Килимок", 400.0, 2))
        }
        .also {
            println(
                "[LOG] Замовлення ${it.id} успішно налаштовано. " +
                        "Кількість позицій: ${it.items.size}"
            )
        }


    // Завдання 2: run
    val finalPrice: Double = order.run {
        val basePrice = items.sumOf {
            it.price * it.quantity
        }

        basePrice * (1 - discountPercent / 100.0)
    }

    println("Розрахована сума: $finalPrice грн")


    // Завдання 3: let
    val paymentPayload = finalPrice.let {
        val amountInCents = (it * 100).toLong()

        "TXN-${order.id}_AMOUNT_${amountInCents}_UAH"
    }

    println("Транзакційний payload: $paymentPayload")


    // Завдання 4: with
    with(order) {
        println("========================================")
        println("ЧЕК ЗАМОВЛЕННЯ: $id")
        println("Отримувач: $customerName")
        println("Адреса доставки: $deliveryAddress")
        println("----------------------------------------")
        println("Товари:")

        items.forEach {
            println(
                " • ${it.name} (${it.quantity} шт.) - " +
                        "${it.price * it.quantity} грн"
            )
        }

        println("----------------------------------------")
        println("Знижка клієнта: $discountPercent%")
        println("Разом до сплати: $finalPrice грн")
        println("========================================")
    }
}