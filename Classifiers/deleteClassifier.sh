source ./APIKEY.sh

curl -X DELETE "https://gateway-a.watsonplatform.net/visual-recognition/api/v3/classifiers/$CLASSIFIER?api_key=$APIKEY&version=2016-05-20"