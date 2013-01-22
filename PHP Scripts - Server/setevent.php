<?php
/**
* File to handle all API requests
* Accepts GET and POST
*
* Each request will be identified by TAG
* Response will be JSON data
* check for POST request
*/
if (isset($_POST['tag']) && $_POST['tag'] != '') 
{
	require_once '/home/a9208348/public_html/DB_Connect.php';
        // connecting to database
	$db = new DB_Connect();
        $db->connect();
 
	$tag = $_POST['tag'];
    	$subject= $_POST['subject'];
	$business_name = $_POST['businessName'];
	$message= $_POST['message'];
	$DATE= $_POST['date'];
	$result = mysql_query("INSERT INTO events(business, subject, message, date) VALUES('$business_name', '$subject', '$message', '$DATE')");
	
}
?>