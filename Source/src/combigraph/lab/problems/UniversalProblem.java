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

import combigraph.lab.experiments.HypergraphTestGenerationExperiment;

/**
 * Combinatorial "t-way" problem, to which universal constraints are added.
 */
public abstract class UniversalProblem extends ConstrainedProblem 
{
	/**
	 * Creates a new generic instance of a universal t-way problem
	 * @param t Interaction strength
	 * @param v Domain size
	 * @param n Number of parameters
	 */
	public UniversalProblem(int t, int v, int n)
	{
		super(t, v, n);
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
}
