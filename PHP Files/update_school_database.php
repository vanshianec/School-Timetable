<?php
    //open connection to mysql db
    $connection = mysqli_connect("localhost",$_POST['username'],$_POST['password'],$_POST['database']) or die("Error " . mysqli_error($connection));
 
    //fetch table rows from mysql db
    $query = $_POST['query'];
   
     $result = mysqli_multi_query($connection, $query) or die("Error in Selecting " . mysqli_error($connection));
    //close the db connection
    mysqli_close($connection);
?>