--
--  This sample demonstrates how to create a Transaction Event Queue Topic using ORDS REST API
--

HOST=<Database host Name>
PORT=<ORDS proxy port>
DBNAME=<Database Name>
USER=<Database User Name. This is the user that ORDS configured with >
PWD=<Database User Password>

echo "Create Topic weekly_sales"
curl -k -u $USER:$PWD https://$HOST:$PORT/ords/$USER/_/db-api/stable/database/txeventq/clusters/$DBNAME/topics/ --header 'Content-Type: application/json' --data-binary '{"topic_name": "weekly_sales"}' 
echo " "
