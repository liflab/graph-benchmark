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

import ca.uqac.lif.labpal.Region;
import combigraph.lab.problems.TWayProblem;

public class TWayRegion extends Region
{
	public TWayRegion()
	{
		super();
	}
	
	public TWayRegion(Region r)
	{
		super(r);
	}
	
	@Override
	public boolean isInRegion(Region r) 
	{
		if (r.getAll(TWayProblem.N).size() > 1 || r.getInt(TWayProblem.T) > 1)
		{
			// One of T and N is not yet defined
			return true;
		}
		if (r.getInt(TWayProblem.T) >= r.getInt(TWayProblem.N))
		{
			// Impossible that t > n, uninteresting when t=n
			return false;
		}
		return true;
	}
	
	@Override
	protected TWayRegion getEmptyRegion()
	{
		return new TWayRegion();
	}
	
	@Override
	protected TWayRegion getRegion(Region r)
	{
		return new TWayRegion(r);
	}
}
