source APIKEY.sh

if [ -z "$1" ]; then
  IMAGE="Aliens.jpg"
else
  IMAGE=$1
fi

curl -X POST \
--form "images_file=@$IMAGE" \
--form "parameters=@metadata.json" \
--form "threshold=0.0001" \
"https://gateway-a.watsonplatform.net/visual-recognition/api/v3/classify?api_key=$APIKEY&version=2016-05-20"
