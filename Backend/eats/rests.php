<?php

$lat = $_POST["Latitude"];
$longi = $_POST["Longitude"];
$Email= $_POST["Email"];

$PhoneNO= $_POST["PhoneNo"];
$lat = 10.730328;
$longi= 79.009439; // remove these for location wise functionality

    include_once 'db_functions.php';
    $db = new DB_Functions();


    $rest = $db->getrest();

    $a = array();
    $b = array();
    if ($rest != false){
        while ($row = mysqli_fetch_array($rest)) {

$dist = distance($lat, $longi, $row["Lat"], $row["Longi"]);

if($dist < 5 ){


if($dist < .5 ){             $b["Time"] = "10-20 mins";
  }
elseif($dist < 1.5 ){ $b["Time"] = "20-30 mins";}
elseif($dist < 3 ){ $b["Time"] = "30-40 mins";}
elseif($dist < 4 ){ $b["Time"] = "30-40 mins";}

else { $b["Time"] = "40-60 mins";}

     
            $b["Name"] = $row["Name"];
            $b["URL"] = $row["URL"];

            $b["Lat"] = $row["Lat"];

            $b["Longi"] = $row["Longi"];

            $b["dist"] = $dist;


            $b["PlaceID"] = $row["PlaceID"];

            $b["RestID"] = $row["RestID"];


            array_push($a,$b);


}
        }
        echo json_encode($a,JSON_UNESCAPED_SLASHES);
    }



	
function distance($lat1, $lon1, $lat2, $lon2) {

  $theta = $lon1 - $lon2;
  $dist = sin(deg2rad($lat1)) * sin(deg2rad($lat2)) +  cos(deg2rad($lat1)) * cos(deg2rad($lat2)) * cos(deg2rad($theta));
  $dist = acos($dist);
  $dist = rad2deg($dist);
  $miles = $dist * 60 * 1.1515;

  
  return ($miles * 1.609344);
  
}

?>	