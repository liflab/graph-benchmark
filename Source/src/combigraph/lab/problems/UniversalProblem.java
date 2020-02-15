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
import java.util.List;

import ca.uqac.lif.labpal.ExperimentException;
import ca.uqac.lif.labpal.Random;
import combigraph.lab.experiments.ActsTestGenerationExperiment;
import combigraph.lab.experiments.HypergraphTestGenerationExperiment;

/**
 * Combinatorial "t-way" problem, to which universal constraints are added.
 */
public abstract class UniversalProblem extends ConstrainedProblem 
{
	/**
	 * Creates a new generic instance of a universal t-way problem
	 * @param random A random number generator
	 * @param t Interaction strength
	 * @param v Domain size
	 * @param n Number of parameters
	 */
	public UniversalProblem(Random random, int t, int v, int n)
	{
		super(random, t, v, n);
	}
	
	@Override
	public boolean supportedBy(String tool_name)
	{
		if (tool_name.compareTo(HypergraphTestGenerationExperiment.NAME) == 0)
		{
			// Only hypergraph supports existential constraints
			return false;
		}
		return super.supportedBy(tool_name);
	}
	
	@Override
	public void generateFor(String tool_name, PrintStream ps) throws ExperimentException, IOException
	{
		switch (tool_name)
		{
		case ActsTestGenerationExperiment.NAME:
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
			generateActsConstraintString(ps);
			break;
		default:
			super.generateFor(tool_name, ps);
			break;
		}
	}
	
	/**
	 * Generates the "without" command line parameters for Jenny
	 * @return The list of forbidden tuples
	 */
	public abstract List<String> generateJennyWithoutParams();
}
