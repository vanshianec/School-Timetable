 
<?php
    //this was used on the previous host when user access was required
    //$connection = mysqli_connect("localhost","schoolti_uchenik","uchenici",$_POST['androidDatabase']) or die("Error " . //mysqli_error($connection));
    //open connection to mysql db
    $connection = mysqli_connect("localhost","id8270031_geomilev","123456",$_POST['androidDatabase']) or die("Error " . mysqli_error($connection));
    //fetch table rows from mysql db
    $value = $_POST['grades_or_teachers'];
    $table = 'teachers' === $value ? 'name' : 'grade';
    $query = sprintf("SELECT %s FROM %s",$table,$value);
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