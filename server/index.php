<?php
	include("connecti.php"); 		
	$link = Connection();

        $targetpage = "index.php";   //your file name  (the name of this file)
        $limit = 12;             //how many items to show per page
        $adjacents = 1;         // limit page links, keep low
        $page=1;
        $pagination = '';
?>

<html>
   <head>
          <meta name='apple-mobile-web-app-capable' content='yes' />
          <meta name='apple-mobile-web-app-status-bar-style' content='black-translucent' />
          <link rel='stylesheet' type='text/css' href='http://randomnerdtutorials.com/ethernetcss.css'/>
      <title>Home Temperature Monitoring</title>
   </head>
<body>
   <h1>Home Temperature / Humidity</h1>

<?php
/* get the number of rows  */
    $query = "SELECT COUNT(*) as num FROM `tempLog`";
    $res1 = mysqli_query($link, $query);
    $total_pages = mysqli_fetch_array($res1);
    $total_pages = $total_pages['num'];

 /* Setup vars for query. */
    if (isset($_GET['page'])) {
      $page = $_GET['page'];
    }
    if($page) 
        $start = ($page - 1) * $limit;          //first item to display on this page
    else
        $start = 0;      

/* Get data. 
    $sql = "SELECT * FROM `tempLog` ORDER BY `timest` DESC LIMIT $start, $limit";
    $result = mysqli_query($link, $sql);  */

/* Setup page vars for display. */
    if ($page == 0) $page = 1;                  //if no page var is given, default to 1.
    $prev = $page - 1;                          //previous page is page - 1
    $next = $page + 1;                          //next page is page + 1
    $lastpage = ceil($total_pages/$limit);      //lastpage is = total pages / items per page, rounded up.
    $lpm1 = $lastpage - 1;                      //last page minus 1
?>

<table border="1" align="center" cellspacing="0" cellpadding="5">
<tr>	<td align="center" valign="middle">&nbsp;Date / Time&nbsp;</td>
<td align="center" valign="middle">&nbsp;Temperature&nbsp;</td>
<td align="center" valign="middle">&nbsp;Humidity&nbsp;</td>
</tr>

<?php 
	     $result = mysqli_query($link, "SELECT * FROM `tempLog` ORDER BY `timest` DESC LIMIT $start, $limit");
             if($result!==FALSE){
	     while($row = mysqli_fetch_array($result)) {
	        printf("<tr><td align=\"center\"> &nbsp;%s </td><td align=\"center\"> &nbsp;%s&nbsp; </td><td align=\"center\"> &nbsp;%s&nbsp; </td></tr>", 
	           $row["timest"], $row["temperature"], $row["humidity"]);
	     }
	     mysqli_free_result($result);
	     mysqli_close($link);
	  }
?>

</table>
<br>
<br>

<?php
 if($lastpage > 1)
    {   
        $pagination .= "<div class=\"pagination\">";
        //previous button
        if ($page > 1) 
            $pagination.= "<a href=\"$targetpage?page=$prev\">Previous</a>";
        else
            $pagination.= "<span class=\"disabled\">Previous</span>"; 

        //pages 
        if ($lastpage < 7 + ($adjacents * 2))   //not enough pages to bother breaking it up
        {   
            for ($counter = 1; $counter <= $lastpage; $counter++)
            {
                if ($counter == $page)
                    $pagination.= "<span class=\"current\">$counter</span>";
                else
                    $pagination.= "<a href=\"$targetpage?page=$counter\">$counter</a>";                 
            }
        }
        elseif($lastpage > 5 + ($adjacents * 2))    //enough pages to hide some
        {
            //close to beginning; only hide later pages
            if($page < 1 + ($adjacents * 2))        
            {
                for ($counter = 1; $counter < 4 + ($adjacents * 2); $counter++)
                {
                    if ($counter == $page)
                        $pagination.= "<span class=\"current\">$counter</span>";
                    else
                        $pagination.= "<a href=\"$targetpage?page=$counter\">$counter</a>";                 
                }
                $pagination.= "...";
                $pagination.= "<a href=\"$targetpage?page=$lpm1\">$lpm1</a>";
                $pagination.= "<a href=\"$targetpage?page=$lastpage\">$lastpage</a>";       
            }
            //in middle; hide some front and some back
            elseif($lastpage - ($adjacents * 2) > $page && $page > ($adjacents * 2))
            {
                $pagination.= "<a href=\"$targetpage?page=1\">1</a>";
                $pagination.= "<a href=\"$targetpage?page=2\">2</a>";
                $pagination.= "...";
                for ($counter = $page - $adjacents; $counter <= $page + $adjacents; $counter++)
                {
                    if ($counter == $page)
                        $pagination.= "<span class=\"current\">$counter</span>";
                    else
                        $pagination.= "<a href=\"$targetpage?page=$counter\">$counter</a>";                 
                }
                $pagination.= "...";
                $pagination.= "<a href=\"$targetpage?page=$lpm1\">$lpm1</a>";
                $pagination.= "<a href=\"$targetpage?page=$lastpage\">$lastpage</a>";       
            }
            //close to end; only hide early pages
            else
            {
                $pagination.= "<a href=\"$targetpage?page=1\">1</a>";
                $pagination.= "<a href=\"$targetpage?page=2\">2</a>";
                $pagination.= "...";
                for ($counter = $lastpage - (2 + ($adjacents * 2)); $counter <= $lastpage; $counter++)
                {
                    if ($counter == $page)
                        $pagination.= "<span class=\"current\">$counter</span>";
                    else
                        $pagination.= "<a href=\"$targetpage?page=$counter\">$counter</a>";                 
                }
            }
        }

        //next button
        if ($page < $counter - 1) 
            $pagination.= "<a href=\"$targetpage?page=$next\">Next</a>";
        else
            $pagination.= "<span class=\"disabled\">Next</span>";
        $pagination.= "</div>\n";       
    }
   printf($pagination);
?>

</body>
</html>
