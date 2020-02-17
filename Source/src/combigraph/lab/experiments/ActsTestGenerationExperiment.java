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

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.uqac.lif.labpal.CommandRunner;
import combigraph.lab.GraphLab;
import combigraph.lab.problems.CombinatorialTestingProblem;
import combigraph.lab.problems.TWayProblem;

public class ActsTestGenerationExperiment extends TestGenerationExperiment
{
	/**
	 * Name of this particular tool
	 */
	public static final transient String NAME = "IPOG";
	
	/**
	 * Name of the JAR file for ACTS
	 */
	protected static final transient String s_actsJarName = "acts_3.1.jar";
	
	/**
	 * The regex pattern to fetch the size from the tool's output
	 */
	protected static final transient Pattern s_pattern = Pattern.compile("Number of Tests\\s*:\\s*(\\d+)");
	
	public ActsTestGenerationExperiment(CombinatorialTestingProblem problem)
	{
		super(problem, NAME);
	}
	
	@Override
	protected String runTool() throws IOException
	{
		if (GraphLab.s_dryRun)
		{
			return "";
		}
		File temp = File.createTempFile("temp-input-FOO", ".ncond");
		int t = ((TWayProblem) m_problem).getT();
		String[] command = {"java", "-Dmode=extend", "-Doutput=numeric", "-Ddoi=" + t, "-jar", s_actsJarName, m_problem.getFilenameFor(NAME), temp.getAbsolutePath()};
		CommandRunner runner = new CommandRunner(command);
		runner.run();
		temp.delete();
		return runner.getString();
	}
	
	@Override
	protected int getSize(String tool_output)
	{
		Matcher mat = s_pattern.matcher(tool_output);
		if (mat.find())
		{
			return Integer.parseInt(mat.group(1).trim());
		}
		return 0;
	}

}
