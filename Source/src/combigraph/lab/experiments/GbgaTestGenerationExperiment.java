/*
    A lab for comparing combinatorial test suite generators
    Copyright (C) 2017-2021 Sylvain Hallé, Edmond La Chance,
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

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import ca.uqac.lif.labpal.ExperimentException;
import combigraph.lab.GraphLab;
import combigraph.lab.problems.CombinatorialTestingProblem;
import combigraph.lab.problems.TWayProblem;

/**
 * Experiment that retrieves the results of the GBGA algorithm. These results
 * are taken from Table I of the following publication:
 * <ul>
 * <li>J. Torres-Jimenez, J. C.Perez-Torres. (2019). A greedy algorithm to
 * construct covering arrays using a graph representation. <i>Information
 * Sciences</i>, Volume 477, March 2019, Pages 234-245, Elsevier.
 * DOI: 10.1016/j.ins.2018.10.048</li>
 * </ul>
 */
public class GbgaTestGenerationExperiment extends TestGenerationExperiment
{
	/**
	 * The name of the tool.
	 */
	public static final transient String NAME = "GBGA";
	
	/**
	 * A map associating a t-way problem to a test suite size.
	 */
	protected static transient Map<TWayProblem,Integer> s_sizes;

	/**
	 * A map associating a t-way problem to a test generation time.
	 */
	protected static transient Map<TWayProblem,Float> s_times;
	
	// Read data from internal static file
	static
	{
		s_sizes = new HashMap<TWayProblem,Integer>();
		s_times = new HashMap<TWayProblem,Float>();
		Scanner scanner = new Scanner(GraphLab.class.getResourceAsStream("experiments/data/GBGA.csv"));
		while (scanner.hasNextLine())
		{
			String line = scanner.nextLine().trim();
			if (line.isEmpty() || line.startsWith("#"))
				continue;
			String[] parts = line.split("\t");
			int t = Integer.parseInt(parts[0]);
			int n = Integer.parseInt(parts[1]);
			int v = Integer.parseInt(parts[2]);
			int size = Integer.parseInt(parts[3]);
			float time = Float.parseFloat(parts[4]);
			TWayProblem prob = new TWayProblem(null, t, n, v);
			s_sizes.put(prob, size);
			s_times.put(prob, time);
		}
		scanner.close();
	}
	
	public GbgaTestGenerationExperiment(CombinatorialTestingProblem problem)
	{
		super(problem, NAME);
		TWayProblem prob = (TWayProblem) m_problem;
		if (s_sizes.containsKey(prob))
		{
			write(SIZE, s_sizes.get(prob));
		}
		if (s_times.containsKey(prob))
		{
			write(DURATION, s_times.get(prob));
		}
	}
	
	@Override
	public void execute() throws ExperimentException, InterruptedException
	{
		// Do nothing
	}
	
	@Override
	public boolean prerequisitesFulfilled()
	{
		return true;
	}
	
	@Override
	public void fulfillPrerequisites() throws ExperimentException
	{
		// Do nothing
	}
	
	@Override
	public void cleanPrerequisites()
	{
		// Do nothing
	}
}
