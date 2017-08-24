<?php
include_once './db_functions.php';

$db = new DB_Functions(); 



$json = $_POST["Cart"];
//$json =[];

$Email = $_POST["Email"];







if (get_magic_quotes_gpc()){
$json = stripslashes($json);
}

$data = json_decode($json);

$a=array();
$b=array();

for($i=0; $i<count($data) ; $i++)
{   

$res = $db->Updatecart($Email,$data[$i]->ItemID,$data[$i]->Quant);
	

	
}	
	
	$cartitems = $db->getcart();

    $a = array();
    $b = array();
    if ($cartitems != false){

        while ($row = mysqli_fetch_array($cartitems)) {


     
            $b["ItemID"] = $row["ItemID"];
            $b["Quant"] = $row["Quant"];

         $result= $db->getItemDetails($row["ItemID"]);

         if ($result!= false){

        while ($row = mysqli_fetch_array($result)) {

          $b["Name"] = $row["Name"];

            $b["ItemName"] =  $row["ItemName"];

            $b["Price"] = $row["price"];
            $b["itemdetails"] = $row["itemdetails"];



//$b["URL"] = $row["URL"];



}}



            array_push($a,$b);



        }
	
	
}

echo json_encode($a);
?>					