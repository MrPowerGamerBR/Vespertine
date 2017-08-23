package com.mrpowergamerbr.vespertine

object VespertineLauncher {
	@JvmStatic
	fun main(vararg args: String) {
		org.jooby.run(::Vespertine, *args)
	}
}