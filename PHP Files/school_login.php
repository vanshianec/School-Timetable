<?php

$connection = new mysqli("localhost", "id8270031_vanshianec", "123456", "id8270031_timetable");
if ( mysqli_connect_errno() ) {
	// If there is an error with the connection, stop the script and display the error.
	die ('Failed to connect to MySQL: ' . mysqli_connect_error());
}

// Prepare our SQL 
if ($stmt = $connection->prepare('SELECT school_name,school_password FROM schools WHERE school_username = ?')) {
	// Bind parameters (s = string, i = int, b = blob, etc), hash the password using the PHP password_hash function.
	$stmt->bind_param('s', $_POST['username']);
	$stmt->execute(); 
	$stmt->store_result(); 
	// Store the result so we can check if the account exists in the database.
	if ($stmt->num_rows > 0) {
		$stmt->bind_result($school_name, $password);
		$stmt->fetch();      
		// Account exists, now we verify the password.
		if (password_verify($_POST['password'], $password)) {
			// Verification success! User has loggedin!
			echo 'success';
			//send database name to the school program
			$stmt = $connection->prepare('SELECT school_database_name FROM schools WHERE school_username = ?');
			$stmt->bind_param('s', $_POST['username']);
			$stmt->execute();
			$result = $stmt->get_result();
			$row = $result->fetch_assoc();
            $school_database = $row['school_database_name'];
            echo $school_database;
		} else {
			echo 'Incorrect username and/or password!';
		}
	} else {
		echo 'Incorrect username and/or password!';
	}
	$stmt->close();
} else {
	echo 'Could not prepare statement!';
}
?>