package nl.hu.inno.dashboard.exception

import org.slf4j.LoggerFactory

inline fun <reified T : Any> loggerFor() = LoggerFactory.getLogger(T::class.java)

class GlobalExceptionHandler(
    private val defaultHandler: Thread.UncaughtExceptionHandler? = null
    ) : Thread.UncaughtExceptionHandler {

        private val Log = loggerFor<GlobalExceptionHandler>()

        override fun uncaughtException(thread: Thread, exception: Throwable) {
            Log.error("Uncaught exception in thread ${thread.name}", exception)


            defaultHandler?.uncaughtException(thread, exception)
        }
    }