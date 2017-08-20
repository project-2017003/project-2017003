# project-2017003


Described app flow based on Appearance.
### MyApplication.java 

to Find data and wifi is  connected  or not


### SplashActivity.java

    opens with ImageView with app Icon followed by video View with circular reveal animation .
    This activity will have set delivary location button.onClick button goes to app Intro if its 1st start of the app else
    it will requests user to set the location and then latitude and longitude ,Address is stored in shared preferences
    on fail to aquire the location user can retry the same act . On completion will go to Main Activity.
    
    
### MainActivity.java
    BottomNavigation with 
    
    Home ,Search,Cart,Account item Buttons Launches respective Fragments
    
#### HomeFragment.java

     Custom toolbar which will  have user address and clickable to change the address.followed by another toolbar to sort restaurents.
     Horizantal recycler Card views for Most popular and User Fav restarant. followed by Vertical Recycler view for remaining restarents.
     
#### SearchFragment.java
      (TODO)
     Searchs the items or restaurents in the user location and displayed in recycler grid view
#### OrdersFragment.java
       (TODO)
     Having the Pageviewr of two tabs. 
     UPCOMING-- upcomming orders 
     COMPLETED -- Completed orders
     
#### AccountFragment.java 
    (Not completed TODO )
     ```
     Profile editing ,User addresses adding ,Liked restaurents,Log out option 
     
     ```
    
    
### RestarentActivity.java 
    CollapsingToolbarLayout with Image view of Restaurant image. Card view Restarent name and top ItemNames of it 
    
    follwer by View Pager with 3 timings -- breakfast , lunch ,dinner 
     each will have RestaurantTimeFragment
     
#### RestaurantTimeFragment.java 
      will get Restaurent detail from the RestaurentActivity.java and displays items list with respective catergory 
      by using SectionedRecyclerViewAdapter
      
      and can add items to cart inside recycler view
      and if anyquantity is there in cart bottom button to goto cart will be displayed and with that can goto cartActivity.java
      
      
      Sqlite  is used to store the cart offline details on checkout will be updated online
      
 ### CartActivity.java
      Retrives cart details of perticular user , user can modify the cart from here 
      
