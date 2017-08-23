package com.mrpowergamerbr.vespertine

import com.mitchellbosecke.pebble.PebbleEngine
import com.mitchellbosecke.pebble.loader.FileLoader
import com.mrpowergamerbr.aminoreapi.Amino
import com.mrpowergamerbr.aminoreapi.AminoClient
import com.mrpowergamerbr.aminoreapi.entities.AminoBlogPost
import com.mrpowergamerbr.vespertine.Vespertine.Companion.WEBSITE_URL
import com.mrpowergamerbr.vespertine.views.GlobalHandler
import org.jooby.Kooby
import java.io.File
import java.io.StringWriter

class Vespertine : Kooby({
	assets("/**", File(FOLDER, "static/").toPath())
	get("/**", { req, res ->
		res.send(GlobalHandler.render(req, res))
	})
	post("/**", { req, res ->
		res.send(GlobalHandler.render(req, res))
	})
	port(4858)
}) {
	companion object {
		lateinit var engine: PebbleEngine
		val FOLDER = "/home/servers/vespertine/"
		val WEBSITE_URL = "https://vespertine.mrpowergamerbr.com/"
		var deviceId: String = "changemeplz"
		val aminoClients = mutableMapOf<String, AminoClient>()
	}

	init {
		Amino.DEBUG = true
		// Start Pebble Template Engine
		val fl = FileLoader()
		fl.prefix = FOLDER + "content/"
		engine = PebbleEngine.Builder().cacheActive(false).strictVariables(true).loader(fl).build()

		deviceId = File("deviceId.txt").readText()
	}
}

inline fun evaluate(file: String, variables: MutableMap<String, Any> = mutableMapOf<String, Any>()): String {
	variables["websiteUrl"] = WEBSITE_URL
	val writer = StringWriter()
	Vespertine.engine.getTemplate("$file").evaluate(writer, variables)
	return writer.toString()
}

fun AminoBlogPost.fromAminoToHtml(): String {
	var content = this.content

	if (content == null)
		return ""

	content = content.replace("\n", "</br>")

	if (this.mediaList != null) {
		for (media in this.mediaList) {
			if (media is List<*>) {
				if (media.size == 4) { // Imagens
					val unknown = media[0] // ?
					val url = media[1] as String // image URL
					val unknown2 = media[2] // ?
					val id = media[3] as String // media ID

					// [IMG=6T1]
					content = content.replace("[IMG=$id]", "<img src=\"$url\"></img>")
				}
			}
		}
	}

	return content
}