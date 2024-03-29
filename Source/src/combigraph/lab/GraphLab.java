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
import ca.uqac.lif.labpal.LatexNamer;
import ca.uqac.lif.labpal.Random;
import ca.uqac.lif.labpal.Region;
import ca.uqac.lif.labpal.TitleNamer;
import ca.uqac.lif.labpal.table.ExperimentTable;
import ca.uqac.lif.mtnp.plot.TwoDimensionalPlot.Axis;
import ca.uqac.lif.mtnp.plot.gnuplot.Scatterplot;
import ca.uqac.lif.mtnp.table.ExpandAsColumns;
import ca.uqac.lif.mtnp.table.TransformedTable;
import combigraph.lab.experiments.AllPairsTestGenerationExperiment;
import combigraph.lab.experiments.ActsTestGenerationExperiment;
import combigraph.lab.experiments.CasaTestGenerationExperiment;
import combigraph.lab.experiments.ColoringTestGenerationExperiment;
import combigraph.lab.experiments.GbgaTestGenerationExperiment;
import combigraph.lab.experiments.HypergraphTestGenerationExperiment;
import combigraph.lab.experiments.JennyTestGenerationExperiment;
import combigraph.lab.experiments.TcasesTestGenerationExperiment;
import combigraph.lab.experiments.TestGenerationExperiment;
import combigraph.lab.experiments.TestingProblemExperiment;
import combigraph.lab.experiments.VPTagTestGenerationExperiment;
import combigraph.lab.problems.ForbiddenTuples;
import combigraph.lab.problems.IncreasingValues;
import combigraph.lab.problems.TWayProblem;
import combigraph.lab.problems.TestSuiteCompletion;

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
	 * If set to true, the tols for solving the problems will not be run
	 */
	public static boolean s_dryRun = false;

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
	
	/**
	 * The maximum duration given to an experiment, in milliseconds
	 */
	public static int s_timeout = 100000;

	/**
	 * A name for LaTeX tables and figures
	 */
	LatexNamer m_latexNamer = new LatexNamer();

	/**
	 * A title for tables and figures
	 */
	TitleNamer m_titleNamer = new TitleNamer();

	@Override
	public void setup()
	{
		// Default parameters
		boolean with_t_way = true, 
				with_forbidden_tuples = false, 
				with_increasing_values = false,
				with_test_suite_completion = false;

		// Setup the lab's factory
		TestGenerationExperimentFactory factory = new TestGenerationExperimentFactory(this, getRandom());

		// Create the data folder if it does not exist
		File data_folder = new File(TestingProblemExperiment.s_folder);
		data_folder.mkdir();

		t_min = 2;
		t_max = 3;
		v_min = 2;
		v_max = 6;
		n_max = 6;

		// Setup the lab's regions
		TWayRegion big_r = new TWayRegion();
		big_r.addRange(T, t_min, t_max);
		big_r.addRange(V, v_min, v_max);
		big_r.addRange(N, n_min, n_max);
		big_r.add(TOOL_NAME,
				//ActsTestGenerationExperiment.NAME,
				//AllPairsTestGenerationExperiment.NAME,
				CasaTestGenerationExperiment.NAME,
				JennyTestGenerationExperiment.NAME,
				//TcasesTestGenerationExperiment.NAME,
				//VPTagTestGenerationExperiment.NAME,
				//ColoringTestGenerationExperiment.NAME, 
				GbgaTestGenerationExperiment.NAME,
				HypergraphTestGenerationExperiment.NAME);

		// Classical t-way problems
		if (with_t_way)
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
					et_size.setShowInList(false);
					TransformedTable tt_size = new TransformedTable(new ExpandAsColumns(TOOL_NAME, SIZE), et_size);
					m_titleNamer.setTitle(tt_size, out_r, "Classical t-way ", " for size");
					Scatterplot p_size = new Scatterplot(tt_size);
					ExperimentTable et_duration = new ExperimentTable(TOOL_NAME, T, DURATION);
					et_duration.setShowInList(false);
					TransformedTable tt_duration = new TransformedTable(new ExpandAsColumns(TOOL_NAME, DURATION), et_duration);
					m_titleNamer.setTitle(tt_duration, out_r, "Classical t-way ", " for duration");
					Scatterplot p_duration = new Scatterplot(tt_duration);
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
					add(tt_size, tt_duration);
					add(p_size, p_duration);
				}
			}
		}

		// Universal constraints: increasing values
		if (with_increasing_values)
		{
			TWayRegion twr = new TWayRegion(big_r);
			twr.add(TESTING_PROBLEM_NAME, IncreasingValues.NAME);
			Group g = new Group("Universal constraints: increasing values");
			add(g);
			{
				// Fixing n and v, varying t
				for (Region out_r : twr.all(T, V))
				{
					boolean added = false;
					ExperimentTable et_size = new ExperimentTable(TOOL_NAME, N, SIZE);
					et_size.setShowInList(false);
					TransformedTable tt_size = new TransformedTable(new ExpandAsColumns(TOOL_NAME, SIZE), et_size);
					m_titleNamer.setTitle(tt_size, out_r, "Increasing values ", " for size");
					Scatterplot p_size = new Scatterplot(tt_size);
					ExperimentTable et_duration = new ExperimentTable(TOOL_NAME, N, DURATION);
					et_duration.setShowInList(false);
					TransformedTable tt_duration = new TransformedTable(new ExpandAsColumns(TOOL_NAME, DURATION), et_duration);
					m_titleNamer.setTitle(tt_duration, out_r, "Increasing values ", " for duration");
					Scatterplot p_duration = new Scatterplot(tt_duration);
					for (Region in_r : out_r.all(N, TOOL_NAME))
					{
						TestGenerationExperiment exp = factory.get(in_r);
						if (exp == null)
						{
							continue;
						}
						else
						{
							added = true;
						}
						et_size.add(exp);
						et_duration.add(exp);
						g.add(exp);
					}
					if (added)
					{
						add(et_size, tt_size);
						add(et_duration, tt_duration);
						add(p_size, p_duration);
					}
				}
			}
		}

		// Forbidden tuples
		if (with_forbidden_tuples)
		{
			TWayRegion twr = new TWayRegion(big_r);
			//twr.addRange(N, n_min, n_max / 2);
			twr.add(TESTING_PROBLEM_NAME, ForbiddenTuples.NAME);
			twr.add(ForbiddenTuples.FRACTION_VALUES, 0, 0.1, 0.2, 0.5);
			twr.add(ForbiddenTuples.FRACTION_VARS, 0, 0.1, 0.2, 0.5);
			Group g = new Group("Universal constraints: forbidden tuples");
			g.setDescription("In these experiments, the problem is to generate a combinatorial "
					+ "test suite for given values of t, n and v, with the added constraint that "
					+ "some combination of parameter values (the \"forbidden tuples\") cannot "
					+ "appear in any test case.");
			add(g);
			{
				// Fixing n and v, and t, varying the fraction
				for (Region out_r : twr.all(N, V, T))
				{
					ExperimentTable et_size = new ExperimentTable(TOOL_NAME, ForbiddenTuples.FRACTION_VALUES, SIZE);
					et_size.setShowInList(false);
					TransformedTable tt_size = new TransformedTable(new ExpandAsColumns(TOOL_NAME, SIZE), et_size);
					m_titleNamer.setTitle(tt_size, out_r, "Forbidden tuples ", " for size");
					Scatterplot p_size = new Scatterplot(tt_size);
					p_size.setTitle(tt_size.getTitle());
					p_size.setCaption(Axis.X, "Fraction of tuples");
					p_size.setCaption(Axis.Y, "Size");
					ExperimentTable et_duration = new ExperimentTable(TOOL_NAME, ForbiddenTuples.FRACTION_VALUES, DURATION);
					et_duration.setShowInList(false);
					TransformedTable tt_duration = new TransformedTable(new ExpandAsColumns(TOOL_NAME, DURATION), et_duration);
					m_titleNamer.setTitle(tt_duration, out_r, "Forbidden tuples ", " for duration");
					Scatterplot p_duration = new Scatterplot(tt_duration);
					p_duration.setTitle(tt_duration.getTitle());
					p_duration.setCaption(Axis.X, "Fraction of tuples");
					p_duration.setCaption(Axis.Y, "Duration");
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
					add(et_size, tt_size);
					add(p_size);
					add(et_duration, tt_duration);
					add(p_duration);
				}
			}
		}

		// Test suite completion
		if (with_test_suite_completion)
		{
			TWayRegion twr = new TWayRegion(big_r);
			//twr.addRange(N, n_min, n_max / 2);
			twr.add(TESTING_PROBLEM_NAME, TestSuiteCompletion.NAME);
			twr.addRange(TestSuiteCompletion.NUM_TESTS, 0, 10, 2);
			Group g = new Group("Existential constraints: test suite completion");
			g.setDescription("In these experiments, the problem is to generate a combinatorial "
					+ "test suite for given values of t, n and v, with the added constraint that "
					+ "an existing list of test cases must be included in the result. The tools "
					+ "must therefore complete this partial test suite in the best possible way.");
			add(g);
			{
				// Fixing n and v, and t, varying the number of tests
				for (Region out_r : twr.all(N, V, T))
				{
					ExperimentTable et_size = new ExperimentTable(TOOL_NAME, TestSuiteCompletion.NUM_TESTS, SIZE);
					et_size.setShowInList(false);
					TransformedTable tt_size = new TransformedTable(new ExpandAsColumns(TOOL_NAME, SIZE), et_size);
					m_titleNamer.setTitle(tt_size, out_r, "Test suite completion ", " for size");
					Scatterplot p_size = new Scatterplot(tt_size);
					p_size.setTitle(tt_size.getTitle());
					p_size.setCaption(Axis.X, "Number of pre-existing tests");
					p_size.setCaption(Axis.Y, "Size");
					ExperimentTable et_duration = new ExperimentTable(TOOL_NAME, TestSuiteCompletion.NUM_TESTS, DURATION);
					et_duration.setShowInList(false);
					TransformedTable tt_duration = new TransformedTable(new ExpandAsColumns(TOOL_NAME, DURATION), et_duration);
					m_titleNamer.setTitle(tt_duration, out_r, "Test suite completion ", " for duration");
					Scatterplot p_duration = new Scatterplot(tt_duration);
					p_duration.setTitle(tt_duration.getTitle());
					p_duration.setCaption(Axis.X, "Number of pre-existing tests");
					p_duration.setCaption(Axis.Y, "Duration");
					for (Region in_r : out_r.all(TOOL_NAME, TestSuiteCompletion.NUM_TESTS))
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
					add(et_size, tt_size);
					add(p_size);
					add(et_duration, tt_duration);
					add(p_duration);
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
		if (!FileHelper.commandExists(JennyTestGenerationExperiment.JENNY))
		{
			out += "<li>Command <tt>jenny</tt> not found. Experiments involving running Jenny will not work</li>";
		}
		if (!FileHelper.commandExists(CasaTestGenerationExperiment.CASA))
		{
			out += "<li>Command <tt>CASA</tt> not found. Experiments involving running CASA will not work</li>";
		}
		if (!FileHelper.fileExists("variables-to-graph.php"))
		{
			String script = FileHelper.internalFileToString(this, "scripts/variables-to-graph.php");
			FileHelper.writeFromString(new File("variables-to-graph.php"), script);
		}
		if (!FileHelper.fileExists("variables-to-hypergraph.php"))
		{
			String script = FileHelper.internalFileToString(this, "scripts/variables-to-hypergraph.php");
			FileHelper.writeFromString(new File("variables-to-hypergraph.php"), script);
		}
		if (out != null && out.isEmpty())
		{
			return null;
		}
		return "<ul>" + out + "</ul>";
	}
}
