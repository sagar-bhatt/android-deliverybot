CS646: Android project
------------------------
Developed by: Sagar Bhatt
RedID: 820911696
------------------------
Application: DeliveryBot
-------------------------
Test with: Nexus 5 / API 22
Supports only portrait mode
No support for tablets
Permissions: INTERNET, ACCESS_NETWORK_STATE, ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION, WRITE_EXTERNAL_STORAGE

-------------------------
Instructions to run the app:
1. Google Directions API: replace with your API key in manifest file in "DIRECTIONS_API_KEY" meta-data tag
2. Google Maps API: replace with your API key

Other details
1. App contains single admin profile with following login details:
admin@sdsu.edu/password
2. Driver detail with existing data for test:
samuel@sdsu.edu/samuel123
3. Though itinerary visualization is dynamic, I suggest using job allocations in the same city for any particular driver.

-------------------------
Functions:
Admin user
1. Create new jobs
2. Locate delivery location using map or search box
3. Visualize all drivers routes/itinerary
4. All job list view
5. Contact customer (phone number)

Drivers
1. Register new driver
2. View job lists
3. Visualize delivery route
4. Take customer signature and mark order as a deliver
5. Save signature to Download folder in media gallery
6. Cancel order, if customer is returning the order at the door
7. Contact customer

-------------------------
Data storage:
App uses firebase database and authentication. 
Database access rules are currently set to public. 

-------------------------
App description: Targeted to local shipping carrier companies for delivery of goods.
Admin user creates and assigns job to drivers.
Driver will use itinerary map for directions, take customer e-signature to mark a delivery.

