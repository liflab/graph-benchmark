<?php
/*
  Generates a Graphviz file representing the hypergraph whose vertex
  covering creates an n-way test coverage. See example.nway for the input
  file format. Example usage:
  
  $ php variables-to-hypergraph.php example.ncond

  To pipe the resulting graph directly to Graphviz to create
  an SVG picture, do e.g.:
  
  $ php variables-to-graph.php example.cond | dot -Tsvg > example.svg
  
  Last modified: 2020-02-16
*/

$output_format = "txt";
if (count($argv) <= 1 || $argv[1] === "--help")
{
  echo "Generates a Graphviz file representing the hypergraph whose vertex\n";
  echo "covering creates an n-way test coverage.\n";
  echo "\n";
  echo "Usage: php variables-to-hypergraph.php [--help] [-t x] [--edn] <filename>\n\n";
  echo "  -t x      Generates conditions for n-way test coverage\n";
  echo "  --edn     Output in EDN format (used for hitting-set)\n";
  echo "  filename  File containing the domains for each variable and\n";
  echo "            the conditions\n";
  echo "\n";
  exit(0);
}
$t_param = 0;
for ($i = 1; $i < count($argv); $i++)
{
  if ($argv[$i] === "-t" && $i < count($argv) - 1)
  {
    $t_param = $argv[$i + 1];
    $i++;
  }
  elseif ($argv[$i] === "--edn")
  {
    $output_format = "edn";
  }
  else
  {
    $input_filename = $argv[$i];
  }
}

// Read input
$p_names = array();
$domains = array();
$conditions = array("Once" => array(), "Always" => array());
$fh = fopen($input_filename, "r");
if (!$fh) 
{
  echo "Cannot open file $input_filename\n";
  exit(1);
}
while (($line = fgets($fh)) !== false)
{
  $line = trim($line);
  if (empty($line) || $line[0] == "#")
    continue;
  if (!preg_match("/:/", $line))
  {
    // This is a Boolean condition
    $cond_type = "Once";
    if (preg_match("/Once/", $line))
    {
      $cond_type = "Once";
      $line = str_replace("Once", "", $line);
    }
    elseif (preg_match("/Always/", $line))
    {
      $cond_type = "Always";
      $line = str_replace("Always", "", $line);
    }
    elseif (preg_match("/Never/", $line))
    {
      // A "never" is just an "always" of its negation
      $cond_type = "Always";
      $line = str_replace("Never", "", $line);
      $line = "!(".$line.")";
    }
    $conditions[$cond_type][] = $line;
  }
  else
  {
    // This is a variable definition
    list($p_name, $values) = explode(":", $line);
    $p_names[] = trim($p_name);
    $data = explode(",", $values);
    $domain = array();
    foreach ($data as $value)
    {
      $domain[] = trim($value);
    }
    $domains[] = $domain;
  }
}
fclose($fh);

// Open file
$fh = fopen("php://stdout", "w");
//$fh = fopen("/dev/null", "w");

/*
 // We don't need to generate vertices; the list of edges implicitly
 // contains them
 
// Generate vertices
$num_variables = count($domains);
$index = array();
for ($i = 0; $i < count($domains); $i++)
{
  $index[$i] = 0;
}
$vertex_nb = 0;
while ($index !== false)
{
  fputs($fh, " $vertex_nb [label=\"");
  for ($i = 0; $i < count($p_names); $i++)
  {
    $p_n = $p_names[$i];
    $p_v = $domains[$i][$index[$i]];
    if ($i > 0)
      fputs($fh, " ∧ ");
    fputs($fh, "$p_n=$p_v");
  }
  fputs($fh, "\"];\n");
  $index = increment_combination($index, $domains);
  $vertex_nb++;
}
*/

// Generate conditions for t-way if necessary
if ($t_param > 0)
{
  $t_conds = generate_conditions($p_names, $domains, $t_param);
  foreach ($t_conds as $condition)
  {
    $cond_string = "";
    foreach ($condition as $p => $v)
      $cond_string .= "$p == $v && ";
    $cond_string .= "true";
    $conditions["Once"][] = $cond_string;
  }
}

//print_r($conditions);
//exit();

// Generate hyperedges
$hyperedges = array();
for ($i = 0; $i < count($domains); $i++)
{
  $index[$i] = 0;
}
$vertex_nb = 0;
while ($index !== false)
{
  $rep_find = array();
  $rep_replace = array();
  for ($i = 0; $i < count($p_names); $i++)
  {
    $rep_find[] = $p_names[$i];
    $rep_replace[] = $domains[$i][$index[$i]];
  }
  if (isset($conditions["Always"]))
  {
    $has_one_violated_condition = false;
    foreach ($conditions["Always"] as $cond)
    {
      // Evaluate each "Always" condition on this valuation
      $cond_eval = replace_variables($rep_find, $rep_replace, $cond);
      $cond_value = eval("return $cond_eval;");
      if (!$cond_value)
      {
        $has_one_violated_condition = true;
        break;
      }
    }
    if ($has_one_violated_condition)
    {
      // One "always" condition is false. This vertex cannot be considered
      // as a test case: move on to the next
      $index = increment_combination($index, $domains);
      $vertex_nb++;
      continue;
    }
  }
  if (isset($conditions["Once"]))
  {
    foreach ($conditions["Once"] as $cond)
    {
      // Evaluate each "Once" condition on this valuation
      $cond_eval = replace_variables($rep_find, $rep_replace, $cond);
      $cond_value = eval("return $cond_eval;");
      if ($cond_value)
      {
        // This edge satisfies the condition
        $hyperedges[$cond][] = $vertex_nb;
      }
    }
  }
  $index = increment_combination($index, $domains);
  $vertex_nb++;
}

