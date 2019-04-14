<?php
 
  
    $connection = new mysqli("localhost", "id8270031_vanshianec",
                             "123456", "id8270031_timetable");
if ($connection->connect_errno) {
	// If there is an error with the connection, stop the script and display the error.
	die ('Failed to connect to MySQL: ' . $connection->connect_errno);
}

    // We need to check if the account with that username exists
    if ($stmt = $connection->prepare('SELECT school_name,school_password FROM schools WHERE school_username = ?')) {
	// Bind parameters (s = string, i = int, b = blob, etc), hash the password using the PHP password_hash function.
	$stmt->bind_param('s',$_POST['username']);
	$stmt->execute(); 
	$stmt->store_result(); 
	// Store the result so we can check if the account exists in the database.
	if ($stmt->num_rows > 0) {
		// Username already exists
		echo 'Училището вече съществува.';
	} else {
		// Username doesnt exists, insert new account
		if ($stmt = $connection->prepare("INSERT INTO `schools` (`school_name`, `school_username`, `school_password`, `school_database_name`, `logo_url`) VALUES (?, ?, ?, ?, ?);")) {
			// We do not want to expose passwords in our database, so hash the password and use password_verify when a user logs in.
			$password = password_hash($_POST['password'], PASSWORD_DEFAULT);
			$stmt->bind_param('sssss', $_POST['name'], $_POST['username'], $password, $_POST['db_name'], $_POST['logo_url']);
			$stmt->execute();
			echo 'Училището е успешно въведено в базата данни.';
		} else {
			echo 'Грешка при обработката на данните. Моля, опитайте по - късно.';
		}
	}
	$stmt->close();
} else {
	echo 'Грешка при обработката на данните. Моля, опитайте по - късно.';
}
$connection->close();
?>
