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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.labpal.ExperimentException;
import ca.uqac.lif.mtnp.util.FileHelper;
import combigraph.lab.problems.ExistentialProblem;
import combigraph.lab.problems.TestSuiteCompletion;

/**
 * A special case of Jenny experiment for the {@link TestSuiteCompletion} test
 * generation problem. A particular experiment is needed, since test suite
 * completion modifies the basic command line syntax used to call the tool. 
 */
public class JennyTestCompletionExperiment extends JennyTestGenerationExperiment 
{
	public JennyTestCompletionExperiment(TestSuiteCompletion problem) 
	{
		super(problem);
	}
	
	@Override
	protected List<String> getAdditionalParameters()
	{
		List<String> out = new ArrayList<String>(1);
		out.add("-o" + ((TestSuiteCompletion) m_problem).getJennySeedFilename());
		return out;
	}
	
	@Override
	public boolean prerequisitesFulfilled()
	{
		if (!super.prerequisitesFulfilled())
		{
			return false;
		}
		String seed_filename = ((TestSuiteCompletion) m_problem).getJennySeedFilename();
		return FileHelper.fileExists(seed_filename);
	}
	
	@Override
	public void fulfillPrerequisites() throws ExperimentException
	{
		String seed_filename = ((TestSuiteCompletion) m_problem).getJennySeedFilename();
		File f = new File(seed_filename);
		try
		{
			PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(f)));
			((ExistentialProblem) m_problem).writeJennySeedFile(ps);
			ps.close();
		}
		catch (IOException e)
		{
			throw new ExperimentException(e);
		}
	}
}
