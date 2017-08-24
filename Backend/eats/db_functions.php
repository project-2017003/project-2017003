<?php

class DB_Functions {

    private $db;

    //put your code here
    // constructor
    function __construct() {
        include_once './db_connect.php';
        // connecting to database
        $this->db = new DB_Connect();
        $this->db->connect();
    }

    // destructor
    function __destruct() {
        
    }

    







public function getrest() {
        $result = mysqli_query($this->db->connect(),"SELECT * FROM `Restaurants` " );

        //echo $result;
        return $result;
    }


public function getrestitems($RestID) {
        $result = mysqli_query($this->db->connect(),"SELECT * FROM `RestItems` WHERE RestID = '$RestID' " );

        //echo $result;
        return $result;
    }













public function adduser($EmailId,$Phone,$userName,$placeid) {  
        // Insert user into database
        $result = mysqli_query($this->db->connect(),"INSERT INTO `Customer` ( `Firstname`, `PhoneNO`, `Email`, `PlaceID`) 
		VALUES ( '$userName ',  '$Phone', '$EmailId', '$placeid');");
 if ($result) {


			return true;
        } else {
			if( mysqli_connect_errno()== 1062) {






				return true;
			} else {
				// For other errors
				return false;
			}            
        }
        
    }



	
public function getplaceid($Email, $numb){

        $result = mysqli_query($this->db->connect(),"SELECT * FROM `Customer` WHERE Email = '$Email' OR PhoneNO = '$numb'" );

        //echo $result;
		// $result = mysqli_query($this->db->connect(),);
        return $result;
    }

public function	insertplace($email,$numb,$placeID){
	$result = mysqli_query($this->db->connect(),"UPDATE `Customer` SET  `PlaceID`= '$placeID' WHERE Email = '$Emaid' OR PhoneNO = '$numb'" );

        
        return $result;
	
}	




      public function Updatecart($EmailPhone,$ItemID,$Quant) {

 $result = mysqli_query($this->db->connect(),"INSERT INTO `Cart`(ItemID,Quant,UserID)VALUES('$ItemID','$Quant','$EmailPhone') ON DUPLICATE KEY UPDATE Quant = '$Quant' "); 

 

		       
 if ($result) {


			return true;
        } else {
			if( mysqli_connect_errno()== 1062) {

				return true;
			} else {
				// For other errors
				return false;
			}            
        }
    }

public function getUserID($EmailPhone){


$result = mysqli_query($this->db->connect(),"SELECT ID FROM `Customer` WHERE Email = '$EmailPhone' or PhoneNO = '$EmailPhone'" );

        //echo $result;
		// $result = mysqli_query($this->db->connect(),);
        return $result;
    }




public function getItemDetails($ItemID){


$result = mysqli_query($this->db->connect(),"SELECT * FROM `RestItems` WHERE ID= '$ItemID' " );

        //echo $result;
		// $result = mysqli_query($this->db->connect(),);
        return $result;
    }






public function getcart() {
        $result = mysqli_query($this->db->connect(),"SELECT * FROM `Cart` WHERE UserID <> '' and UserID IS NOT NULL and Quant > '0' " );

        //echo $result;
        return $result;
    }



public function getcats(){

        $result = mysqli_query($this->db->connect(),"SELECT * FROM `Cat` " );

        //echo $result;
		// $result = mysqli_query($this->db->connect(),);
        return $result;
    }	




public function getrestwithcat($CATID){

        $result = mysqli_query($this->db->connect(),"SELECT DISTINCT  RestID FROM `RestItems` WHERE cat = '$CATID' " );

        //echo $result;
		// $result = mysqli_query($this->db->connect(),);
        return $result;
    }	




public function getrestname($RestID) {
        $result = mysqli_query($this->db->connect(),"SELECT Name,URL FROM `Restaurants` where RestID = '$RestID' " );

        //echo $result;
        return $result;
    }






public function getcatname($cat) {
        $result = mysqli_query($this->db->connect(),"SELECT CAT FROM `Cat` where ID = '$cat' " );

        //echo $result;
        return $result;
    }




























}














?>						