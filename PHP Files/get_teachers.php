 
<?php
    //open connection to mysql db
    $connection = mysqli_connect("localhost",$_POST['username'],$_POST['password'],$_POST['database']) or die("Error " . mysqli_error($connection));
 
    //fetch table rows from mysql db
    $post_value = $_POST['teacher'];
  
    $query = sprintf("SELECT teacher_id,name FROM ".$post_value);
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