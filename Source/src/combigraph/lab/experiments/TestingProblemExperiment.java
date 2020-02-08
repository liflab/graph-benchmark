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

import ca.uqac.lif.labpal.Experiment;
import combigraph.lab.problems.CombinatorialTestingProblem;

public abstract class TestingProblemExperiment extends Experiment
{
	/**
	 * The folder where the input files are generated
	 */
	public static final transient String s_folder = "data/";
	
	/**
	 * The testing problem associated to this experiment
	 */
	protected CombinatorialTestingProblem m_problem;
	
	/**
	 * Creates a new test generation experiment
	 * @param problem The testing problem associated to this experiment
	 */
	public TestingProblemExperiment(CombinatorialTestingProblem problem)
	{
		super();
		m_problem = problem;
		problem.fillExperiment(this);
	}
	
	/**
	 * Gets the testing problem associated to this experiment
	 * @return The problem
	 */
	public CombinatorialTestingProblem getProblem()
	{
		return m_problem;
	}
}
