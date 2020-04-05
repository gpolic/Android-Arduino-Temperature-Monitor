<?php
	include("connecti.php"); 		
	$link = Connection();

        $date1 = $_POST["date"];
        $query = "SELECT * FROM tempLog WHERE timest > '$date1' ORDER BY timest ASC";
        $jsonres = array();
        
        $res = mysqli_query($link, $query);

        while($row = mysqli_fetch_array($res)) {
              array_push($jsonres, array('timest'=>$row["timest"],'temperature'=>$row["temperature"],'humidity'=>$row["humidity"] ));
        }
        
        echo json_encode(array("result"=>$jsonres));

	mysqli_free_result($res);
	mysqli_close($link);  

?>
