package org.db_poultry.errors

/**
 * Generates an error message. Follow the format
 * Error at `<the code block>` in `<fileName>`
 *
 * @param message the message of the error.
 * @param description the description of the error (why did it happen?)
 * @param solution (optional) provides the most basic troubleshooting solution
 * @param exceltion (optional) if the error generates an exception, include it
 *
 *
 * @author zrygan
 */
fun generateErrorMessage(
    message: String,
    description: String,
    solution: String = "",
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
