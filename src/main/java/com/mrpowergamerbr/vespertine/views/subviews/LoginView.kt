package com.mrpowergamerbr.vespertine.views.subviews

import com.mrpowergamerbr.aminoreapi.AminoClient
import com.mrpowergamerbr.vespertine.Vespertine
import com.mrpowergamerbr.vespertine.evaluate
import org.jooby.Request
import org.jooby.Response
import java.util.*

class LoginView : AbstractView() {
	override fun handleRender(req: Request, res: Response): Boolean {
		return req.path() == "/login"
	}

	override fun render(req: Request, res: Response): String {
		if (!req.param("username").isSet || !req.param("password").isSet) {
			return evaluate("login.html")
		}

		val username = req.param("username").value()
		val password = req.param("password").value()

		val amino = AminoClient(username, password, Vespertine.deviceId)

		val variables = mutableMapOf<String, Any>()
		try {
			amino.login()
			val randomUuid = UUID.randomUUID()
			req.session().set("aminoUuid", randomUuid.toString())
			Vespertine.aminoClients[randomUuid.toString()] = amino
			res.redirect(Vespertine.WEBSITE_URL)
		} catch (e: Exception) {
			e.printStackTrace()
			variables["vaspertineErrorMessage"] = e.localizedMessage
		}

		return evaluate("login.html", variables)
	}
}