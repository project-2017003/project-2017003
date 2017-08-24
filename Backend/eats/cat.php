<?php

    include_once 'db_functions.php';
    $db = new DB_Functions();


    $rest = $db->getcats();

    $a = array();
    $b = array();
    if ($rest != false){
        while ($row = mysqli_fetch_array($rest)) {






            $b["CAT"] = $row["CAT"];
            $b["URL"] = $row["URL"];


            $b["ID"] = $row["ID"];

 


            array_push($a,$b);


}
        }
        echo json_encode($a,JSON_UNESCAPED_SLASHES);
    




?>	