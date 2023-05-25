--
--  This sample demonstrates how to Consume/Get messages from a Transaction Event Queue Topic using ORDS REST API
--  Note : Topic and Consumer should be created before this 

HOST=<Database host Name>
PORT=<ORDS proxy port>
DBNAME=<Database Name>
USER=<Database User Name. This is the user that ORDS configured with >
PWD=<Database User Password>

echo "Get messages for Consumer Instance"
curl -k -u $USER:$PWD --location "https://$HOST:$PORT/ords/$USER/_/db-api/stable/database/txeventq/consumers/sales_json_consumer/instances/sales_consumer_instance/records"
echo " "
