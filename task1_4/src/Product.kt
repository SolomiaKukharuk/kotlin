data class Product(
    val id: Int,
    val name: String,
    val category: String,
    val price: Double,
    val rating: Double,
    val inStock: Boolean
)

val products = listOf(
    Product(1, "Google Pixel 9", "Smartphones", 899.0, 4.8, true),
    Product(2, "iPhone 16 Pro", "Smartphones", 1199.0, 4.9, true),
    Product(3, "Galaxy A55", "Smartphones", 399.0, 4.3, false),
    Product(4, "MacBook Air M3", "Laptops", 1299.0, 4.9, true),
    Product(5, "ThinkPad X1 Carbon", "Laptops", 1499.0, 4.6, false),
    Product(6, "Dell XPS 13", "Laptops", 1150.0, 4.4, true),
    Product(7, "Sony WH-1000XM5", "Audio", 349.0, 4.7, true),
    Product(8, "AirPods Pro 2", "Audio", 249.0, 4.8, false),
    Product(9, "Pixel Buds Pro 2", "Audio", 229.0, 4.5, true)
)

fun List<Product>.filterAndTransform(
    predicate: (Product) -> Boolean,
    transform: (Product) -> String
): List<String> {
    return this
        .filter(predicate)
        .map(transform)
}

fun main() {

    // Частина 1
    products
        .filter {
            it.inStock &&
                    it.rating >= 4.7 &&
                    it.price < 1000.0
        }
        .sortedByDescending { it.rating }
        .map {
            "Назва: ${it.name} | Рейтинг: ${it.rating} | Ціна: $${it.price}"
        }
        .forEach {
            println(it)
        }

    println()


    // Частина 2

    // 1. Перший ноутбук дорожчий за 1200
    val laptop = products.find {
        it.category == "Laptops" && it.price > 1200.0
    }

    println(laptop?.name ?: "Товар не знайдено")


    // 2. Чи є аудіопристрій дорожчий за 300
    val hasExpensiveAudio = products.any {
        it.category == "Audio" && it.price > 300.0
    }

    println(hasExpensiveAudio)


    // 3. Чи всі смартфони мають рейтинг > 4.0
    val allSmartphonesGood = products
        .filter { it.category == "Smartphones" }
        .all { it.rating > 4.0 }

    println(allSmartphonesGood)

    println()


    // Частина 3

    // 1. Розділення на товари в наявності і не в наявності
    val (available, outOfStock) = products.partition {
        it.inStock
    }

    println("В наявності: ${available.size}")
    println("Немає в наявності: ${outOfStock.size}")


    // 2. Групування за категоріями
    val groupedProducts: Map<String, List<Product>> =
        products.groupBy { it.category }


    // 3. Найдорожчий товар у кожній категорії
    groupedProducts.forEach { (category, productList) ->

        val mostExpensive = productList.maxByOrNull {
            it.price
        }

        if (mostExpensive != null) {
            println(
                "Категорія $category -> Найдорожчий: " +
                        "${mostExpensive.name} ($${mostExpensive.price})"
            )
        }
    }

    println()


    // Частина 4

    val promoProducts = products.filterAndTransform(
        { product -> product.price < 300.0 }
    ) { product ->
        "Акційна ціна на ${product.name}: лише $${product.price}!"
    }

    promoProducts.forEach {
        println(it)
    }
}