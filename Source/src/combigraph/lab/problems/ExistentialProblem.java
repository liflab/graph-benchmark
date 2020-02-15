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

import ca.uqac.lif.labpal.Random;
import combigraph.lab.experiments.HypergraphTestGenerationExperiment;
import combigraph.lab.experiments.TestingProblemExperiment;

/**
 * Combinatorial "t-way" problem, to which existential constraints are added.
 */
public abstract class ExistentialProblem extends ConstrainedProblem 
{
	/**
	 * Creates a new generic instance of an existential t-way problem
	 * @param random A random number generator
	 * @param t Interaction strength
	 * @param v Domain size
	 * @param n Number of parameters
	 */
	public ExistentialProblem(Random random, int t, int v, int n)
	{
		super(random, t, v, n);
	}
	
	@Override
	public boolean supportedBy(String tool_name)
	{
		if (tool_name.compareTo(HypergraphTestGenerationExperiment.NAME) != 0)
		{
			// Only hypergraph supports existential constraints
			return false;
		}
		return super.supportedBy(tool_name);
	}
	
	public String getJennySeedFilename()
	{
		return TestingProblemExperiment.s_folder + "Jenny-completion-" + m_t + "-" + m_v + "-" + m_n + "-seed.txt";
	}
	
	public void writeJennySeedFile(PrintStream ps)
	{
		// Do nothing
	}
}
