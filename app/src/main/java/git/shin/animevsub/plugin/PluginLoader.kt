package git.shin.animevsub.plugin

import android.content.Context
import git.shin.animevsub.data.remote.api.AnimeDataSource
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.security.MessageDigest

class PluginLoader(private val context: Context) {

    private val pluginDir: File = File(context.filesDir, "plugins").apply { mkdirs() }
    private val loadedPlugins = mutableMapOf<String, URLClassLoader>()

    fun loadPlugin(pluginInfo: PluginInfo): Result<AnimeDataSource> {
        return try {
            val jarFile = File(pluginDir, "${pluginInfo.id}.jar")
            if (!jarFile.exists()) {
                return Result.failure(Exception("Plugin JAR not found"))
            }

            val parentLoader = AnimeDataSource::class.java.classLoader
            val classLoader = URLClassLoader(
                arrayOf(jarFile.toURI().toURL()),
                parentLoader
            )

            loadedPlugins[pluginInfo.id] = classLoader

            val dataSourceClass = classLoader.loadClass(pluginInfo.className)
            val instance = dataSourceClass.getDeclaredConstructor().newInstance() as AnimeDataSource

            Result.success(instance)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun unloadPlugin(pluginId: String) {
        loadedPlugins.remove(pluginId)?.close()
    }

    fun installPlugin(jarBytes: ByteArray, pluginInfo: PluginInfo): Result<File> {
        return try {
            val jarFile = File(pluginDir, "${pluginInfo.id}.jar")
            jarFile.writeBytes(jarBytes)

            val checksum = calculateSha256(jarBytes)
            if (pluginInfo.checksum.isNotEmpty() && checksum != pluginInfo.checksum) {
                jarFile.delete()
                return Result.failure(Exception("Checksum verification failed"))
            }

            Result.success(jarFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun uninstallPlugin(pluginId: String): Result<Unit> {
        return try {
            unloadPlugin(pluginId)
            val jarFile = File(pluginDir, "$pluginId.jar")
            if (jarFile.exists()) {
                jarFile.delete()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getInstalledPlugins(): List<File> {
        return pluginDir.listFiles()?.filter { it.extension == "jar" } ?: emptyList()
    }

    private fun calculateSha256(data: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(data)
        return hash.joinToString("") { "%02x".format(it) }
    }

    fun close() {
        loadedPlugins.values.forEach { it.close() }
        loadedPlugins.clear()
    }
}