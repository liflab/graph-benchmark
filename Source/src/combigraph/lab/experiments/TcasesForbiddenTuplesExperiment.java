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

import combigraph.lab.GraphLab;
import combigraph.lab.problems.CombinatorialTestingProblem;

public class TcasesForbiddenTuplesExperiment extends TestGenerationExperiment
{
	/**
	 * Name of this particular tool
	 */
	public static final transient String NAME = "Tcases";
	
	public TcasesForbiddenTuplesExperiment(CombinatorialTestingProblem problem)
	{
		super(problem, NAME);
	}
	
	@Override
	protected String runTool() 
	{
		if (GraphLab.s_dryRun)
		{
			return "";
		}
		return null;
	}
	
	@Override
	protected int getSize(String tool_output)
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
