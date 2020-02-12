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
import java.io.PrintStream;

import ca.uqac.lif.labpal.ExperimentException;
import ca.uqac.lif.testing.tway.FileHelper;
import combigraph.lab.problems.CombinatorialTestingProblem;

public abstract class TestGenerationExperiment extends TestingProblemExperiment
{
	/**
	 * Name of parameter "tool name"
	 */
	public static final transient String TOOL_NAME = "Tool";

	/**
	 * Name of parameter "duration"
	 */
	public static final transient String DURATION = "Duration";

	/**
	 * Name of parameter "test suite size"
	 */
	public static final transient String SIZE = "Size";

	public TestGenerationExperiment(CombinatorialTestingProblem problem, String tool_name)
	{
		super(problem);
		describe(TOOL_NAME, "The name of the tool used to generate the test suite");
		describe(DURATION, "The duration of the test generation, in milliseconds");
		describe(SIZE, "The number of test cases in the generated test suite");
		setInput(TOOL_NAME, tool_name);
	}

	@Override
	public void execute() throws ExperimentException, InterruptedException
	{
		long time_start = System.currentTimeMillis();
		try
		{
			String tool_output = runTool();
			long time_end = System.currentTimeMillis();
			if (tool_output == null || tool_output.isEmpty())
			{
				throw new ExperimentException("The tool did not produce any output");
			}
			write(DURATION, time_end - time_start);
			write(SIZE, getSize(tool_output));
		}
		catch (IOException e)
		{
			throw new ExperimentException(e);
		}
	}

	@Override
	public boolean prerequisitesFulfilled()
	{
		return FileHelper.fileExists(m_problem.getFilenameFor(readString(TOOL_NAME)));
	}

	@Override
	public void fulfillPrerequisites() throws ExperimentException
	{
		String tool_name = readString(TOOL_NAME);
		String input_filename = m_problem.getFilenameFor(tool_name);
		try
		{
			PrintStream ps = new PrintStream(new File(input_filename));
			m_problem.generateFor(tool_name, ps);
			ps.flush();
			ps.close();
		} 
		catch (IOException e) 
		{
			throw new ExperimentException(e);
		}
	}

	/**
	 * Runs the tool on the associated problem
	 * @return The output of the tool at the standard output
	 * @throws IOException If something "goes bad" when running the tool
	 */
	protected abstract String runTool() throws IOException;

	/**
	 * Gets the number of test cases from the tool's output
	 * @param tool_output The output of the tool at the standard output
	 */
	protected abstract int getSize(String tool_output);
}
