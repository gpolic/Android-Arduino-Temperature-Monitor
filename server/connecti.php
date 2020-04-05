<?php

function Connection(){

  define('HOST','localhost');
  define('USER','xxx');
  define('PASS','xxx');
  define('DB','xxx');
 
  $con = mysqli_connect(HOST,USER,PASS,DB);
  return $con;
}

?>

