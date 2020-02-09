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
package combigraph.lab.experiments;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.labpal.CommandRunner;
import combigraph.lab.problems.CombinatorialTestingProblem;
import combigraph.lab.problems.TWayProblem;

/**
 * Test generation experiment that uses
 * <a href="https://burtleburtle.net/bob/math/jenny.html">Jenny</a>
 * to generate test suites.
 */
public class JennyTestGenerationExperiment extends TestGenerationExperiment
{
	/**
	 * Name of this particular tool
	 */
	public static final transient String NAME = "Jenny";

	/**
	 * Name of the Jenny executable
	 */
	public static final transient String JENNY = "jenny";

	/**
	 * An array mapping positions to feature names for Jenny's "without"
	 * command line arguments
	 */
	public static final transient String[] FEATURES = new String[] {"a", "b", "c", "d", "e", "f", 
			"g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", 
			"u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", 
			"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", 
			"W", "X", "Y", "Z"};

	public JennyTestGenerationExperiment(CombinatorialTestingProblem problem)
	{
		super(problem, NAME);
	}

	@Override
	protected String runTool() 
	{
		List<String> syntax = new ArrayList<String>();
		syntax.add(JENNY);
		TWayProblem twp = (TWayProblem) m_problem;
		syntax.add("-n" + twp.getT());
		String v = twp.getV() + "";
		for (int i = 0; i < twp.getN(); i++)
		{
			syntax.add(v);
		}
		syntax.addAll(getAdditionalParameters());
		CommandRunner runner = new CommandRunner(toStringArray(syntax));
		runner.run();
		return runner.getString();
	}

	@Override
	protected int getSize(String tool_output)
	{
		String[] lines = tool_output.split("\r\n|\r|\n");
		return  lines.length;
	}

	/**
	 * Returns a list of additional command line parameters for Jenny
	 * @return The list of parameters
	 */
	protected List<String> getAdditionalParameters()
	{
		// No additional parameter by default
		return new ArrayList<String>(0);
	}

	@Override
	public boolean prerequisitesFulfilled()
	{
		// For the basic t-way problem, Jenny does not require any input file
		return true;
	}

	/**
	 * Converts a list of strings into an array of strings
	 * @param list The list
	 * @return The array
	 */
	protected static String[] toStringArray(List<String> list)
	{
		String[] out = new String[list.size()];
		for (int i = 0; i < out.length; i++)
		{
			out[i] = list.get(i);
		}
		return out;
	}
}
