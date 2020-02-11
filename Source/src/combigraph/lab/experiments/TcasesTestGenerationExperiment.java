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

import ca.uqac.lif.mtnp.util.CommandRunner;
import combigraph.lab.GraphLab;
import combigraph.lab.problems.CombinatorialTestingProblem;
import combigraph.lab.problems.TWayProblem;

public class TcasesTestGenerationExperiment extends TestGenerationExperiment
{
	/**
	 * Name of this particular tool
	 */
	public static final transient String NAME = "Tcases";
	
	/**
	 * The regex pattern to look for in Tcases' output to count test cases
	 */
	protected static final transient Pattern s_pattern = Pattern.compile("<TestCase");
	
	public TcasesTestGenerationExperiment(CombinatorialTestingProblem problem)
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
		int t = ((TWayProblem) m_problem).getT();
		String t_filename = TestGenerationExperiment.s_folder + "Tcases-t-" + t + ".xml";
		String[] command = {"java", "-jar", "tcases.jar", "-g", t_filename, "-t", m_problem.getFilenameFor(NAME)};
		CommandRunner runner = new CommandRunner(command);
		runner.run();
		return runner.getString();
	}
	
	@Override
	protected int getSize(String tool_output)
	{
		Matcher mat = s_pattern.matcher(tool_output);
		int size = 0;
		while (mat.find())
		{
			size++;
		}
		return size;
	}

}
