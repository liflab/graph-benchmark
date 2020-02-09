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
package combigraph.lab;

import ca.uqac.lif.json.JsonNumber;
import ca.uqac.lif.labpal.ExperimentFactory;
import ca.uqac.lif.labpal.Region;
import combigraph.lab.experiments.AllPairsTestGenerationExperiment;
import combigraph.lab.experiments.ColoringTestGenerationExperiment;
import combigraph.lab.experiments.HypergraphTestGenerationExperiment;
import combigraph.lab.experiments.JennyForbiddenTuplesExperiment;
import combigraph.lab.experiments.JennyTestGenerationExperiment;
import combigraph.lab.experiments.TestGenerationExperiment;
import combigraph.lab.problems.CombinatorialTestingProblem;
import combigraph.lab.problems.ForbiddenTuples;
import combigraph.lab.problems.TWayProblem;

import static combigraph.lab.experiments.TestGenerationExperiment.TOOL_NAME;
import static combigraph.lab.problems.CombinatorialTestingProblem.TESTING_PROBLEM_NAME;
import static combigraph.lab.problems.TWayProblem.N;
import static combigraph.lab.problems.TWayProblem.T;
import static combigraph.lab.problems.TWayProblem.V;

public class TestGenerationExperimentFactory extends ExperimentFactory<GraphLab,TestGenerationExperiment>
{
	public TestGenerationExperimentFactory(GraphLab lab)
	{
		super(lab, TestGenerationExperiment.class);
	}

	@Override
	protected TestGenerationExperiment createExperiment(Region r)
	{
		String problem_s = r.getString(TESTING_PROBLEM_NAME);
		CombinatorialTestingProblem problem = getProblem(problem_s, r);
		if (problem == null)
		{
			return null;
		}
		String tool_s = r.getString(TOOL_NAME);
		if (!problem.supportedBy(tool_s))
		{
			return null;
		}
		switch (tool_s)
		{
		case JennyTestGenerationExperiment.NAME:
		{
			if (problem instanceof ForbiddenTuples)
			{
				return new JennyForbiddenTuplesExperiment((ForbiddenTuples) problem);
			}
			return new JennyTestGenerationExperiment(problem);
		}
		case AllPairsTestGenerationExperiment.NAME:
			return new AllPairsTestGenerationExperiment(problem);
		case ColoringTestGenerationExperiment.NAME:
			return new ColoringTestGenerationExperiment(problem);
		case HypergraphTestGenerationExperiment.NAME:
			return new HypergraphTestGenerationExperiment(problem);
		}
		return null;
	}

	protected static CombinatorialTestingProblem getProblem(String prob_name, Region r)
	{
		switch (prob_name)
		{
		case TWayProblem.NAME:
			return new TWayProblem(r.getInt(T), r.getInt(V), r.getInt(N));
		case ForbiddenTuples.NAME:
			return new ForbiddenTuples(r.getInt(T), r.getInt(V), r.getInt(N),
					((JsonNumber) r.get(ForbiddenTuples.FRACTION_VARS)).numberValue().floatValue(),
					((JsonNumber) r.get(ForbiddenTuples.FRACTION_VALUES)).numberValue().floatValue());
		default:
			return null;
		}
	}
}
