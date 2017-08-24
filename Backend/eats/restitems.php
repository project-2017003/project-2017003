<?php

$RestID = $_POST["RestID"];
//$RestID = '1';


    include_once 'db_functions.php';
    $db = new DB_Functions();


    $restitems = $db->getrestitems($RestID);

    $a = array();
    $b = array();
    if ($restitems != false){

        while ($row = mysqli_fetch_array($restitems)) {


     
            $b["ID"] = $row["ID"];
            $b["RestID"] = $row["RestID"];

            //$b["Cat"] = $row["Cat"];





            $b["Name"] = $row["Name"];

            $b["ItemName"] =  $row["ItemName"];


            $b["top5"] = $row["top5"];

            $b["TIME"] = $row["TIME"];

            $b["Price"] = $row["price"];
            $b["itemdetails"] = $row["itemdetails"];

            $b["URL"] = $row["URL"];





 $Name= $db->getcatname($row["Cat"]);

             if( $Name)    
{      $row = mysqli_fetch_array($Name);
              $b["Cat"] = $row["CAT"];

              




}
else{  $b["Cat"]= ' ';}


            array_push($a,$b);


        }
        echo json_encode($a,JSON_UNESCAPED_SLASHES);
    }





?>	