<?php
include_once './db_functions.php';

$db = new DB_Functions(); 






//$json_string = 'http://carpe16.esy.es/carpe16/sync/post.json';
//$json = file_get_contents($json_string);

$EmailPhone= 'h';
$ItemID= '3';

$Quant= '99';


echo $ItemID;

$a=array();
$b=array();

  

$res = $db->Updatecart($EmailPhone,$ItemID,$Quant) ;
	
	if($res){
		$b["ItemID"] = $ItemID;
		$b["Quant"] = $Quant;

		

         
      


                 
		$b["status"] = 'yes';
		array_push($a,$b);
		
		
	}else{
		$b["ItemID"] = $ItemID;
		
		$b["status"] = 'no';
		array_push($a,$b);
	}


echo json_encode($a);
?>					