<?php
/*
  Generates a Graphviz file representing the graph whose colouring
  creates an n-way test coverage. See example.nway for the input file
  format. Example usage:
  
  $ php variables-to-graph.php 3 example.nway

  To pipe the resulting graph directly to Graphviz to create
  an SVG picture, do e.g.:
  
  $ php variables-to-graph.php 3 example.nway | dot -Tsvg > example.svg
  
  Last update: 2020-02-16
*/

$t_param = $argv[1];
if ($t_param === "--help")
{
  echo "Generates a Graphviz file representing the graph whose colouring\n";
  echo "creates an n-way test coverage.\n";
  echo "\n";
  echo "Usage: php variables-to-graph.php <n> <filename>\n\n";
  echo "  n         Generate conditions for combinations of n parameters\n";
  echo "  filename  File containing the domains for each variable\n";
  echo "\n";
  exit(0);
}
$input_filename = $argv[2];

$input = file_get_contents($input_filename);

//$domain_size = 4;
//echo "t\tk\tv\tCA(t,k,v)\n";
//for ($num_params = 7; $num_params < 8; $num_params++)
{
  //echo "$t_param\t$num_params\t$domain_size\t";
  $domains = generate_from_input($input);
  //$domains = generate($num_params, $domain_size);
  $conditions = generate_conditions($domains, $t_param);
  add_existential($conditions, $input);
  $handle = fopen("php://stdout", "w");
  generate_dot($conditions, $handle);
  fclose($handle);
  if (false)
  {        
  	// Take out of the block to run Maxima on input file
	file_put_contents("/tmp/input.txt", generate_maxima_colouring($conditions));
	file_put_contents("log.txt", generate_maxima_colouring($conditions));   //Fix for windows, use an ignored file extension for git
	$output = array();
	$chromatic_index = 0;
	exec("maxima --very-quiet --batch=/tmp/input.txt", $output);
	print_r($output);
	$colouring = parse_maxima_colouring($output, $chromatic_index);
	/*$chromatic_index_line = explode(" ", $output[count($output) - 2]);
	$chromatic_index = $chromatic_index_line[count($chromatic_index_line) - 1];
	echo "$chromatic_index\n";*/
	//echo "$chromatic_index\n";
  }
}

// Handle additional constraints
function add_existential(&$conditions, $input) // {{{
{
	foreach (explode("\n", $input) as $line)
	{
		$line = trim($line);
		if (empty($line) || $line[0] === "#")
			continue;
		if (preg_match("/Once/", $line))
		{
			$line = str_replace("Once", "", $line);
			$conjuncts = explode("&&", $line);
			$condition = array();
			foreach ($conjuncts as $c)
			{
				list($p, $v) = explode("==", $c);
				$condition[trim($p)] = trim($v);
			}
			$conditions[] = $condition;
		}
	}
} // }}}

// Parse the output of Maxima
function parse_maxima_colouring($output, &$chromatic_index) // {{{
{
  $contents = "";
  $read = false;
  $colouring = array();
  foreach ($output as $line)
  {
    if (strpos($line, "vertex_coloring(d)") !== false)
    {
      $read = true;
      continue;
    }
    if ($read)
    {
      $contents .= " ".$line;
    }
  }
  //echo $contents;
  // Parse colouring
  preg_match("/\\[(\\d+), \\[(.*)\\]\\]/", $contents, $matches);
  $chromatic_index = $matches[1];
  preg_match_all("/\\[(\\d+), (\\d+)\\]/", $matches[2], $matches2, PREG_SET_ORDER);
  foreach ($matches2 as $match)
  {
    $colouring[] = $match[2];
  }
  //print_r($colouring);
  return $colouring;
} // }}}

