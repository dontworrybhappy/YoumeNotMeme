source APIKEY.sh

curl -X POST -F "name=memes" "https://gateway-a.watsonplatform.net/visual-recognition/api/v3/classifiers?api_key=$APIKEY&version=2016-05-20"