// Print hyperedges
if ($output_format === "txt")
{
  fputs($fh, $vertex_nb . "\n");
  foreach ($hyperedges as $cond => $vertices)
  { 
    fputs($fh, implode(" ", $vertices)."\n");
  }
}
elseif ($output_format === "edn")
{
  fputs($fh, "{\n");
  $edge_nb = 0;
  $first = true;
  foreach ($hyperedges as $cond => $vertices)
  {
    if (!$first)
      fputs($fh, ",\n");
    else
      $first = false;
    $un_vertices = array_unique($vertices);
    fputs($fh, "\"$edge_nb\" #{".implode(" ", $un_vertices)."}");
    $edge_nb++;
  }
  fputs($fh, "\n}");
}

// Close file
fclose($fh);

function get_combinations($max) // {{{
{
  $solutions = array();
  $index = array();
  for ($i = 0; $i < count($max); $i++)
  {
    $index[$i] = 0;
  }
  while ($index != false)
  {
    $solutions[] = $index;
    $index = increment_combination($index, $max);
  }
  return $solutions;
} // }}}

function increment_combination($sol, $max) // {{{
{
  for ($i = 0; $i < count($sol); $i++)
  {
    $sol[$i]++;
    if ($sol[$i] < count($max[$i]))
      break;
    $sol[$i] = 0;
    if ($i == count($sol) - 1)
    {
      return false;
    }
  }
  return $sol;
} // }}}

function replace_variables($finds, $replaces, $string)
{
  for ($i = 0; $i < count($finds); $i++)
  {
    $f = $finds[$i];
    $r = $replaces[$i];
    $string = preg_replace("/\\b$f\\b/", $r, $string);
  }
  return $string;
}

// Generate formulas
function generate_conditions($parameter_names, $domains, $t_param) // {{{
{
  //$parameter_names = array_keys($domains);
  $parameter_combinations = get_t_picks($parameter_names, $t_param);
  $conditions = array();
  foreach ($parameter_combinations as $parameter_combination)
  {
    //print_r($parameter_combination);
    $array_max = array();
    $i = 0;
    for ($j = 0; $j < count($parameter_names); $j++)
    {
      if ($parameter_combination[$i] == 1)
      {
        $array_max[] = $domains[$j];
      }
      $i++;
    }
    $value_assignments = get_combinations($array_max);
    foreach ($value_assignments as $value_assignment)
    {
      $asg_index = 0;
      $i = 0;
      $condition = array();
      for ($k = 0; $k < count($parameter_names); $k++)
      {
        $p_name = $parameter_names[$k];
        if ($parameter_combination[$i] == 1)
        {
          $asg_value = $value_assignment[$asg_index];
          $domain = $domains[$k];
          $j = 0;
          foreach ($domain as $value)
          {
            if ($j == $asg_value)
            {
              $condition[$p_name] = $value;
            }
            $j++;
          }
          $asg_index++;
        }
        $i++;
      }
      $conditions[] = $condition;
    }
  }
  return $conditions;
} // }}}

function get_choices($max) // {{{
{
  $solutions = array();
  $index = array();
  for ($i = 0; $i < count($max); $i++)
  {
    $index[$i] = 0;
  }
  while ($index != false)
  {
    //print_r($index);
    if (!is_valid_combination($index))
    {
      while (!is_valid_combination($index) && $index != false)
      {
        $index = increment_combination($index, $max);
        //print_r($index);
      }
      if ($index === false)
      {
        break;
      }
    }
    $solutions[] = $index;
    $index = increment_combination($index, $max);
  }
  return $solutions;
} // }}}

// Generate all combinations of t params
function get_t_picks($params, $t) // {{{
{
  $max = array();
  $solutions = array();
  $index = array();
  for ($i = 0; $i < count($params); $i++)
  {
    $index[$i] = 0;
    $max[$i] = array(0, 1);
  }
  while ($index != false)
  {
    //print_r($index);
    if (!is_valid_combination($index))
    {
      while (!is_valid_t_pick($index, $t) && $index != false)
      {
        $index = increment_combination($index, $max);
        //print_r($index);
      }
      if ($index === false)
      {
        break;
      }
    }
    $solutions[] = $index;
    $index = increment_combination($index, $max);
  }
  return $solutions;
} // }}}

function is_valid_combination($sol) // {{{
{
  for ($i = 0; $i < count($sol) - 1; $i++)
  {
    if ($sol[$i] >= $sol[$i + 1])
      return false;
  }
  return true;
} // }}}

function is_valid_t_pick($sol, $t) // {{{
{
  $num_chosen = 0;
  if ($sol === false)
  {
  	  return false;
  }
  for ($i = 0; $i < count($sol); $i++)
  {
    if ($sol[$i] == 1)
      $num_chosen++;
    if ($num_chosen > $t)
      return false;
  }
  return $num_chosen == $t;
} // }}}


?>
