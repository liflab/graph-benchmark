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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.labpal.Experiment;
import ca.uqac.lif.labpal.Random;
import combigraph.lab.experiments.ActsTestGenerationExperiment;
import combigraph.lab.experiments.ColoringTestGenerationExperiment;
import combigraph.lab.experiments.HypergraphTestGenerationExperiment;
import combigraph.lab.experiments.JennyTestGenerationExperiment;
import combigraph.lab.experiments.TcasesTestGenerationExperiment;
import combigraph.lab.experiments.TestingProblemExperiment;

public class IncreasingValues extends UniversalProblem
{
	/**
	 * The name of this problem
	 */
	public static final transient String NAME = "Increasing values";

	/**
	 * Creates a new instance of the t-way test case generation with 
	 * forbidden tuples
	 * @param random A random number generator
	 * @param t Interaction strength
	 * @param v Domain size
	 * @param n Number of parameters
	 */
	public IncreasingValues(Random random, int t, int v, int n) 
	{
		super(random, t, v, n);
	}

	@Override
	public void fillExperiment(Experiment e)
	{
		super.fillExperiment(e);
	}

	@Override
	public boolean supportedBy(String tool_name)
	{
		if (tool_name.compareTo(HypergraphTestGenerationExperiment.NAME) == 0 || 
				tool_name.compareTo(JennyTestGenerationExperiment.NAME) == 0 ||
				tool_name.compareTo(ActsTestGenerationExperiment.NAME) == 0)
		{
			// Only ACTS, hypergraph and Jenny support this problem
			return true;
		}
		return false;
	}

	@Override
	public String getName()
	{
		return NAME;
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
		if (tool_name.compareTo(TcasesTestGenerationExperiment.NAME) == 0)
		{
			extension = ".tcases";
		}
		return TestingProblemExperiment.s_folder + tool_name + "-increasing-" + m_t + "-" + m_v + "-" + m_n + extension;
	}

	@Override
	protected void generateQictConstraintString(PrintStream ps)
	{
		ps.print("Always ");
		for (int n_i = 0; n_i < m_n - 1; n_i++)
		{
			String p1 = "p" + n_i;
			String p2 = "p" + (n_i + 1);
			if (n_i > 0)
			{
				ps.print(" && ");
			}
			ps.print(p1 + " <= " + p2);
		}
		ps.println();
	}

	@Override
	protected void generateActsConstraintString(PrintStream ps)
	{
		ps.println();
		ps.println("[Constraints]");
		for (int n_i = 0; n_i < m_n - 1; n_i++)
		{
			String p1 = "p" + (n_i + 1);
			String p2 = "p" + (n_i + 2);
			if (n_i > 0)
			{
				ps.print(" && ");
			}
			ps.print(p1 + " <= " + p2);
		}
		ps.println();
	}

	@Override
	public List<String> generateJennyWithoutParams()
	{
		List<String> list = new ArrayList<String>();
		int[] values = new int[m_n];
		for (int n_i = 0; n_i < m_n; n_i++)
		{
			values[n_i] = 0;
		}
		if (!isValid(values))
		{
			list.add(writeJennyParameter(values));
		}
		while (nextForbidden(values))
		{
			list.add(writeJennyParameter(values));
		}
		return list;
	}
	
	protected boolean nextForbidden(int[] values)
	{
		while (increment(values))
		{
			if (!isValid(values))
			{
				return true;
			}
		}
		return false;
	}
	
	protected boolean isValid(int[] values)
	{
		for (int i = 0; i < m_n - 1; i++)
		{
			if (values[i] > values[i+1])
			{
				return false;
			}
		}
		return true;
	}
	
	
}
