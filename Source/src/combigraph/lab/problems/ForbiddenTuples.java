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
import combigraph.lab.experiments.ColoringTestGenerationExperiment;
import combigraph.lab.experiments.HypergraphTestGenerationExperiment;
import combigraph.lab.experiments.JennyTestGenerationExperiment;
import combigraph.lab.experiments.TestingProblemExperiment;

public class ForbiddenTuples extends UniversalProblem
{
	public static final transient String FRACTION_VARS = "Fraction of parameters";

	public static final transient String FRACTION_VALUES = "Fraction of values";

	/**
	 * The name of this problem
	 */
	public static final transient String NAME = "Forbidden tuples";

	/**
	 * The fraction of parameters having forbidden tuples
	 */
	protected float m_fractionVars;

	/**
	 * The fraction of values for each parameter having a forbidden relationship
	 */
	protected float m_fractionValues;

	/**
	 * Creates a new instance of the t-way test case generation with 
	 * forbidden tuples
	 * @param t Interaction strength
	 * @param v Domain size
	 * @param n Number of parameters
	 * @param fraction_vars The fraction of parameters having forbidden tuples
	 * @param fraction_values The fraction of values for each parameter having a 
	 * forbidden relationship
	 */
	public ForbiddenTuples(int t, int v, int n, float fraction_vars, float fraction_values) 
	{
		super(t, v, n);
		m_fractionVars = fraction_vars;
		m_fractionValues = fraction_values;
	}

	@Override
	public void fillExperiment(Experiment e)
	{
		super.fillExperiment(e);
		e.describe(FRACTION_VARS, "The fraction of parameters having forbidden tuples");
		e.describe(FRACTION_VALUES, "The fraction of values for each parameter having a forbidden relationship");
		e.setInput(FRACTION_VARS, m_fractionVars);
		e.setInput(FRACTION_VALUES, m_fractionValues);
	}

	@Override
	public boolean supportedBy(String tool_name)
	{
		if (tool_name.compareTo(HypergraphTestGenerationExperiment.NAME) == 0 || 
				tool_name.compareTo(JennyTestGenerationExperiment.NAME) == 0)
		{
			// Only hypergraph and Jenny support forbidden tuples
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
		return TestingProblemExperiment.s_folder + tool_name + "-forbidden-" + m_t + "-" + m_v + "-" + m_n + "-" + m_fractionVars + "-" + m_fractionValues + extension;
	}

	@Override
	protected void generateQictConstraintString(PrintStream ps)
	{
		for (int n_i = 0; n_i < m_fractionVars * m_n; n_i++)
		{
			String p1 = "p" + n_i;
			String p2 = "p" + (n_i + 1);
			for (int v_i = 0; v_i < m_fractionValues * m_v; v_i++)
			{
				ps.println("Always !(" + p1 + "==" + v_i + " && " + p2  + " == 0)");
			}
		}
	}
	
	public List<String> generateJennyWithoutParams()
	{
		List<String> list = new ArrayList<String>();
		for (int n_i = 1; n_i <= m_fractionVars * m_n; n_i++)
		{
			for (int v_i = 0; v_i < m_fractionValues * m_v; v_i++)
			{
				list.add("-w" + n_i + JennyTestGenerationExperiment.FEATURES[v_i] + (n_i + 1) + JennyTestGenerationExperiment.FEATURES[0]);
			}
		}
		return list;
	}
}
