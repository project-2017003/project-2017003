<?php
$email = $_POST["Email"];
$numb = $_POST["Numb"];


//$email = "hemanth.kandula@gmail.com";
//$numb = "79";
$placeID = "yoo";


    include_once 'db_functions.php';
    $db = new DB_Functions();


    $rest = $db->insertplace($email,$numb,$placeID);
	
	

 
    if ($rest != false){
        
    echo "true";
}
else{
	
	echo "false";
}
?>	