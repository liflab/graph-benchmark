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
import ca.uqac.lif.testing.tway.DotGraphGenerator;
import ca.uqac.lif.testing.tway.EdnGenerator;
import combigraph.lab.experiments.AllPairsTestGenerationExperiment;
import combigraph.lab.experiments.ColoringTestGenerationExperiment;
import combigraph.lab.experiments.HypergraphTestGenerationExperiment;
import combigraph.lab.experiments.TcasesTestGenerationExperiment;
import combigraph.lab.experiments.TestingProblemExperiment;
import combigraph.lab.experiments.VPTagTestGenerationExperiment;

/**
 * Classical combinatorial test generation problem.
 */
public class TWayProblem extends CombinatorialTestingProblem
{
	/**
	 * Parameter name for interaction strength
	 */
	public static final transient String T = "t";

	/**
	 * Parameter name for domain size
	 */
	public static final transient String V = "v";

	/**
	 * Parameter name for number of parameters
	 */
	public static final transient String N = "n";

	/**
	 * The name of this problem
	 */
	public static final transient String NAME = "t-way test generation";

	/**
	 * Interaction strength
	 */
	protected int m_t;

	/**
	 * Domain size
	 */
	protected int m_v;

	/**
	 * Number of parameters
	 */
	protected int m_n;

	/**
	 * Creates a new instance of the t-way problem
	 * @param t Interaction strength
	 * @param v Domain size
	 * @param n Number of parameters
	 */
	public TWayProblem(int t, int v, int n)
	{
		super();
		m_t = t;
		m_v = v;
		m_n = n;
	}

	@Override
	public boolean supportedBy(String tool_name)
	{
		if (m_t > 2 && tool_name.compareTo(AllPairsTestGenerationExperiment.NAME) == 0)
		{
			// AllPairs only supports t=2
			return false;
		}
		return true;
	}

	@Override
	public void generateFor(String tool_name, PrintStream ps) throws ExperimentException, IOException
	{
		switch (tool_name)
		{
		case ColoringTestGenerationExperiment.NAME:
		{
			List<String> p_names = new ArrayList<String>(m_n);
			for (int n_i = 0; n_i < m_n; n_i++)
			{
				p_names.add("p" + n_i);
			}
			DotGraphGenerator g_gen = new DotGraphGenerator(m_t, p_names);
			g_gen.setOutput(ps);
			List<String> domain = new ArrayList<String>(m_v);
			for (int v_i = 0; v_i < m_v; v_i++)
			{
				domain.add(Integer.toString(v_i));
			}
			for (int n_i = 0; n_i < m_n; n_i++)
			{
				g_gen.addDomain("p" + n_i, domain);
			}
			g_gen.generateTWayEdges();
			break;
		}
		case HypergraphTestGenerationExperiment.NAME:
		{
			List<String> p_names = new ArrayList<String>(m_n);
			for (int n_i = 0; n_i < m_n; n_i++)
			{
				p_names.add("p" + n_i);
			}
			EdnGenerator h_gen = new EdnGenerator(m_t, p_names);
			h_gen.setOutput(ps);
			List<String> domain = new ArrayList<String>(m_v);
			for (int v_i = 0; v_i < m_v; v_i++)
			{
				domain.add(Integer.toString(v_i));
			}
			for (int n_i = 0; n_i < m_n; n_i++)
			{
				h_gen.addDomain("p" + n_i, domain);
			}
			h_gen.generateTWayEdges();
			break;
		}
		case TcasesTestGenerationExperiment.NAME:
		{
			ps.println("<System name=\"foo\">");
			ps.println(" <Function name=\"test\">");
			ps.println("  <Input>");
			for (int n_i = 1; n_i <= m_n; n_i++)
			{
				ps.println("   <Var name=\"p" + n_i + "\">");
				for (int v_i = 1; v_i <= m_v; v_i++)
				{
					ps.println("     <Value name=\"" + v_i + "\" />");
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
			break;
		}
		case VPTagTestGenerationExperiment.NAME:
		{
			ps.println("<FAM VERSION='Configuration File V1.2'>");
			for (int n_i = 1; n_i <= m_n; n_i++)
			{
				ps.println("<FACTOR>     '" + n_i + "'");
				for (int v_i = 1; v_i <= m_v; v_i++)
				{
					ps.println("    <VALUE>     '" + v_i + "'");
					ps.println("    </VALUE>");
				}
				ps.println("</FACTOR>");
			}
			break;
		}
		case AllPairsTestGenerationExperiment.NAME:
		{
			break;
		}
		}
	}

	@Override
	public String getCommandLineFor(String tool_name)
	{
		// TODO Auto-generated method stub
		return null;
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
		switch (tool_name)
		{
		case ColoringTestGenerationExperiment.NAME:
			extension = ".dot";
			break;
		case HypergraphTestGenerationExperiment.NAME:
			extension = ".edn";
			break;
		case VPTagTestGenerationExperiment.NAME:
			extension = ".fam";
			break;
		case TcasesTestGenerationExperiment.NAME:
			extension = ".xml";
			break;
		}
		
		return TestingProblemExperiment.s_folder + tool_name + "-comb-" + m_t + "-" + m_v + "-" + m_n + extension;
	}

	@Override
	public void fillExperiment(Experiment e)
	{
		super.fillExperiment(e);
		e.describe(T, "Interaction strength");
		e.describe(V, "Domain size");
		e.setInput(N, "Number of parameters");
		e.setInput(T, m_t);
		e.setInput(V, m_v);
		e.setInput(N, m_n);
	}
	
	/**
	 * Gets the interaction strength of the problem
	 * @return The value
	 */
	public int getT()
	{
		return m_t;
	}
	
	/**
	 * Gets the domain size of the problem
	 * @return The value
	 */
	public int getV()
	{
		return m_v;
	}
	
	/**
	 * Gets the number of parameters of the problem
	 * @return The value
	 */
	public int getN()
	{
		return m_n;
	}
}
