<?php
    //open connection to mysql db
    $connection = mysqli_connect("localhost","id8270031_vanshianec","123456","id8270031_timetable") or die("Error " . mysqli_error($connection));
  
    $query = "SELECT school_name,logo_url FROM schools";
       
    $result = mysqli_query($connection, $query) or die("Error in Selecting " . mysqli_error($connection));
 
    //create an array
    $rows = array();
    while($r = mysqli_fetch_assoc($result)) {
    $rows[] = $r;
    }
    
    print json_encode($rows,JSON_UNESCAPED_UNICODE );
    
 
    //close the db connection
    mysqli_close($connection);
?>