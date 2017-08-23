package com.mrpowergamerbr.vespertine.views.subviews

import com.mrpowergamerbr.vespertine.Vespertine
import com.mrpowergamerbr.vespertine.evaluate
import com.mrpowergamerbr.vespertine.fromAminoToHtml
import kotlinx.html.br
import kotlinx.html.div
import kotlinx.html.hr
import kotlinx.html.id
import kotlinx.html.img
import kotlinx.html.li
import kotlinx.html.span
import kotlinx.html.stream.appendHTML
import kotlinx.html.style
import kotlinx.html.ul
import kotlinx.html.unsafe
import org.jooby.Request
import org.jooby.Response
import java.io.StringWriter
import java.util.regex.Pattern

class CommunityView : AbstractView() {
	override fun handleRender(req: Request, res: Response): Boolean {
		val matcher = Pattern.compile("\\/community\\/([0-9]+)").matcher(req.path())
		matcher.find()
		return matcher.matches()
	}

	override fun render(req: Request, res: Response): String {
		val matcher = Pattern.compile("\\/community\\/([0-9]+)").matcher(req.path())
		matcher.find()
		val communityId = matcher.group(1)

		val amino = Vespertine.aminoClients[req.session().get("aminoUuid").value()]!!

		val community = amino.getCommunityById("x$communityId")

		var image: String = community.icon
		if (community.promotionalMediaList != null) {
			search@ for (obj in community.promotionalMediaList) {
				println(community.name + " - " + obj)
				if (obj is List<*>) {
					for (x in obj) {
						if (x is String) {
							if (x.endsWith("png") || x.endsWith("jpg") || x.endsWith("gif")) {
								image = x
								break@search
							}
						}
					}
				}
			}
		}

		val writer = StringWriter().appendHTML()
				.div {
					id = "communityView"
					div("wrapper") {
						div("communityBackground") {
							style = "position: relative; background: linear-gradient(#f000, #0000004d), url(${image}) no-repeat center center; background-size: cover; height: 50vh;"
							img(src = community.icon, classes = "pageIcon")
						}

						div("sidebar") {
							ul {
								style = "sidebarList"
								for (entry in community.configuration.page.customList) {
									li {
										text(entry.alias ?: entry.originalTitle)
									}
								}
							}
							br {
								style = "clear: both"
							}
						}
						div("postView") {
							for (post in community.getBlogFeed(0, 15)) {
								div("post") {
									img(src = post.author.icon) {
										style = "border-radius: 999999px; width: 64px; height: 64px;"
									}
									span("postAuthor") {
										text(post.author.nickname)
									}
									div {}
									unsafe {
										raw(post.fromAminoToHtml())
									}
									div {}
									unsafe {
										raw("<i style=\"font-size: 2em\" class=\"fa fa-heart-o\" aria-hidden=\"true\"></i> ${post.votesCount}")
									}
									hr {}
								}
							}
							br {
								style = "clear: both"
							}
						}
						br {
							style = "clear: both"
						}
					}
				}
		val variables = mutableMapOf<String, Any>("content" to writer.toString())
		variables["subtitle"] = community.name
		return evaluate("index.html", variables)
	}
}