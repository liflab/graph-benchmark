/*
    A lab for comparing combinatorial test suite generators
    Copyright (C) 2017-2020 Sylvain Hallé, Edmond La Chance,
    Vincent Porta-Scarta

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package combigraph.lab.problems;

import java.io.IOException;
import java.io.PrintStream;

import ca.uqac.lif.labpal.Experiment;
import ca.uqac.lif.labpal.ExperimentException;

public abstract class CombinatorialTestingProblem 
{
	/**
	 * The name of the paramater "testing problem"
	 */
	public static final transient String TESTING_PROBLEM_NAME = "Testing problem";
	
	/**
	 * Determines if a testing problem is supported by a given software 
	 * @param tool_name The name of the tool
	 * @return <tt>true</tt> if the tool supports the problem, <tt>false</tt>
	 * if not
	 */
	public abstract boolean supportedBy(String tool_name);
	
	/**
	 * Generates an input file for a given tool
	 * @param tool_name The name of the tool to generate the file for
	 * @param ps The contents of the file will be written to that
	 * print stream
	 */
	public abstract void generateFor(String tool_name, PrintStream ps) throws ExperimentException, IOException;
	
	/**
	 * Produces a unique filename corresponding to this testing problem,
	 * for a specific tool
	 * @param tool_name  The name of the tool
	 * @return The filename
	 */
	public abstract String getFilenameFor(String tool_name);
	
	/**
	 * Generates the command line to run a given tool
	 * @param tool_name The name of the tool
	 * @return The string containing the command line
	 */
	public abstract String getCommandLineFor(String tool_name);
	
	/**
	 * Gets the name of the testing problem
	 * @return The name of the testing problem
	 */
	public abstract String getName();
	
	/**
	 * Fills the content of an experiment with data about this specific
	 * testing problem
	 * @param e The experiment to fill with
	 */
	public void fillExperiment(Experiment e)
	{
		e.describe(TESTING_PROBLEM_NAME, "The name of the testing problem");
		e.setInput(TESTING_PROBLEM_NAME, getName());
	}
}