// Generate formulas
function generate_conditions($domains, $t_param) // {{{
{
  $parameter_names = array_keys($domains);
  $parameter_combinations = get_t_picks($parameter_names, $t_param);
  $conditions = array();
  foreach ($parameter_combinations as $parameter_combination)
  {
    //print_r($parameter_combination);
    $array_max = array();
    $i = 0;
    foreach ($parameter_names as $p_name)
    {
      if ($parameter_combination[$i] == 1)
        $array_max[] = count($domains[$p_name]) - 1;
      $i++;
    }
    $value_assignments = get_combinations($array_max);
    foreach ($value_assignments as $value_assignment)
    {
      $asg_index = 0;
      $i = 0;
      $condition = array();
      foreach ($parameter_names as $p_name)
      {
        if ($parameter_combination[$i] == 1)
        {
          $asg_value = $value_assignment[$asg_index];
          $domain = $domains[$p_name];
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

// Read input
function generate_from_input($input) // {{{
{
  $domains = array();
  $lines = explode("\n", $input);
  foreach ($lines as $line)
  {
    $line = trim($line);
    if (empty($line) || $line[0] == "#")
      continue;
  	if (strpos($line, ":") === false)
  		continue;
    list($p_name, $values) = explode(":", $line);
    $data = explode(",", $values);
    foreach ($data as $value)
    {
      $domains[$p_name][] = trim($value);
    }
  }
  return $domains;
} // }}}

function generate($num_params, $domain_size) // {{{
{
  $domains = array();
  $domain = array();
  // Create domain of fixed size
  for ($i = 0; $i < $domain_size; $i++)
  {
    $domain[] = $i;
  }
  for ($i = 0; $i < $num_params; $i++)
  {
    $p_name = chr(97 + $i);
    $domains[$p_name] = $domain;
  }
  return $domains;
} // }}}

// Generate graph in DOT format
function generate_dot($conditions, $handle) // {{{
{
  fputs($handle, "graph G {\n");
  //$maxima_out_file = "";
  //$maxima_out_file_edges = "";
  //$maxima_out_file_vertices = "";
  $first_vertex = true;
  /* Pas besoin de ceci pour DOT
  for ($i = 0; $i < count($conditions); $i++)
  {
    if ($first_vertex)
      $first_vertex = false;
    else
      $maxima_out_file_vertices .= ",";
    $maxima_out_file_vertices .= "$i";
  }*/
  $first_edge = true;
  for ($i = 0; $i < count($conditions); $i++)
  {
    $first_condition = $conditions[$i];
    //$maxima_out_file_edges .= "$i [label=\"";
    fputs($handle, "$i [label=\"");
    foreach ($first_condition as $p => $v)
    {
      //$maxima_out_file_edges .= "$p = $v ";
      fputs($handle, "$p = $v ");
    }
    //$maxima_out_file_edges .= "\"];\n";
    fputs($handle, "\"];\n");
    for ($j = $i + 1; $j < count($conditions); $j++)
    {
      $second_condition = $conditions[$j];
      if (are_conflicting($first_condition, $second_condition))
      {
        //echo "Conditions ".print_r($first_condition, true)." and ".print_r($second_condition, true)."\n";
        //$maxima_out_file_edges .= "$i -- $j;\n";
        fputs($handle, "$i -- $j;\n");
      }
    }
  }
  //$maxima_out_file .= "$maxima_out_file_edges\n";
  fputs($handle, "}");
  //return $maxima_out_file;
} // }}}

// Generate a Maxima file that finds a coloring of the graph
function generate_maxima_colouring($conditions) // {{{
{
  //$maxima_out_file = "";
  //$maxima_out_file .= "load (graphs)$\n";
  $maxima_out_file .= "d : ".generate_maxima_graph($conditions);
  //$maxima_out_file .= "vertex_coloring(d);\n";
  return $maxima_out_file;
} // }}}

// Generate a Maxima file that finds the chromatic number of the graph
function generate_maxima_chromatic_index($conditions) // {{{
{
  $maxima_out_file = "";
  $maxima_out_file .= "load (graphs)$\n";
  $maxima_out_file .= "d : ".generate_maxima_graph($conditions);
  $maxima_out_file .= "chromatic_index(d);\n";
  return $maxima_out_file;
} // }}}

// Generate graph in Maxima format
function generate_maxima_graph($conditions) // {{{
{
  $maxima_out_file = "";
  $maxima_out_file_edges = "";
  $maxima_out_file_vertices = "";
  $first_vertex = true;
  for ($i = 0; $i < count($conditions); $i++)
  {
    if ($first_vertex)
      $first_vertex = false;
    else
      $maxima_out_file_vertices .= ",";
    $maxima_out_file_vertices .= "$i";
  }
  $first_edge = true;
  for ($i = 0; $i < count($conditions); $i++)
  {
    $first_condition = $conditions[$i];
    for ($j = $i + 1; $j < count($conditions); $j++)
    {
      $second_condition = $conditions[$j];
      if (are_conflicting($first_condition, $second_condition))
      {
        //echo "Conditions ".print_r($first_condition, true)." and ".print_r($second_condition, true)."\n";
        if ($first_edge)
          $first_edge = false;
        else
          $maxima_out_file_edges .= ",";
        $maxima_out_file_edges .= "[$i,$j]";
      }
    }
  }
  return "create_graph([$maxima_out_file_vertices], [$maxima_out_file_edges])$\n";
} // }}}

// Checks if two node conditions are conflicting. As implemented, the
// function works for conditions that are conjunctions.
function are_conflicting($condition1, $condition2) // {{{
{
  foreach ($condition1 as $p1 => $v1)
  {
    if (isset($condition2[$p1]) && $condition2[$p1] != $v1)
    {
      //echo "Conflict: $p1: ".$condition2[$p1]." vs ".$condition1[$p1];
      return true;
    }
  }
  return false;
} // }}}

/*
// Iterate with solver
echo "Trying to cover $t_param-way combinations of ".count($parameter_names)." parameters (".count($conditions)." combinations)\n";
//for ($num_tests = 1; $num_tests <= count($conditions); $num_tests++)
$num_tests = 12;
$num_lines = 0;
$best_case = count($conditions);
for ($tries = 0; $tries < 1000; $tries++)
{
  //echo "Trying with $num_tests test(s)...\r";
  $renamed_conditions = rename_parameters_random_alternate($conditions, $num_tests, $num_lines);
  // Create input file
  file_put_contents("/tmp/input.l2c", conditions_to_logic2cnf($renamed_conditions));
  // Send to solver
  $result = exec("./tosolver.sh /tmp/input.l2c");
  if ($result == "SATISFIABLE" && $num_lines < $best_case)
  {
    $best_case = $num_lines;
  }
  printf("Generation %3d: %3d\tBest solution: %3d tests  \r", $tries, $num_lines, $best_case);
}
echo "\n";
*/

function conditions_to_logic2cnf($conditions) // {{{
{
  $out_file = "";
  $encountered_vars = array();
  $first = true;
  foreach ($conditions as $condition)
  {
    if ($first)
      $first = false;
    else
      $out_file .= " .\n";
    $p_names = array_keys($condition);
    foreach ($p_names as $p)
    {
      if (!in_array($p, $encountered_vars))
        $encountered_vars[] = $p;
    }
    $out_file .= condition_to_sat_boolean($condition);
  }
  $preamble = "def";
  foreach ($encountered_vars as $p)
  {
    $preamble .= " $p";
  }
  $out_file = $preamble.";\n\n".$out_file.";";
  return $out_file;
} // }}}

function condition_to_sat_boolean($condition) // {{{
{
  $first = true;
  $out = "";
  foreach ($condition as $p => $v)
  {
    if ($first)
      $first = false;
    else
      $out .= " . ";
    if ($v == 0)
      $out .= "~$p";
    else
      $out .= "$p";
  }
  return $out;
} // }}}

function rename_parameters($conditions, $first_n) // {{{
{
  $new_conditions = array();
  $index = 0;
  foreach ($conditions as $condition)
  {
    $par_name = min(++$index, $first_n);
    $new_condition = array();
    foreach ($condition as $p => $v)
    {
      $new_condition[$p.$par_name] = $v;
    }
    $new_conditions[] = $new_condition;
  }
  return $new_conditions;
} // }}}

function rename_parameters_random($conditions, $first_n) // {{{
{
  $new_conditions = array();
  $index = 0;
  $available_conditions = array();
  for ($i = 0; $i < count($conditions); $i++)
  {
    $available_conditions[] = $i;
    $new_conditions[] = $conditions[$i];
  }
  for ($tries = 0; $tries < $first_n; $tries++)
  {
    // Pick a condition that was not renamed at random
    $index = rand(0, count($available_conditions) - 1);
    $index_pos = $available_conditions[$index];
    //echo "Renaming $index_pos\n";
    $condition = $conditions[$index_pos];
    unset($available_conditions[$index]);
    $available_conditions = array_values($available_conditions);
    //print_r($available_conditions);
    $new_condition = array();
    foreach ($condition as $p => $v)
    {
      $new_condition[$p.$tries] = $v;
    }
    $new_conditions[$index_pos] = $new_condition;
  }
  return $new_conditions;
} // }}}

function rename_parameters_random_alternate($conditions, $first_n, &$total_sols) // {{{
{
  $new_conditions = array();
  $index = 0;
  $values = array();
  foreach ($conditions as $condition)
  {
    $par_name = rand(0, $first_n);
    if (!in_array($par_name, $values))
      $values[] = $par_name;
    $new_condition = array();
    foreach ($condition as $p => $v)
    {
      $new_condition[$p.$par_name] = $v;
    }
    $new_conditions[] = $new_condition;
  }
  $total_sols = count($values);
  return $new_conditions;
} // }}}

function condition_to_sat($condition) // {{{
{
  $first = true;
  $out = "";
  foreach ($condition as $p => $v)
  {
    if ($first)
      $first = false;
    else
      $out .= " & ";
    $out .= "$p = $v";
  }
  return $out;
} // }}}

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
    $max[$i] = 1;
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
  if (!$sol)
  {
  	  return false;
  }
  for ($i = 0; $i < count($sol) - 1; $i++)
  {
    if ($sol[$i] >= $sol[$i + 1])
      return false;
  }
  return true;
} // }}}

function is_valid_t_pick($sol, $t) // {{{
{
  if (!$sol)
  {
  	  return false;
  }
  $num_chosen = 0;
  for ($i = 0; $i < count($sol); $i++)
  {
    if ($sol[$i] == 1)
      $num_chosen++;
    if ($num_chosen > $t)
      return false;
  }
  return $num_chosen == $t;
} // }}}

function increment_combination($sol, $max) // {{{
{
  for ($i = 0; $i < count($sol); $i++)
  {
    $sol[$i]++;
    if ($sol[$i] <= $max[$i])
      break;
    $sol[$i] = 0;
    if ($i == count($sol) - 1)
    {
      return false;
    }
  }
  return $sol;
} // }}}

// :mode=php:wrap=none:folding=explicit:
?>
