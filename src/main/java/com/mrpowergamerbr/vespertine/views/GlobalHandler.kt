package com.mrpowergamerbr.vespertine.views

import com.mrpowergamerbr.vespertine.Vespertine
import com.mrpowergamerbr.vespertine.evaluate
import com.mrpowergamerbr.vespertine.views.subviews.AbstractView
import com.mrpowergamerbr.vespertine.views.subviews.CommunityView
import com.mrpowergamerbr.vespertine.views.subviews.LoginView
import kotlinx.html.ATarget
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.html
import kotlinx.html.img
import kotlinx.html.input
import kotlinx.html.p
import kotlinx.html.stream.appendHTML
import kotlinx.html.style
import kotlinx.html.unsafe
import org.jooby.Request
import org.jooby.Response
import java.io.StringWriter

object GlobalHandler {
	fun render(req: Request, res: Response): String {
		val views = getViews()

		for (view in views) {
			if (view.handleRender(req, res)) {
				return view.render(req, res)
			}
		}

		if (req.path() == "/bye") {
			req.session().destroy()
		}
		if (req.session().isSet("aminoUuid")) {
			val amino = Vespertine.aminoClients[req.session().get("aminoUuid").value()]!!

			val writer = StringWriter().appendHTML().div("pure-g") {
				for (community in amino.getJoinedCommunities(0, 50).communityList) {
					div("pure-u-1 pure-u-md-1-6") {
						div("communityWrapper") {
							div("communityEntry") {
								img(classes = "communityIcon", src = community.icon)
								div("communityBackground") {
									var image: String = community.icon
									if (community.launchPage != null && community.launchPage.mediaList != null) {
										search@ for (obj in community.launchPage.mediaList) {
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

									style = "background: linear-gradient(#f000, #0000004d), url(${image}); background-size: cover;"
									div("communityBar") {
										div("communityText") {
											text(community.name)
										}
										div {
											style = "padding: 7px;"

											unsafe {
												raw("<a href=\"${Vespertine.WEBSITE_URL}community/${community.ndcId}\" <button style=\"width: 100%\" class=\"checkInButton pure-button\"><i class=\"fa fa-external-link-square\" aria-hidden=\"true\"></i> Check In</button></a>")
											}
										}
									}
								}
							}
						}
					}
				}
			}

			for (community in amino.getJoinedCommunities(0, 50).communityList) {
				if (community.launchPage != null && community.launchPage.mediaList != null) {
					println(community.name + " - " + community.launchPage.mediaList.joinToString(separator = " | "))
				}
			}

			val variables = mutableMapOf<String, Any>("content" to writer.toString())
			variables["subtitle"] = "Meus Aminos"
			return evaluate("index.html", variables)
		} else {
			res.redirect(Vespertine.WEBSITE_URL + "login")
			return "Not Logged In!"
		}
	}

	fun getViews(): List<AbstractView> {
		val views = mutableListOf<AbstractView>()

		views.add(LoginView())
		views.add(CommunityView())
		return views
	}
}