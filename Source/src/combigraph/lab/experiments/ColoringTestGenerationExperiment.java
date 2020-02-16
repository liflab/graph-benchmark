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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.uqac.lif.labpal.CommandRunner;
import ca.uqac.lif.labpal.ExperimentException;
import combigraph.lab.GraphLab;
import combigraph.lab.problems.CombinatorialTestingProblem;

public class ColoringTestGenerationExperiment extends TestGenerationExperiment
{
	/**
	 * Name of this particular tool
	 */
	public static final transient String NAME = "Coloring";

	/**
	 * The command used to launch the coloring program (DSATUR)
	 */
	protected static final transient String DSATUR_COMMAND = "./dsatur";

	/**
	 * The pattern to look for in the tool's output
	 */
	protected static final transient Pattern s_sizePattern = Pattern.compile("permutation is (\\d+)");
	
	public ColoringTestGenerationExperiment(CombinatorialTestingProblem problem)
	{
		super(problem, NAME);
	}

	@Override
	protected String runTool() 
	{
		if (GraphLab.s_dryRun)
		{
			return "";
		}
		String[] command = new String[] {"./dsatur", m_problem.getFilenameFor(NAME)};
		CommandRunner runner = new CommandRunner(command);
		runner.run();
		return runner.getString();
	}

	@Override
	protected int getSize(String tool_output) throws ExperimentException
	{
		Matcher mat = s_sizePattern.matcher(tool_output);
		if (mat.find())
		{
			return Integer.parseInt(mat.group(1));
		}
		throw new ExperimentException("No solution from the tool");
	}
}
