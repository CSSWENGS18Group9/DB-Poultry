package org.db_poultry.errors

fun generateErrorMessage(
    message: String,
    description: String,
    solution: String,
    exception: Throwable? = null

) {
    print("\n============================== DB_POULTRY ERROR \n")
    println(message)
    println(description)
    println("Solution: ${solution.ifEmpty { "No solution provided." }}")
    exception?.let {
        println("\nException Stack Trace:")
        it.printStackTrace()
    }
    print("\n======================== END OF DB_POULTRY ERROR\n")
}
