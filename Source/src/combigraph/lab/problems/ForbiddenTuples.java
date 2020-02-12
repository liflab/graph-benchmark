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

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.labpal.Experiment;
import ca.uqac.lif.labpal.ExperimentException;
import combigraph.lab.experiments.ActsTestGenerationExperiment;
import combigraph.lab.experiments.ColoringTestGenerationExperiment;
import combigraph.lab.experiments.HypergraphTestGenerationExperiment;
import combigraph.lab.experiments.JennyTestGenerationExperiment;
import combigraph.lab.experiments.TcasesTestGenerationExperiment;
import combigraph.lab.experiments.TestingProblemExperiment;

public class ForbiddenTuples extends UniversalProblem
{
	public static final transient String FRACTION_VARS = "fp";

	public static final transient String FRACTION_VALUES = "fv";

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
				tool_name.compareTo(JennyTestGenerationExperiment.NAME) == 0 ||
				tool_name.compareTo(ActsTestGenerationExperiment.NAME) == 0 ||
				tool_name.compareTo(TcasesTestGenerationExperiment.NAME) == 0)
		{
			// Only ACTS, hypergraph, Tcases and Jenny support forbidden tuples
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

	@Override
	public void generateFor(String tool_name, PrintStream ps) throws ExperimentException, IOException
	{
		switch (tool_name)
		{
		case TcasesTestGenerationExperiment.NAME:
			generateForTcases(ps);
			break;
		case ActsTestGenerationExperiment.NAME:
			generateForActs(ps);
			break;
		default:
			// Defer to superclass for all but those above
			super.generateFor(tool_name, ps);
			break;
		}
	}

	protected void generateForTcases(PrintStream ps) throws IOException
	{
		ps.println("<System name=\"foo\">");
		ps.println(" <Function name=\"test\">");
		ps.println("  <Input>");
		int highest_p = (int) (m_fractionVars * m_n) + 1;
		int highest_v = (int) (m_fractionValues * m_v) + 1;
		for (int n_i = 1; n_i <= m_n; n_i++)
		{
			ps.println("   <Var name=\"p" + n_i + "\">");
			for (int v_i = 1; v_i <= m_v; v_i++)
			{
				ps.print("     <Value name=\"" + v_i + "\" ");
				if (n_i < highest_p && v_i < highest_v)
				{
					String prop_name = "p" + n_i + "v" + v_i;
					ps.print("property=\"" + prop_name + "\" ");
				}
				if (n_i > 1 && n_i <= highest_p && v_i == 1)
				{
					ps.print("whenNot=\"");
					for (int vv_i = 1; vv_i < highest_v; vv_i++)
					{
						if (vv_i > 1)
						{
							ps.print(",");
						}
						String other_prop_name = "p" + (n_i - 1) + "v" + vv_i;
						ps.print(other_prop_name);
					}
					ps.print("\" ");
				}
				ps.println("/>");
			}
			ps.println("   </Var>");
		}
		ps.println("  </Input>");
		ps.println(" </Function>");
		ps.println("</System>");
		PrintStream ps_gen = new PrintStream(new File(TestingProblemExperiment.s_folder + "Tcases-t-" + m_t + ".xml"));
		ps_gen.println("<Generators>");
		ps_gen.println(" <TupleGenerator tuples=\"" + m_t + "\" />");
		ps_gen.println("</Generators>");
		ps_gen.close();
	}

	protected void generateForActs(PrintStream ps)
	{
		ps.println("[System]");
		ps.println("Name: foo");
		ps.println();
		ps.println("[Parameter]");
		for (int n_i = 1; n_i <= m_n; n_i++)
		{
			ps.print("p" + n_i + " (int): ");
			for (int v_i = 1; v_i <= m_v; v_i++)
			{
				if (v_i > 1)
				{
					ps.print(",");
				}
				ps.print(v_i);
			}
			ps.println();
		}
		ps.println();
		ps.println("[Constraints]");
		for (int n_i = 0; n_i < m_fractionVars * m_n; n_i++)
		{
			String p1 = "p" + (n_i + 1);
			String p2 = "p" + (n_i + 2);
			for (int v_i = 1; v_i < (m_fractionValues * m_v) + 1; v_i++)
			{
				ps.println(p1 + " != " + v_i + " || " + p2  + " != 1");
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
