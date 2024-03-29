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

import ca.uqac.lif.labpal.CommandRunner;
import ca.uqac.lif.labpal.ExperimentException;
import ca.uqac.lif.labpal.Random;
import combigraph.lab.experiments.ActsTestGenerationExperiment;
import combigraph.lab.experiments.ColoringTestGenerationExperiment;
import combigraph.lab.experiments.HypergraphTestGenerationExperiment;
import combigraph.lab.experiments.JennyTestGenerationExperiment;

/**
 * Classical "t-way" problem, to which extra constraints are added
 */
public abstract class ConstrainedProblem extends TWayProblem 
{
	/**
	 * Creates a new generic instance of a constrained t-way problem
	 * @param random A random number generator
	 * @param t Interaction strength
	 * @param v Domain size
	 * @param n Number of parameters
	 */
	public ConstrainedProblem(Random random, int t, int v, int n)
	{
		super(random, t, v, n);
	}

	@Override
	public void generateFor(String tool_name, PrintStream ps) throws ExperimentException, IOException
	{
		switch (tool_name)
		{
		case ColoringTestGenerationExperiment.NAME:
		{
			generateGraph(ps, false);
			break;
		}
		case HypergraphTestGenerationExperiment.NAME:
		{
			generateGraph(ps, true);
			break;
		}
		case ActsTestGenerationExperiment.NAME:
		{
			super.generateFor(ActsTestGenerationExperiment.NAME, ps);
			generateActsConstraintString(ps);
			break;
		}
		default:
			// Nothing to do: unsupported
			break;
		}
	}

	protected void generateGraph(PrintStream ps, boolean hypergraph) throws ExperimentException, IOException
	{
		File temp = File.createTempFile("temp-input-FOO", ".ncond");
		PrintStream in_ps = new PrintStream(temp);
		printQictDomains(in_ps);
		generateQictConstraintString(in_ps);
		in_ps.close();
		String[] command = {"php", "variables-to-graph.php", 
				Integer.toString(m_t), temp.getAbsolutePath()};
		if (hypergraph)
		{
			command =  new String[] {"php", "variables-to-hypergraph.php", "-t", 
					Integer.toString(m_t), "--edn", temp.getAbsolutePath()};
		}
		CommandRunner runner = new CommandRunner(command);
		runner.run();
		String s_graph_contents = runner.getString();
		if (s_graph_contents == null || s_graph_contents.trim().isEmpty())
		{
			throw new ExperimentException("Error when generating the graph");
		}
		ps.print(s_graph_contents);
		temp.delete();
	}

	/**
	 * Prints the domains for each parameter using the QICT file syntax
	 * @param ps The print stream where to print these domains
	 */
	protected void printQictDomains(PrintStream ps)
	{
		for (int n_i = 0; n_i < m_n; n_i++)
		{
			ps.print("p" + n_i + ": ");
			for (int v_i = 0; v_i < m_v; v_i++)
			{
				if (v_i > 0)
				{
					ps.print(", ");
				}
				ps.print(v_i);
			}
			ps.println();
		}
	}

	/**
	 * Prints the set of constraints for this problem using the extended
	 * QICT file syntax format
	 * @param ps The print stream where to print these constraints
	 */
	protected abstract void generateQictConstraintString(PrintStream ps);
	
	/**
	 * Prints the set of constraints for this problem using the extended
	 * ACTS file syntax format
	 * @param ps The print stream where to print these constraints
	 */
	protected abstract void generateActsConstraintString(PrintStream ps);
	
	protected boolean increment(int[] values)
	{
		for (int i = 0; i < m_n; i++)
		{
			values[i]++;
			if (values[i] < m_v)
			{
				return true;
			}
			else
			{
				values[i] = 0;
			}
		}
		return false;
	}
	
	protected String writeJennyParameter(int[] values)
	{
		String w = "-w";
		for (int n_i = 0; n_i < values.length; n_i++)
		{
			w += (n_i + 1) + JennyTestGenerationExperiment.FEATURES[values[n_i]];
		}
		return w;
	}
}
