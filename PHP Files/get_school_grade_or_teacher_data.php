 
<?php
    //open connection to mysql db
    $connection = mysqli_connect("localhost","id8270031_geomilev","123456",$_POST['androidDatabase']) or die("Error " . mysqli_error($connection));
 
    //fetch table rows from mysql db
    $post_value = $_POST['androidKey'];
  
    
    if(is_numeric($post_value)){
    $query = sprintf("SELECT order_id,monday_grade,monday_room,tuesday_grade,tuesday_room,
    wednesday_grade,wednesday_room,thursday_grade,thursday_room,friday_grade,friday_room FROM `%d`",intval($post_value)); 
    }
    else{
      $query =sprintf("SELECT order_id,monday_teacher,monday_room,tuesday_teacher,tuesday_room,
      wednesday_teacher,wednesday_room,thursday_teacher,thursday_room,friday_teacher,friday_room,shift FROM `%s`",$post_value);
    }
       
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