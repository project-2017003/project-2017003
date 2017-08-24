
<?php

$email = $_POST["Email"];
$numb = $_POST["Numb"];
$name=$_POST["Name"];
$placeid = $_POST["PlaceID"];


    include_once 'db_functions.php';
    $db = new DB_Functions();


    $user = $db->adduser($email,$numb,$name,$placeid);

if($user){echo "true";}
else {echo "false";}
    

?>	