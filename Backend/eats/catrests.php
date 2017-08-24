<?php

$CATID= $_POST["CATID"];
//$CATID = '1';


    include_once 'db_functions.php';
    $db = new DB_Functions();


    $restitems = $db->getrestwithcat($CATID);

    $a = array();
    $b = array();
    if ($restitems != false){

        while ($row = mysqli_fetch_array($restitems)) {


     
            $b["RestID"] = $row["RestID"];

           

 $Name= $db->getrestname($b["RestID"]);

             if( $Name)    
{      $row = mysqli_fetch_array($Name);
              $b["NAME"] = $row["Name"];

              $b["URL"] = $row["URL"];




}
else{  $b["Name"]= ' ';}



            array_push($a,$b);


        }
        echo json_encode($a,JSON_UNESCAPED_SLASHES);
    }





?>	