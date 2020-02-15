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
import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.labpal.Experiment;
import ca.uqac.lif.labpal.ExperimentException;
import ca.uqac.lif.labpal.Random;
import combigraph.lab.experiments.ActsTestGenerationExperiment;
import combigraph.lab.experiments.ColoringTestGenerationExperiment;
import combigraph.lab.experiments.HypergraphTestGenerationExperiment;
import combigraph.lab.experiments.JennyTestGenerationExperiment;
import combigraph.lab.experiments.TestingProblemExperiment;

public class TestSuiteCompletion extends ExistentialProblem
{
	/**
	 * Name of parameter "number of tests"
	 */
	public static final transient String NUM_TESTS = "Number of existing tests";

	/**
	 * The name of this problem
	 */
	public static final transient String NAME = "Test suite completion";

	/**
	 * The number of tests already present in the test suite
	 */
	protected int m_numTests;
	
	/**
	 * The set of pre-existing tests generated for this problem
	 */
	protected transient List<int[]> m_tests;
	
	/**
	 * Creates a new instance of the t-way test case generation with 
	 * forbidden tuples
	 * @param random A random number generator
	 * @param t Interaction strength
	 * @param v Domain size
	 * @param n Number of parameters
	 * @param num_tests Number of tests already present in the test suite
	 */
	public TestSuiteCompletion(Random random, int t, int v, int n, int num_tests)
	{
		super(random, t, v, n);
		m_numTests = num_tests;
		m_tests = null;
	}
	
	@Override
	public boolean supportedBy(String tool_name)
	{
		if (tool_name.compareTo(HypergraphTestGenerationExperiment.NAME) == 0 ||
				tool_name.compareTo(ColoringTestGenerationExperiment.NAME) == 0 ||
				tool_name.compareTo(JennyTestGenerationExperiment.NAME) == 0 ||
				tool_name.compareTo(ActsTestGenerationExperiment.NAME) == 0
				)
		{
			// Only ACTS, hypergraph, coloring and Jenny support test suite completion
			return true;
		}
		return false;
	}
	
	@Override
	public String getFilenameFor(String tool_name)
	{
		String extension = ".txt";
		if (tool_name.compareTo(ColoringTestGenerationExperiment.NAME) == 0)
		{
			extension = ".dot";
		}
		if (tool_name.compareTo(HypergraphTestGenerationExperiment.NAME) == 0)
		{
			extension = ".edn";
		}
		return TestingProblemExperiment.s_folder + tool_name + "-completion-" + m_t + "-" + m_v + "-" + m_n + "-" + m_numTests + "-" + extension;
	}

	@Override
	public String getName()
	{
		return NAME;
	}
	
	@Override
	public void fillExperiment(Experiment e)
	{
		super.fillExperiment(e);
		e.describe(NUM_TESTS, "Number of tests already present in the test suite");
		e.setInput(NUM_TESTS, m_numTests);
	}
	
	@Override
	public void generateFor(String tool_name, PrintStream ps) throws IOException, ExperimentException
	{
		if (m_tests == null)
		{
			m_tests = generateExistingTests();
		}
		switch (tool_name)
		{
		default:
			super.generateFor(tool_name, ps);
			break;
		}
	}
	
	/**
	 * Creates a list of a predefined number of randomly-generated test cases
	 * @param random The random number generator used to generate the tests.
	 * Method {@link Random#reseed() reseed()} of the RNG is called before
	 * generating the tests, so that every call to this method generates the
	 * same list. 
	 * @return The list of test cases
	 */
	protected List<int[]> generateExistingTests()
	{
		m_random.reseed();
		List<int[]> out_list = new ArrayList<int[]>(m_numTests);
		for (int n_t = 0; n_t < m_numTests; n_t++)
		{
			int[] test = new int[m_n];
			for (int n_i = 0; n_i < m_n; n_i++)
			{
				test[n_i] = m_random.nextInt(m_v);
			}
			out_list.add(test);
		}
		return out_list;
	}

	@Override
	protected void generateQictConstraintString(PrintStream ps)
	{
		ps.println();
		for (int[] test : m_tests)
		{
			ps.print("Once ");
			for (int i = 0; i < test.length; i++)
			{
				if (i > 0)
				{
					ps.print(" && ");
				}
				ps.print("p" + i + " = " + test[i]);
			}
		}
	}
	
	@Override
	protected void generateActsConstraintString(PrintStream ps)
	{
		ps.println();
		ps.println("[Test Set]");
		for (int i = 0; i < m_n; i++)
		{
			if (i > 0)
			{
				ps.print(",");
			}
			ps.print("p" + (i + 1));
		}
		ps.println();
		for (int[] test : m_tests)
		{
			for (int i = 0; i < test.length; i++)
			{
				if (i > 0)
				{
					ps.print(",");
				}
				ps.print(test[i] + 1);
			}
			ps.println();
		}
	}
}
