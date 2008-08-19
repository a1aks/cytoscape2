/*
  File: Exec.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

// Exec.java

// exec a child process, and get its stdout & stderr
package cytoscape.util;

import cytoscape.logger.CyLogger;

import java.io.*;
import java.io.IOException;
import java.io.InputStreamReader;

import java.lang.Process;
import java.lang.Runtime;
import java.lang.Runtime;

import java.util.Enumeration;
import java.util.Vector;


/**
 *
 */
public class Exec {
	String[] cmd;
	Vector stdoutResults;
	Vector stderrResults;
	String stringToSendToStandardInput;
	boolean runInBackground = false;
	String stdout;
	String stderr;
	static CyLogger logger = CyLogger.getLogger(Exec.class);

	/**
	 * Creates a new Exec object.
	 */
	public Exec() {
		this(null);
	}

	/**
	 * Creates a new Exec object.
	 *
	 * @param cmd  DOCUMENT ME!
	 */
	public Exec(String[] cmd) {
		this.cmd = cmd;
		stdoutResults = new Vector(100); // just guessing...
		stderrResults = new Vector(10);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param input DOCUMENT ME!
	 */
	public void setStandardInput(String input) {
		stringToSendToStandardInput = input;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param newValue DOCUMENT ME!
	 */
	public void setRunInBackground(boolean newValue) {
		runInBackground = newValue;

		int length = cmd.length;
		String[] revisedCmd = new String[length + 1];

		for (int i = 0; i < length; i++)
			revisedCmd[i] = cmd[i];

		revisedCmd[length] = " &";

		cmd = revisedCmd;
	} // setRunInBackground

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getCmd() {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < cmd.length; i++) {
			sb.append(cmd[i]);
			sb.append(" ");
		}

		return sb.toString();
	} // getCmd

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int run() {
		int execExitValue = -1; // be pessimistic

		StringBuffer cmdSB = new StringBuffer();

		for (int i = 0; i < cmd.length; i++) {
			cmdSB.append(cmd[i]);
			cmdSB.append(" ");
		}

		try {
			Runtime runtime = Runtime.getRuntime();

			// logger.info (" --> just before exec: \n\t" + getCmd ());
			//Process process = runtime.exec (cmd);
			Process process = runtime.exec(cmdSB.toString());
			BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(process
			                                                                                                                                           .getInputStream()));
			BufferedReader stderrReader = new BufferedReader(new InputStreamReader(process
			                                                                                                                                             .getErrorStream()));

			if (stringToSendToStandardInput != null) {
				// A PrintStream adds functionality to another output stream, namely the
				// ability to print representations of various data values
				// conveniently. Two other features are provided as well. Unlike other
				// output streams, a PrintStream never throws an IOException; instead,
				// exceptional situations merely set an internal flag that can be tested
				// via the checkError method. Optionally, a PrintStream can be created so
				// as to flush automatically; this means that the flush method is
				// automatically invoked after a byte array is written, one of
				// the println methods is invoked, or a newline character or
				// byte ('\n') is written.
				PrintStream stdinWriter = new PrintStream(process.getOutputStream(), true);
				stdinWriter.print(stringToSendToStandardInput);
				stdinWriter.close();
			}

			try {
				execExitValue = process.waitFor();
			} catch (InterruptedException e) {
				logger.warn("Interrupted waiting for child process: "+e.getMessage());
			}

			String stdoutResult;

			while ((stdoutResult = stdoutReader.readLine()) != null) {
				stdoutResults.addElement(stdoutResult);
			}

			String stderrResult;

			while ((stderrResult = stderrReader.readLine()) != null) {
				stderrResults.addElement(stderrResult);
			}
		} // try
		catch (IOException e) {
			logger.error("I/O error while communicating with child process: "+e.getMessage());
		}

		return execExitValue;
	} // run

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public int runThreaded() throws Exception {
		int execExitValue = -1; // be pessimistic

		StringBuffer cmdSB = new StringBuffer();

		for (int i = 0; i < cmd.length; i++) {
			cmdSB.append(cmd[i]);
			cmdSB.append(" ");
		}

		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(cmdSB.toString());

		final BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(process
		                                                                                                                                                                                                                 .getInputStream()));
		final BufferedReader stderrReader = new BufferedReader(new InputStreamReader(process
		                                                                                                                                                                                                                   .getErrorStream()));
		final StringBuffer stdoutSB = new StringBuffer();
		final StringBuffer stderrSB = new StringBuffer();

		Thread stdoutReadingThread = new Thread() {
			public void run() {
				String s;

				try {
					while ((s = stdoutReader.readLine()) != null) {
						stdoutSB.append(s + "\n");
					} // while
				} // trey
				catch (Exception exc0) {
					logger.error("--- error: ", exc0);
				} // catch
			}
			; // run
		}; // thread

		Thread stderrReadingThread = new Thread() {
			public void run() {
				String s;

				try {
					while ((s = stderrReader.readLine()) != null) {
						stderrSB.append(s + "\n");
					} // while
				} // try
				catch (Exception exc1) {
					logger.info("--- error: ", exc1);
				} // catch
			}
			; // run
		}; // thread

		stdoutReadingThread.start();
		stderrReadingThread.start();
		execExitValue = process.waitFor();

		stdout = stdoutSB.toString();
		stderr = stderrSB.toString();

		return execExitValue;
	} // runThreaded

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Vector getStdout() {
		return stdoutResults;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Vector getStderr() {
		return stderrResults;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getStdoutAsString() {
		return stdout;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getStderrAsString() {
		return stderr;
	}
} // Exec.java
