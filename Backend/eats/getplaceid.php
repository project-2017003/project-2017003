<?php
$email = $_POST["Email"];
$numb = $_POST["Numb"];


//$email = "10";
//$numb = "79";


    include_once 'db_functions.php';
    $db = new DB_Functions();


    $rest = $db->getplaceid($email,$numb);

 $a = array();
    $b = array();
    if ($rest != false){
        while ($row = mysqli_fetch_array($rest)) {

		
		
		echo $row["PlaceID"];

          //  $b["PlaceID"] = $row["PlaceID"];


            array_push($a,$b);



        }
       // echo json_encode($a,JSON_UNESCAPED_SLASHES);
    
}
?>	