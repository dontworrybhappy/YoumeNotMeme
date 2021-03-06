source ./APIKEY.sh

curl -X POST \
  -F "name=memes" \
  -F "aliens_positive_examples=@images/aliens.zip" \
  -F "goodGuyGreg_positive_examples=@images/goodGuyGreg.zip" \
  -F "jDawg_positive_examples=@images/jDawg.zip"   \
  -F "mostInterestingManInTheWorld_positive_examples=@images/mostInterestingManInTheWorld.zip"   \
  -F "scumbagSteve_positive_examples=@images/scumbagSteve.zip" \
  -F "successKid_positive_examples=@images/successKid.zip" \
  -F "wat_positive_examples=@images/wat.zip" \
  "https://gateway-a.watsonplatform.net/visual-recognition/api/v3/classifiers?api_key=$APIKEY&version=2016-05-20"
