<?php
	include("connecti.php"); 		
	$link = Connection();

        $jsonres = array();

        $res = mysqli_query($link, "SELECT * FROM `tempLog` ORDER BY `timest` DESC LIMIT 0, 30");

        while($row = mysqli_fetch_array($res)) {
              array_push($jsonres, array('timest'=>$row["timest"],'temperature'=>$row["temperature"],'humidity'=>$row["humidity"] ));
        }
        
        echo json_encode(array("result"=>$jsonres));

	mysqli_free_result($res);
	mysqli_close($link);  
?>
