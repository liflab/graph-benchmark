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
import ca.uqac.lif.labpal.FileHelper;
import ca.uqac.lif.labpal.Group;
import ca.uqac.lif.labpal.Laboratory;
import ca.uqac.lif.labpal.Region;
import ca.uqac.lif.labpal.table.ExperimentTable;
import combigraph.lab.experiments.AllPairsTestGenerationExperiment;
import combigraph.lab.experiments.ColoringTestGenerationExperiment;
import combigraph.lab.experiments.HypergraphTestGenerationExperiment;
import combigraph.lab.experiments.TcasesTestGenerationExperiment;
import combigraph.lab.experiments.TestGenerationExperiment;
import combigraph.lab.experiments.TestingProblemExperiment;
import combigraph.lab.experiments.VPTagTestGenerationExperiment;
import combigraph.lab.problems.ForbiddenTuples;
import combigraph.lab.problems.TWayProblem;

import static combigraph.lab.experiments.TestGenerationExperiment.DURATION;
import static combigraph.lab.experiments.TestGenerationExperiment.SIZE;
import static combigraph.lab.experiments.TestGenerationExperiment.TOOL_NAME;
import static combigraph.lab.problems.CombinatorialTestingProblem.TESTING_PROBLEM_NAME;
import static combigraph.lab.problems.TWayProblem.N;
import static combigraph.lab.problems.TWayProblem.T;
import static combigraph.lab.problems.TWayProblem.V;

import java.io.File;

public class GraphLab extends Laboratory
{
	/**
	 * Lower bound for parameter <i>t</i> in the experiments
	 */
	public static int t_min = 2;

	/**
	 * Upper bound for parameter <i>t</i> in the experiments
	 */
	public static int t_max = 5;

	/**
	 * Lower bound for parameter <i>v</i> in the experiments
	 */
	public static int v_min = 2;

	/**
	 * Upper bound for parameter <i>v</i> in the experiments
	 */
	public static int v_max = 4;

	/**
	 * Lower bound for parameter <i>n</i> in the experiments
	 */
	public static int n_min = 2;

	/**
	 * Upper bound for parameter <i>n</i> in the experiments
	 */
	public static int n_max = 20;

	@Override
	public void setup()
	{
		// Setup the lab's factory
		TestGenerationExperimentFactory factory = new TestGenerationExperimentFactory(this);

		// Create the data folder if it does not exist
		File data_folder = new File(TestingProblemExperiment.s_folder);
		data_folder.mkdir();

		// Setup the lab's regions
		TWayRegion big_r = new TWayRegion();
		big_r.addRange(T, t_min, t_max);
		big_r.addRange(V, v_min, v_max);
		big_r.addRange(N, n_min, n_max);
		big_r.add(TOOL_NAME, 
				AllPairsTestGenerationExperiment.NAME,
				TcasesTestGenerationExperiment.NAME,
				VPTagTestGenerationExperiment.NAME,
				ColoringTestGenerationExperiment.NAME, 
				HypergraphTestGenerationExperiment.NAME);

		// Classical t-way problems
		{
			TWayRegion twr = new TWayRegion(big_r);
			twr.add(TESTING_PROBLEM_NAME, TWayProblem.NAME);
			Group g = new Group("Classical t-way problems");
			add(g);
			{
				// Fixing n and v, varying t
				for (Region out_r : twr.all(N, V))
				{
					ExperimentTable et_size = new ExperimentTable(TOOL_NAME, T, SIZE);
					ExperimentTable et_duration = new ExperimentTable(TOOL_NAME, T, DURATION);
					for (Region in_r : out_r.all(T, TOOL_NAME))
					{
						TestGenerationExperiment exp = factory.get(in_r);
						if (exp == null)
						{
							continue;
						}
						et_size.add(exp);
						et_duration.add(exp);
						g.add(exp);
					}
					add(et_size);
					add(et_duration);
				}
			}
		}

		// Forbidden tuples
		{
			TWayRegion twr = new TWayRegion(big_r);
			twr.addRange(N, n_min, n_max / 2);
			twr.add(TESTING_PROBLEM_NAME, ForbiddenTuples.NAME);
			twr.add(ForbiddenTuples.FRACTION_VALUES, 0, 0.1, 0.2, 0.5);
			twr.add(ForbiddenTuples.FRACTION_VARS, 0, 0.1, 0.2, 0.5);
			Group g = new Group("Forbidden tuples");
			add(g);
			{
				// Fixing n and v, and t, varying the fraction
				for (Region out_r : twr.all(N, V, T))
				{
					ExperimentTable et_size = new ExperimentTable(TOOL_NAME, ForbiddenTuples.FRACTION_VALUES, SIZE);
					ExperimentTable et_duration = new ExperimentTable(TOOL_NAME, ForbiddenTuples.FRACTION_VALUES, DURATION);
					for (Region in_r : out_r.all(TOOL_NAME, ForbiddenTuples.FRACTION_VALUES, ForbiddenTuples.FRACTION_VARS))
					{
						float frac_vars = ((JsonNumber) in_r.get(ForbiddenTuples.FRACTION_VARS)).numberValue().floatValue();
						float frac_vals = ((JsonNumber) in_r.get(ForbiddenTuples.FRACTION_VALUES)).numberValue().floatValue();
						if (frac_vars != frac_vals)
						{
							// We consider only experiments with the same value of
							// frac_vars and frac_vals
							continue;
						}
						TestGenerationExperiment exp = factory.get(in_r);
						if (exp == null)
						{
							continue;
						}
						et_size.add(exp);
						et_duration.add(exp);
						g.add(exp);
					}
					add(et_size);
					add(et_duration);
				}
			}
		}
	}

	public static void main(String[] args)
	{
		System.exit(GraphLab.initialize(args, GraphLab.class));
	}

	@Override
	public String isEnvironmentOk()
	{
		String out = "";
		if (!FileHelper.fileExists("variables-to-graph.php"))
		{
			String script = FileHelper.internalFileToString(this, "../scripts/variables-to-graph.php");
			FileHelper.writeFromString(new File("variables-to-graph.php"), script);
		}
		if (!FileHelper.fileExists("variables-to-hypergraph.php"))
		{
			String script = FileHelper.internalFileToString(this, "../scripts/variables-to-hypergraph.php");
			FileHelper.writeFromString(new File("variables-to-graph.php"), script);
		}
		if (out != null && out.isEmpty())
		{
			out = null;
		}
		return out;
	}
}
