source APIKEY.sh
classifierID="memes_1317972261"

curl -X GET \
"https://gateway-a.watsonplatform.net/visual-recognition/api/v3/classifiers/$classifierID?api_key=$APIKEY&version=2016-05-20"
