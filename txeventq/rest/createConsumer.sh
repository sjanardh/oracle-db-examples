--  This file creates a Oracle Transaction Event Queue Consumer with ORDS REST APIs
--  The first step needed is to create a Consumer Group. Followed by create a Consumer

HOST=<Database host Name>
PORT=<ORDS proxy port>
DBNAME=<Database Name>
USER=<Database User Name. This is the user that ORDS configured with >
PWD=<Database User Password>

#Create a new Consumer-Group "sales_json_consumer"
echo "Create a new Consumer-Group for weekly_sales"
curl -k -u $USER:$PWD --request POST --location https://$HOST:$PORT/ords/$USER/_/db-api/stable/database/txeventq/clusters/$DBNAME/consumer-groups/sales_json_consumer/ --header 'Content-Type:application/json' --data-binary '{"topic_name": "weekly_sales"}'
echo " "

#Create a new Consumer Instance on Consumer-Group. This is a NOOP for TEQ. returns Consumer-Group name as Instance Name
echo "Create a new Consumer Instance on Consumer-Group sales_json_consumer"
curl -k -u $USER:$PWD --location --request POST https://$HOST:$PORT/ords/$USER/_/db-api/stable/database/txeventq/consumers/sales_json_consumer/  --header 'Content-Type: application/json' --data '{"name": "sales_consumer_instance"}'
echo " "

