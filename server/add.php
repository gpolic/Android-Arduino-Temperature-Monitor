<?php
   	include("connecti.php");
   	
   	$link=Connection();

	$temp1=$_POST["temp1"];
	$hum1=$_POST["hum1"];

        $query = "INSERT INTO `tempLog` VALUES (null, '".$temp1."','".$hum1."')"; 

   	mysqli_query($link, $query);
	mysqli_close($link);

   	header("Location: index.php");
?>
