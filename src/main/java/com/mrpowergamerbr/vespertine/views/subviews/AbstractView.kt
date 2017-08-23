package com.mrpowergamerbr.vespertine.views.subviews

import org.jooby.Request
import org.jooby.Response

abstract class AbstractView {
	abstract fun handleRender(req: Request, res: Response): Boolean

	abstract fun render(req: Request, res: Response): String
}