package org.openspim.app;

import ij.IJ;
import ij.io.LogStream;

/**
 * Encapsulate ImageJ 1.x calls.
 *
 * @author Johannes Schindelin
 */
class Helper {

	public static void redirectStdoutAndStderr() {
		System.setOut(new LogStream());
		System.setErr(new LogStream());
	}

	public static void log(final String msg) {
		IJ.log( msg );
	}
}
