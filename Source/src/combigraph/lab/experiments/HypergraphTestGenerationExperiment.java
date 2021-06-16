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

import combigraph.lab.GraphLab;
import combigraph.lab.TimeoutCommandRunner;
import combigraph.lab.problems.CombinatorialTestingProblem;

public class HypergraphTestGenerationExperiment extends TestGenerationExperiment
{
	/**
	 * Name of this particular tool
	 */
	public static final transient String NAME = "Hypergraph";
	
	public HypergraphTestGenerationExperiment(CombinatorialTestingProblem problem)
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
		TimeoutCommandRunner runner = new TimeoutCommandRunner(new String[] {"java", "-jar", "hitting-set-0.9.0-standalone.jar", m_problem.getFilenameFor(NAME)});
		runner.setTimeout(getMaxDuration());
		runner.run();
		return runner.getString();
	}
	
	@Override
	protected int getSize(String tool_output)
	{
		if (tool_output.startsWith("#"))
		{
			// Hitting set output
			return tool_output.length() - tool_output.replaceAll(" ", "").length() + 1;
		}
		String[] lines = tool_output.split("\r\n|\r|\n");
		return  lines.length;
	}
}
