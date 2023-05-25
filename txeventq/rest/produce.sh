--
--  This sample demonstrates how to publish messages in Transaction Event Queue Topic using ORDS REST API
--

HOST=<Database host Name>
PORT=<ORDS proxy port>
DBNAME=<Database Name>
USER=<Database User Name. This is the user that ORDS configured with >
PWD=<Database User Password>

curl  -k -u $USER:$PWD --X POST --location https://$HOST:$PORT/ords/$USER/_/db-api/stable/database/txeventq/topics/weekly_sales/ -H "Content-Type: application/vnd.kafka.json.v2+json" --data '{"records":[{"key":"week1","value":"10000"},{"key":"week2","value":"5000"},{"key":"week3","value":"8000"}]}'
