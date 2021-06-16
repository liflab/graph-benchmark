/*
    A lab for comparing combinatorial test suite generators
    Copyright (C) 2017-2021 Sylvain Hallé, Edmond La Chance,
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import combigraph.lab.GraphLab;
import combigraph.lab.TimeoutCommandRunner;
import combigraph.lab.problems.CombinatorialTestingProblem;

/**
 * Test generation experiment that uses
 * <a href="https://cse.unl.edu/~citportal">CASA</a>
 * to generate test suites.
 */
public class CasaTestGenerationExperiment extends TestGenerationExperiment
{
	/**
	 * Name of this particular tool
	 */
	public static final transient String NAME = "CASA";

	/**
	 * Name of the Jenny executable
	 */
	public static final transient String CASA = "casa-1.1b";

	public CasaTestGenerationExperiment(CombinatorialTestingProblem problem)
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
		File temp_out = File.createTempFile("temp-CASA-", ".out");
		String[] command = {CASA, "--output", temp_out.getAbsolutePath(), m_problem.getFilenameFor(NAME)};
		TimeoutCommandRunner runner = new TimeoutCommandRunner(command);
		runner.setTimeout(getMaxDuration());
		runner.run();
		Scanner scanner = new Scanner(temp_out);
		String line = scanner.nextLine();
		scanner.close();
		temp_out.delete();
		return line;
	}

	@Override
	protected int getSize(String tool_output)
	{
		return Integer.parseInt(tool_output.trim());
	}
	
	/**
	 * Writes a t-way problem instance into an input file for CASA
	 * @param ps The print stream to write to
	 * @param t The value of t
	 * @param n The value of n
	 * @param v The value of v
	 * @throws FileNotFoundException Thrown if writing to the file fails
	 */
	public static void writeSpecFile(PrintStream ps, int t, int n, int v)
	{
		ps.println(t);
		ps.println(n);
		for (int i = 0; i < n; i++)
		{
			if (i > 0)
			{
				ps.print(" ");
			}
			ps.print(v);
		}
		ps.println();
	}
}
