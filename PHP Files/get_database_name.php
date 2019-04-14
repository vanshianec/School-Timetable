<?php

$connection = new mysqli("localhost", "id8270031_vanshianec", "123456", "id8270031_timetable");
if ( mysqli_connect_errno() ) {
	// If there is an error with the connection, stop the script and display the error.
	die ('Failed to connect to MySQL: ' . mysqli_connect_error());
}

			$stmt = $connection->prepare('SELECT school_database_name FROM schools WHERE school_name = ?');
			$stmt->bind_param('s', $_POST['androidSchoolName']);
			$stmt->execute();
			$result = $stmt->get_result();
			$row = $result->fetch_assoc();
            $school_database = $row['school_database_name'];
            echo $school_database;

?>