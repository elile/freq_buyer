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
	$business_name = $_POST['businessName'];
	$result = mysql_query("SELECT message,subject,date FROM events WHERE business='$business_name'");
	
	$arr = array();

	while($obj = mysql_fetch_object($result )) {
		$arr[] = $obj;
	}
	echo '{"events":'.json_encode($arr).'}';

	$db->close();


}
?>