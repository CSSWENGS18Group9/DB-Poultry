package org.db_poultry.Util

import javafx.util.Callback
import java.lang.reflect.Constructor

/**
 * Manager for controller instances to ensure single instance usage across FXML loads
 */
object ControllerManager {
    // Map to store controller instances by class
    private val controllerInstances = mutableMapOf<Class<*>, Any>()
    
    /**
     * Returns a controller factory that produces/reuses controller instances
     */
    val controllerFactory = Callback<Class<*>, Any> { controllerClass ->
        // Return existing instance if available
        controllerInstances[controllerClass]?.let { return@Callback it }
        
        // Otherwise, create a new instance
        try {
            val constructor: Constructor<*> = controllerClass.getDeclaredConstructor()
            val controller = constructor.newInstance()
            controllerInstances[controllerClass] = controller
            controller
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Failed to create controller: ${controllerClass.name}", e)
        }
    }
    
    /**
     * Manually register a controller instance
     */
    fun <T : Any> registerController(controller: T) {
        controllerInstances[controller::class.java] = controller
    }
    
    /**
     * Get a registered controller instance
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getController(controllerClass: Class<T>): T? {
        return controllerInstances[controllerClass] as? T
    }
    
    /**
     * Clear a specific controller instance
     */
    fun clearController(controllerClass: Class<*>) {
        controllerInstances.remove(controllerClass)
    }
    
    /**
     * Clear all controller instances
     */
    fun clearAll() {
        controllerInstances.clear()
    }
}